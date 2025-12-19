package com.wms.entity;

/**
 * User roles for the WMS system.
 */
public enum Role {
    ADMIN, // Full access to all operations
    MANAGER, // Manage products, locations, orders, waves
    PICKER, // Pick operations only
    PACKER // Packing operations only
}
