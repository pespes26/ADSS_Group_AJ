package Domain;

import java.util.HashMap;

public class Supplier {
    private HashMap<Integer, Agreement>  agreements;
    String supplierName;
    int supplier_id;
    int company_id;
    int bankAccount;
    String paymentMethod;
    int phoneNumber;
    String email;

    public Supplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email, HashMap<Integer, Agreement> agreements){
        this.supplierName = supplierName;
        this.supplier_id = supplier_id;
        this.company_id = company_id;
        this.bankAccount = bankAccount;
        this.paymentMethod = paymentMethod;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.agreements = (agreements != null) ? agreements : new HashMap<>(); //if (agreements != null)  -> do agreements

    }


    //set agreement//
    public void setAgreement(Agreement agreement) {
        if(agreement != null){
            agreements.put(agreement.getAgreementID(), agreement);
        }
    }

    public int getBankAccount() {
        return bankAccount;
    }

    public int getCompany_id() {
        return company_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public Agreement getAgreement(int agreement_ID) {
        return agreements.get(agreement_ID);
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void removeAgreement(int agreement_ID){ //
        Agreement agreement = agreements.remove(agreement_ID);
    }




}
