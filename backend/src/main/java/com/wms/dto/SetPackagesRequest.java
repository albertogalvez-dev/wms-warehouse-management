package com.wms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SetPackagesRequest {

    @NotNull(message = "Package count is required")
    @Min(value = 1, message = "Package count must be at least 1")
    @Max(value = 20, message = "Package count must not exceed 20")
    private Integer packageCount;

    // Getters and Setters
    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }
}
