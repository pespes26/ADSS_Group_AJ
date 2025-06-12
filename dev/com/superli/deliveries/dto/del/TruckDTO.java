package com.superli.deliveries.dto.del;

import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Truck;

public class TruckDTO {
    private String licensePlate;
    private String model;
    private LicenseType requiredLicenseType;
    private float netWeight;
    private float maxWeight;
    private boolean available;

    public TruckDTO() {}

    public TruckDTO(String licensePlate, String model, float netWeight, float maxWeight, LicenseType requiredLicenseType) {
        this.licensePlate = licensePlate;
        this.model = model;
        this.requiredLicenseType = requiredLicenseType;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
        this.available = true;
    }

    public String getlicensePlate() { return licensePlate; }
    public String getModel() { return model; }
    public LicenseType getRequiredLicenseType() { return requiredLicenseType; }
    public float getNetWeight() { return netWeight; }
    public float getMaxWeight() { return maxWeight; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setModel(String model) { this.model = model; }
    public void setNetWeight(float netWeight) { this.netWeight = netWeight; }
    public void setMaxWeight(float maxWeight) { this.maxWeight = maxWeight; }
    public void setRequiredLicenseType(LicenseType requiredLicenseType) { this.requiredLicenseType = requiredLicenseType; }

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