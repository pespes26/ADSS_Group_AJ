package Domain;

import java.util.HashMap;

public class Supplier {
    private HashMap<Integer, Agreement>  agreements;
    String supplierName;
    int supplier_id;
    int company_id;
    int bankAccount;
    String paymentMethod;
    String paymentDay;
    int phoneNumber;
    String email;

//    public Supplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email,String paymentDay, HashMap<Integer, Agreement> agreements){
//        this.supplierName = supplierName;
//        this.supplier_id = supplier_id;
//        this.company_id = company_id;
//        this.bankAccount = bankAccount;
//        this.paymentMethod = paymentMethod;
//        this.phoneNumber = phoneNumber;
//        this.email = email;
//        this.paymentDay = paymentDay;
//        this.agreements = (agreements != null) ? agreements : new HashMap<>(); //if (agreements != null)  -> do agreements
//    }

    public Supplier(String supplierName, int supplier_id, int company_id, int bankAccount, String paymentMethod, int phoneNumber, String email,String paymentDay){
        this.supplierName = supplierName;
        this.supplier_id = supplier_id;
        this.company_id = company_id;
        this.bankAccount = bankAccount;
        this.paymentMethod = paymentMethod;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.paymentDay = paymentDay;
        this.agreements = new HashMap<>();
    }


    //set agreement//
    public void setAgreement(Agreement agreement) {//לא נראה לי קריטי
        if(agreement != null){
            agreements.put(agreement.getAgreementID(), agreement);
        }
    }

    public String getPaymentDay(){return paymentDay;}


    public void setPaymentDay(String paymentDay) {//אולי יש שכפול עם הסכם בנושא הזה
        this.paymentDay = paymentDay;
    }

    public void setEmail(String email) {//קריטי?
        this.email = email;
    }

    public void setPaymentMethod(String paymentMethod) {//לבדוק אם יש כפילות בהסכם על הנושא הזה
        this.paymentMethod = paymentMethod;
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

    public Agreement getAgreement(int agreement_ID) {//היות ויש מזהה יחודי ל"הסכם" אין צורך במתודה הזאת
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

    //==========================new=====================================
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

}
