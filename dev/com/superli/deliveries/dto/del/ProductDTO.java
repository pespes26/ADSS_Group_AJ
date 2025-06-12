package com.superli.deliveries.dto.del;

public class ProductDTO {
    private String id;
    private String name;
    private String category;
    private float weight;
    private boolean available;

    public ProductDTO() {}

    public ProductDTO(String id, String name) {
        this.id = id;
        this.name = name;
        this.weight = 0f;
        this.available = true;
    }

    public ProductDTO(String id, String name, float weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.available = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}