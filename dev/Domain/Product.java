package Domain;

public class Product {
    private int product_id;
    private String product_name;
    private String expiring_date;
    private String section;
    private String stored; //interior or warehouse
    private boolean isDefect;
    private Classification classification;


    public Product(){
        isDefect = false;
    }

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String name) {
        this.product_name = name;
    }

    public String getExpiringDate() {
        return expiring_date;
    }

    public void setExpiringDate(String expiring_date) {
        this.expiring_date = expiring_date;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStored() {
        return stored;
    }

    public void setStored(String stored) {
        this.stored = stored;
    }

    public boolean isDefect() {
        return isDefect;
    }

    public void setDefect(boolean defect) {
        isDefect = defect;
    }


    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }


}
