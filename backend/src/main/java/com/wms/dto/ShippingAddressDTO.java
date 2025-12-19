package com.wms.dto;

import com.wms.entity.ShippingAddress;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ShippingAddressDTO {

    @NotBlank(message = "Shipping name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @Size(max = 50, message = "Phone must be at most 50 characters")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @NotBlank(message = "Address1 is required")
    @Size(max = 255, message = "Address1 must be at most 255 characters")
    private String address1;

    @Size(max = 255, message = "Address2 must be at most 255 characters")
    private String address2;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must be at most 20 characters")
    private String postalCode;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @Size(max = 100, message = "Province must be at most 100 characters")
    private String province;

    @NotBlank(message = "Country is required")
    @Size(max = 10, message = "Country must be at most 10 characters")
    private String country = "ES";

    public ShippingAddressDTO() {
    }

    public static ShippingAddressDTO fromEntity(ShippingAddress address) {
        if (address == null)
            return null;
        ShippingAddressDTO dto = new ShippingAddressDTO();
        dto.setName(address.getName());
        dto.setPhone(address.getPhone());
        dto.setEmail(address.getEmail());
        dto.setAddress1(address.getAddress1());
        dto.setAddress2(address.getAddress2());
        dto.setPostalCode(address.getPostalCode());
        dto.setCity(address.getCity());
        dto.setProvince(address.getProvince());
        dto.setCountry(address.getCountry());
        return dto;
    }

    public ShippingAddress toEntity() {
        ShippingAddress address = new ShippingAddress();
        address.setName(this.name);
        address.setPhone(this.phone);
        address.setEmail(this.email);
        address.setAddress1(this.address1);
        address.setAddress2(this.address2);
        address.setPostalCode(this.postalCode);
        address.setCity(this.city);
        address.setProvince(this.province);
        address.setCountry(this.country != null ? this.country : "ES");
        return address;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
