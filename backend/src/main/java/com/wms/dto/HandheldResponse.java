package com.wms.dto;

public class HandheldResponse {
    private PickLineResponse nextOpenLine;
    private Progress progress;

    public HandheldResponse() {
    }

    public static class Progress {
        private int totalLines;
        private int doneLines;
        private int totalQtyAssigned;
        private int totalQtyPicked;

        public Progress(int totalLines, int doneLines, int totalQtyAssigned, int totalQtyPicked) {
            this.totalLines = totalLines;
            this.doneLines = doneLines;
            this.totalQtyAssigned = totalQtyAssigned;
            this.totalQtyPicked = totalQtyPicked;
        }

        // Getters
        public int getTotalLines() {
            return totalLines;
        }

        public int getDoneLines() {
            return doneLines;
        }

        public int getTotalQtyAssigned() {
            return totalQtyAssigned;
        }

        public int getTotalQtyPicked() {
            return totalQtyPicked;
        }
    }

    // Getters and Setters
    public PickLineResponse getNextOpenLine() {
        return nextOpenLine;
    }

    public void setNextOpenLine(PickLineResponse nextOpenLine) {
        this.nextOpenLine = nextOpenLine;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}
