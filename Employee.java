package DomainLayer;

import java.util.Date;
import java.util.List;
public class Employee {
    private String id;
    private String fullName;
    private String bankAccount;
    private double salary;
    private String employeeTerms;
    private Date employeeStartDate;
    private List<Role> roleQualifications;
    private List<AvailableShifts> availabilityConstraints;
    private boolean activeWorker;

    public Employee(String id, String fullName, String bankAccount, double salary,
                    String employeeTerms, Date employeeStartDate,
                    List<Role> roleQualifications, List<AvailableShifts> availabilityConstraints,
                    boolean activeWorker) {
        this.id = id;
        this.fullName = fullName;
        this.bankAccount = bankAccount;
        this.salary = salary;
        this.employeeTerms = employeeTerms;
        this.employeeStartDate = employeeStartDate;
        this.roleQualifications = roleQualifications;
        this.availabilityConstraints = availabilityConstraints;
        this.activeWorker = activeWorker;
    }
}
