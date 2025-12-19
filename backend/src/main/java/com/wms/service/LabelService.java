package com.wms.service;

import com.wms.entity.ShippingAddress;
import org.springframework.stereotype.Service;

/**
 * Service to generate ZPL labels for shipping packages.
 * Generates elaborated ZPL with Code128 barcode for tracking codes.
 */
@Service
public class LabelService {

    public String generateZPL(String carrier, String orderRef, ShippingAddress shipping,
            String trackingCode, int packageNo, int packageCount) {

        StringBuilder zpl = new StringBuilder();

        // ZPL Header
        zpl.append("^XA\n"); // Start format

        // Label dimensions (4x6 inch label - standard shipping label)
        zpl.append("^PW812\n"); // Print width 812 dots (4 inches at 203dpi)
        zpl.append("^LL1218\n"); // Label length 1218 dots (6 inches at 203dpi)
        zpl.append("^LH0,0\n"); // Label home position

        // Carrier logo area (large text as placeholder)
        zpl.append("^FO50,50^A0N,80,80^FD").append(carrier).append("^FS\n");

        // Separator line
        zpl.append("^FO50,150^GB700,3,3^FS\n");

        // Shipping address section
        zpl.append("^FO50,180^A0N,35,35^FDSHIP TO:^FS\n");
        zpl.append("^FO50,230^A0N,45,45^FD").append(escapeZPL(shipping.getName())).append("^FS\n");
        zpl.append("^FO50,290^A0N,35,35^FD").append(escapeZPL(shipping.getAddress1())).append("^FS\n");

        if (shipping.getAddress2() != null && !shipping.getAddress2().isEmpty()) {
            zpl.append("^FO50,340^A0N,35,35^FD").append(escapeZPL(shipping.getAddress2())).append("^FS\n");
        }

        zpl.append("^FO50,390^A0N,35,35^FD").append(escapeZPL(shipping.getPostalCode()))
                .append("  ").append(escapeZPL(shipping.getCity())).append("^FS\n");
        zpl.append("^FO50,440^A0N,35,35^FD").append(escapeZPL(shipping.getCountry())).append("^FS\n");

        // Separator line
        zpl.append("^FO50,500^GB700,3,3^FS\n");

        // Order reference
        zpl.append("^FO50,520^A0N,30,30^FDORDER: ").append(escapeZPL(orderRef)).append("^FS\n");

        // Package info
        zpl.append("^FO50,570^A0N,40,40^FDPACKAGE ").append(packageNo)
                .append(" OF ").append(packageCount).append("^FS\n");

        // Separator line
        zpl.append("^FO50,630^GB700,3,3^FS\n");

        // Tracking label
        zpl.append("^FO50,650^A0N,30,30^FDTRACKING:^FS\n");

        // Tracking code (large text)
        zpl.append("^FO50,690^A0N,50,50^FD").append(escapeZPL(trackingCode)).append("^FS\n");

        // Code128 Barcode for tracking code
        // ^BY - Bar code field default (width,ratio,height)
        // ^BCN - Code 128 barcode (N=normal orientation, height, print interpretation
        // line, line above)
        zpl.append("^FO50,770^BY3,3,150^BCN,150,Y,N,N^FD").append(escapeZPL(trackingCode)).append("^FS\n");

        // Carrier-specific note at bottom
        zpl.append("^FO50,960^A0N,25,25^FDCarrier: ").append(carrier).append("^FS\n");

        // End format
        zpl.append("^XZ\n");

        return zpl.toString();
    }

    /**
     * Escape special ZPL characters
     */
    private String escapeZPL(String input) {
        if (input == null)
            return "";
        // Replace ZPL special characters
        return input.replace("^", "\\^")
                .replace("~", "\\~")
                .replace("\\", "\\\\");
    }
}
