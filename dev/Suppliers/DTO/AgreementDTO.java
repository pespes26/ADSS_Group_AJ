package Suppliers.DTO;

public class AgreementDTO {
    private final int agreement_ID; // Unique ID of the agreement
    private final int supplier_ID; // Unique ID of the supplier
    private String[] deliveryDays; // Days on which the supplier can deliver
    private boolean selfPickup; // Whether delivery is self-handled by the supermarket

    public AgreementDTO(boolean selfPickup, String[] deliveryDays, int supplier_ID, int agreement_ID) {
        this.selfPickup = selfPickup;
        this.deliveryDays = deliveryDays;
        this.supplier_ID = supplier_ID;
        this.agreement_ID = agreement_ID;
    }

    public int getAgreement_ID() {
        return agreement_ID;
    }

    public int getSupplier_ID() {
        return supplier_ID;
    }

    public String[] getDeliveryDays() {
        return deliveryDays;
    }

    public boolean isSelfPickup() {
        return selfPickup;
    }

    public void setDeliveryDays(String[] deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public void setSelfPickup(boolean selfPickup) {
        this.selfPickup = selfPickup;
    }
}
