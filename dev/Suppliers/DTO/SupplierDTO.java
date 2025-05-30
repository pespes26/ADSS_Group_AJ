package Suppliers.DTO;

public class SupplierDTO {
    String supplierName;
    int supplier_id;
    int company_id;
    int bankAccount;
    String paymentMethod;
    String paymentCondition;
    long phoneNumber;
    String email;

    public SupplierDTO(String supplierName,  int company_id, int bankAccount, String paymentMethod, String paymentCondition, long phoneNumber, String email) {
        this.supplierName = supplierName;
        this.supplier_id = 0;
        this.company_id = company_id;
        this.bankAccount = bankAccount;
        this.paymentMethod = paymentMethod;
        this.paymentCondition = paymentCondition;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public int getBankAccount() {
        return bankAccount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentCondition() {
        return paymentCondition;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public void setBankAccount(int bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentCondition(String paymentCondition) {
        this.paymentCondition = paymentCondition;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
