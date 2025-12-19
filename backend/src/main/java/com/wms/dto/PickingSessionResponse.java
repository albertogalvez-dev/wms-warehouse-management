package com.wms.dto;

import java.util.List;

public class PickingSessionResponse {

    public enum Mode {
        EXPECT_LOCATION,
        EXPECT_PRODUCT,
        EXPECT_TOTE
    }

    public static class Candidate {
        private String toteBarcode;
        private Long orderId;
        private String externalRef;
        private int remainingQtyForThisCombo;

        public Candidate(String toteBarcode, Long orderId, String externalRef, int remainingQtyForThisCombo) {
            this.toteBarcode = toteBarcode;
            this.orderId = orderId;
            this.externalRef = externalRef;
            this.remainingQtyForThisCombo = remainingQtyForThisCombo;
        }

        public String getToteBarcode() {
            return toteBarcode;
        }

        public Long getOrderId() {
            return orderId;
        }

        public String getExternalRef() {
            return externalRef;
        }

        public int getRemainingQtyForThisCombo() {
            return remainingQtyForThisCombo;
        }
    }

    public static class ScanResult {
        private String status; // OK or ERROR
        private String message;

        public ScanResult(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class Progress {
        private int waveRemainingLines;
        private int waveDoneLines;

        public Progress(int waveRemainingLines, int waveDoneLines) {
            this.waveRemainingLines = waveRemainingLines;
            this.waveDoneLines = waveDoneLines;
        }

        public int getWaveRemainingLines() {
            return waveRemainingLines;
        }

        public int getWaveDoneLines() {
            return waveDoneLines;
        }
    }

    private Long sessionId;
    private Long waveId;
    private String status;
    private Mode mode;
    private String currentLocationCode;
    private String currentSku;
    private List<String> nextLocations;
    private List<Candidate> candidates;
    private ScanResult lastScanResult;
    private Progress progress;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getWaveId() {
        return waveId;
    }

    public void setWaveId(Long waveId) {
        this.waveId = waveId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getCurrentLocationCode() {
        return currentLocationCode;
    }

    public void setCurrentLocationCode(String currentLocationCode) {
        this.currentLocationCode = currentLocationCode;
    }

    public String getCurrentSku() {
        return currentSku;
    }

    public void setCurrentSku(String currentSku) {
        this.currentSku = currentSku;
    }

    public List<String> getNextLocations() {
        return nextLocations;
    }

    public void setNextLocations(List<String> nextLocations) {
        this.nextLocations = nextLocations;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public ScanResult getLastScanResult() {
        return lastScanResult;
    }

    public void setLastScanResult(ScanResult lastScanResult) {
        this.lastScanResult = lastScanResult;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}

