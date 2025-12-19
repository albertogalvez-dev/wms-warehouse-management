package com.wms.dto;

import com.wms.entity.Carrier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class OrderRequest {

    @Size(max = 100, message = "External reference must be at most 100 characters")
    private String externalRef;

    @NotNull(message = "Carrier is required")
    private Carrier carrier;

    @NotNull(message = "Shipping address is required")
    @Valid
    private ShippingAddressDTO shipping;

    @NotEmpty(message = "Order must have at least one line")
    @Valid
    private List<OrderLineRequest> lines;

    // Getters and Setters
    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public ShippingAddressDTO getShipping() {
        return shipping;
    }

    public void setShipping(ShippingAddressDTO shipping) {
        this.shipping = shipping;
    }

    public List<OrderLineRequest> getLines() {
        return lines;
    }

    public void setLines(List<OrderLineRequest> lines) {
        this.lines = lines;
    }
}
