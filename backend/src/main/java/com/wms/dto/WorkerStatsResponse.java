package com.wms.dto;

import java.time.LocalDate;
import java.util.List;

public class WorkerStatsResponse {
    private LocalDate rangeStart;
    private LocalDate rangeEnd;
    private List<PickerStat> picking;
    private List<PackerStat> packing;

    public static class PickerStat {
        private String operator;
        private Long linesPicked;
        private Long lineCount;
        private Double picksPerHour;

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public Long getLinesPicked() {
            return linesPicked;
        }

        public void setLinesPicked(Long linesPicked) {
            this.linesPicked = linesPicked;
        }

        public Long getLineCount() {
            return lineCount;
        }

        public void setLineCount(Long lineCount) {
            this.lineCount = lineCount;
        }

        public Double getPicksPerHour() {
            return picksPerHour;
        }

        public void setPicksPerHour(Double picksPerHour) {
            this.picksPerHour = picksPerHour;
        }
    }

    public static class PackerStat {
        private String operator;
        private Long linesPacked;
        private Long lineCount;
        private Double packsPerHour;

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public Long getLinesPacked() {
            return linesPacked;
        }

        public void setLinesPacked(Long linesPacked) {
            this.linesPacked = linesPacked;
        }

        public Long getLineCount() {
            return lineCount;
        }

        public void setLineCount(Long lineCount) {
            this.lineCount = lineCount;
        }

        public Double getPacksPerHour() {
            return packsPerHour;
        }

        public void setPacksPerHour(Double packsPerHour) {
            this.packsPerHour = packsPerHour;
        }
    }

    public LocalDate getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(LocalDate rangeStart) {
        this.rangeStart = rangeStart;
    }

    public LocalDate getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(LocalDate rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public List<PickerStat> getPicking() {
        return picking;
    }

    public void setPicking(List<PickerStat> picking) {
        this.picking = picking;
    }

    public List<PackerStat> getPacking() {
        return packing;
    }

    public void setPacking(List<PackerStat> packing) {
        this.packing = packing;
    }
}
