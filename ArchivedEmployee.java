package DomainLayer;
import java.util.Date;
public class ArchivedEmployee {
    private Employee employee;
    private Date terminationDate;

    public ArchivedEmployee(Employee employee, Date terminationDate) {
        this.employee = employee;
        this.terminationDate = terminationDate;
    }
}
