package com.superli.deliveries.dto;

public class TruckDTO {
    private int id;
    private String model;
    private String licensePlate;

    public TruckDTO() {}

    public TruckDTO(int id, String model, String licensePlate) {
        this.id = id;
        this.model = model;
        this.licensePlate = licensePlate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
}