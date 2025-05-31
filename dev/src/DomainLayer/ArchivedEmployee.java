package DomainLayer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArchivedEmployee {

    private List<Employee> archivedEmployees;

    public ArchivedEmployee(List<Employee> archivedEmployees) {
        this.archivedEmployees = archivedEmployees;
    }

    public ArchivedEmployee(){this.archivedEmployees = new ArrayList<>();}


    public void addArchivedEmployee(Employee employee){
        if (!archivedEmployees.contains(employee)){
            archivedEmployees.add(employee);
        }

    }

    public List<Employee> getArchivedEmployees() {
        return archivedEmployees;
    }
}
