package Domain;

import java.util.HashMap;

public class Supplier {
    HashMap<Integer, Agreement>  agreements;
    String supplierName;
    int supplier_id;
    int company_id;
    int bankAccount;
    String paymentMethod;
    int phoneNumber;
    String email;




    //set agreement//
    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
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
        Agreement agreement = agreements.get(agreement_ID);
        return agreement;
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
