package com.superli.deliveries.dto;

public class ZoneDTO {
    private String id;
    private String name;
    private float capacity;
    private boolean active;

    public ZoneDTO() {}

    public ZoneDTO(String id, String name, float capacity, boolean active) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getCapacity() { return capacity; }
    public void setCapacity(float capacity) { this.capacity = capacity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "ZoneDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", active=" + active +
                '}';
    }
}