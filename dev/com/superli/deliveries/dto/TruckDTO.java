package com.superli.deliveries.dto;

import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Truck;

public class TruckDTO {
    private String licensePlate;
    private String model;
    private LicenseType requiredLicenseType;
    private float netWeight;
    private float maxWeight;

    public TruckDTO() {}

    public TruckDTO(String licensePlate, String model, float netWeight, float maxWeight, LicenseType requiredLicenseType) {
        this.licensePlate = licensePlate;
        this.model = model;
        this.requiredLicenseType = requiredLicenseType;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
    }

    public String getlicensePlate() { return licensePlate; }
    public String getModel() { return model; }
    public LicenseType getRequiredLicenseType() { return requiredLicenseType; }
    public float getNetWeight() { return netWeight; }
    public float getMaxWeight() { return maxWeight; }

    public static TruckDTO toDTO(Truck truck) {
        if (truck == null) return null;

        return new TruckDTO(
                truck.getPlateNum(),
                truck.getModel(),
                truck.getNetWeight(),
                truck.getMaxWeight(),
                truck.getRequiredLicenseType()
        );
    }
}