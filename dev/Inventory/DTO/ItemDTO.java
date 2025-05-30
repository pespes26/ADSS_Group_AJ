package Inventory.DTO;

public class ItemDTO {
    private int catalog_number;
    private int item_id;
    private int branch_id;
    private String item_expiring_date;
    private String storage_location;
    private String section_in_store;
    private boolean is_defect;
    private String sale_date;


    public ItemDTO() {
    }

    public ItemDTO(int catalog_number, int item_id, int branch_id, String storage_location, String section_in_store, boolean is_defect, String item_expiring_date, String sale_date) {
        this.catalog_number = catalog_number;
        this.item_id = item_id;
        this.branch_id = branch_id;
        this.storage_location = storage_location;
        // Add a new field for section_in_store in the class if not present
        this.section_in_store = section_in_store;
        this.is_defect = is_defect;
        this.item_expiring_date = item_expiring_date;
        // Add a new field for sale_date in the class if not present
        this.sale_date = sale_date;
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

    public boolean IsDefective() {
        return is_defect;
    }

    public String getStorageLocation() {
        return storage_location;
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
}
