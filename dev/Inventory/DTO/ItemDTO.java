package Inventory.DTO;

public class ItemDTO {
    private int item_id;
    private int catalog_number;
    private int branch_id;
    private String expiration_date;
    private boolean is_defective;
    private String location;

    public ItemDTO() {
    }

    public ItemDTO(int item_id, int catalog_number, int branch_id, String expiration_date, boolean is_defective, String location) {
        this.item_id = item_id;
        this.catalog_number = catalog_number;
        this.branch_id = branch_id;
        this.expiration_date = expiration_date;
        this.is_defective = is_defective;
        this.location = location;
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

    public String getExpirationdate() {
        return expiration_date;
    }

    public boolean IsDefective() {
        return is_defective;
    }

    public String getLocation() {
        return location;
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
        this.expiration_date = expiration_date;
    }

    public void setIsDefective(boolean is_defective) {
        this.is_defective = is_defective;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
