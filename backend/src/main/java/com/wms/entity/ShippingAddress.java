package com.wms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ShippingAddress {

    @Column(name = "shipping_name", nullable = false)
    private String name;

    @Column(name = "shipping_phone")
    private String phone;

    @Column(name = "shipping_email")
    private String email;

    @Column(name = "shipping_address1", nullable = false)
    private String address1;

    @Column(name = "shipping_address2")
    private String address2;

    @Column(name = "shipping_postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "shipping_city", nullable = false, length = 100)
    private String city;

    @Column(name = "shipping_province", length = 100)
    private String province;

    @Column(name = "shipping_country", nullable = false, length = 10)
    private String country = "ES";

    public ShippingAddress() {
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
