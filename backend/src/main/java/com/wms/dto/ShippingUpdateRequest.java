package com.wms.dto;

import com.wms.entity.Carrier;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ShippingUpdateRequest {

    @NotNull(message = "Carrier is required")
    private Carrier carrier;

    @NotNull(message = "Shipping address is required")
    @Valid
    private ShippingAddressDTO shipping;

    // Getters and Setters
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
}
