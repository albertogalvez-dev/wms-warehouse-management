package com.wms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Client for printing ZPL labels to Zebra printers via TCP (port 9100).
 * Configured via environment variables for Oracle VPS deployment.
 */
@Service
public class PrinterClient {

    private static final Logger log = LoggerFactory.getLogger(PrinterClient.class);

    @Value("${printer.host:}")
    private String printerHost;

    @Value("${printer.port:9100}")
    private int printerPort;

    @Value("${printer.enabled:false}")
    private boolean printerEnabled;

    @Value("${printer.fail-strict:false}")
    private boolean failStrict;

    @Value("${printer.timeout:5000}")
    private int timeoutMs;

    /**
     * Print ZPL label to configured Zebra printer.
     * 
     * @param zpl ZPL label content
     * @return null if success, error message if failed
     * @throws RuntimeException if failStrict=true and print fails
     */
    public String printLabel(String zpl) {
        if (!printerEnabled) {
            log.debug("Printer disabled, skipping print");
            return null;
        }

        if (printerHost == null || printerHost.isEmpty()) {
            String error = "Printer enabled but no host configured";
            log.warn(error);
            return handlePrintError(error);
        }

        try (Socket socket = new Socket()) {
            log.info("Connecting to printer {}:{} with timeout {}ms", printerHost, printerPort, timeoutMs);

            socket.connect(new InetSocketAddress(printerHost, printerPort), timeoutMs);
            socket.setSoTimeout(timeoutMs);

            try (OutputStream out = socket.getOutputStream()) {
                out.write(zpl.getBytes());
                out.flush();
            }

            log.info("Label printed successfully to {}:{}", printerHost, printerPort);
            return null; // Success

        } catch (IOException e) {
            String error = String.format("Failed to print to %s:%d - %s",
                    printerHost, printerPort, e.getMessage());
            log.error(error, e);
            return handlePrintError(error);
        }
    }

    private String handlePrintError(String error) {
        if (failStrict) {
            throw new RuntimeException("Printer error (strict mode): " + error);
        }
        return error; // Return error to be saved in DB
    }

    public boolean isEnabled() {
        return printerEnabled;
    }
}
