package com.wms.dto;

import java.util.List;

public class SetPackagesResponse {

    public static class PackageInfo {
        private Long packageId;
        private int packageNo;
        private String trackingCode;
        private String labelFormat;
        private String labelZplPreview; // First 100 chars

        public PackageInfo(Long packageId, int packageNo, String trackingCode, String labelFormat, String labelZpl) {
            this.packageId = packageId;
            this.packageNo = packageNo;
            this.trackingCode = trackingCode;
            this.labelFormat = labelFormat;
            this.labelZplPreview = labelZpl != null && labelZpl.length() > 100 ? labelZpl.substring(0, 100) + "..."
                    : labelZpl;
        }

        public Long getPackageId() {
            return packageId;
        }

        public int getPackageNo() {
            return packageNo;
        }

        public String getTrackingCode() {
            return trackingCode;
        }

        public String getLabelFormat() {
            return labelFormat;
        }

        public String getLabelZplPreview() {
            return labelZplPreview;
        }
    }

    private Long shipmentId;
    private List<PackageInfo> packages;
    private String mode = "READY_TO_COMPLETE";

    public SetPackagesResponse(Long shipmentId, List<PackageInfo> packages) {
        this.shipmentId = shipmentId;
        this.packages = packages;
    }

    // Getters
    public Long getShipmentId() {
        return shipmentId;
    }

    public List<PackageInfo> getPackages() {
        return packages;
    }

    public String getMode() {
        return mode;
    }
}
