package Domain;

import java.util.HashMap;

public class Supplier {
    private final HashMap<Integer, Agreement>  agreements;
    String supplierName;
    int supplier_id;
    int company_id;
    int bankAccount;
    String paymentMethod;
    String paymentCondition;
    long phoneNumber;
    String email;


    public Supplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, long phoneNumber, String email,String paymentCondition){
        this.supplierName = supplierName;
        this.supplier_id = supplier_id;
        this.company_id = company_id;
        this.bankAccount = bankAccount;
        this.paymentMethod = paymentMethod;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.paymentCondition = paymentCondition;
        this.agreements = new HashMap<>();
    }


    //set agreement//
    public void addNewAgreement(Agreement agreement) {//לא נראה לי קריטי
        if(agreement != null){
            agreements.put(agreement.getAgreementID(), agreement);
        }
    }


    public int getSupplier_id() {
        return supplier_id;
    }


    public int[] removeAllAgreementFromSupplier() {
        int size = agreements.size();
        int[] agreementIDs = new int[size];
        int i = 0;

        for (Agreement agreement : new HashMap<>(agreements).values()) {
            int agreement_ID = agreement.getAgreementID();
            agreements.remove(agreement_ID);
            agreementIDs[i++] = agreement_ID;
        }
        return agreementIDs;
    }


    public int[] getAllAgreementIDs() {
        int[] agreementIDs = new int[agreements.size()];
        int i = 0;

        for (Agreement agreement : agreements.values()) {
            agreementIDs[i++] = agreement.getAgreementID();
        }

        return agreementIDs;
    }


    // מתודה שבודקת אם יש לספק לפחות הסכם אחד
    public boolean hasAgreements() {
        return agreements != null && !agreements.isEmpty();  // אם יש לפחות הסכם אחד
    }

}
