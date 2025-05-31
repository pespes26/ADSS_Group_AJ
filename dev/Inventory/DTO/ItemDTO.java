package Inventory.DTO;

import java.time.LocalDate;

public class ItemDTO {
    private int catalog_number;
    private int item_id;
    private int branch_id;
    private String item_expiring_date;
    private LocalDate sale_date;
    private String storage_location;
    private String section_in_store;
    private boolean is_defect;



    public ItemDTO() {
    }

    public ItemDTO(int catalog_number, int branch_id, String storage_location, String section_in_store, boolean is_defect, String item_expiring_date,LocalDate item_sale_date ) {
        this.catalog_number = catalog_number;
        this.item_id = 0;
        this.branch_id = branch_id;
        this.storage_location = storage_location;
        // Add a new field for section_in_store in the class if not present
        this.section_in_store = section_in_store;
        this.is_defect = is_defect;
        this.item_expiring_date = item_expiring_date;
        this.sale_date=item_sale_date;
        // Add a new field for sale_date in the class if not present

    }

    public int getItemId() {
        return item_id;
    }

    public int getCatalogNumber() {
        return catalog_number;
    }

    public int getBranchId() {
        return branch_id;
    }

    public String getItemExpiringDate() {
        return item_expiring_date;
    }

    public LocalDate getSale_date() {
        return sale_date;
    }

    public boolean IsDefective() {
        return is_defect;
    }

    public String getStorageLocation() {
        return storage_location;
    }

    public String getSectionInStore() {
        return section_in_store;
    }

    public void setItemId(int item_id) {
        this.item_id = item_id;
    }

    public void setCatalogNumber(int catalog_number) {
        this.catalog_number = catalog_number;
    }

    public void setBranchId(int branch_id) {
        this.branch_id = branch_id;
    }

    public void setExpirationDate(String expiration_date) {
        this.item_expiring_date = expiration_date;
    }

    public void setIsDefective(boolean is_defective) {
        this.is_defect = is_defective;
    }

    public void setLocation(String location) {
        this.storage_location = location;
    }

    public void setSaleDate(LocalDate sale_date) {
        this.sale_date = sale_date;
    }

    public void setSection_in_store(String section_in_store) {
        this.section_in_store = section_in_store;
    }

}
