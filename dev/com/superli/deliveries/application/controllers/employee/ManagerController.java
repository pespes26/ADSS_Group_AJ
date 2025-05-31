package DomainLayer;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.time.DayOfWeek;

public class ControllerManager {
    private static HRManager hrManager = new HRManager();
    public static void setHRManager(HRManager hr) {
        hrManager = hr;
    }

    public static HRManager getHRManager() {
        return hrManager;
    }

    // --------------------------------Employee Management ------------------------------------
    public static void addNewEmployee( Scanner sc) {
        HRManager hr = getHRManager();
        System.out.println("--- Add New Employee ---");

        String id;
        while (true) {
            System.out.print("Enter ID (9 digits): ");
            id = sc.nextLine().trim();
            if (!isNumeric(id) || id.length() != 9) {
                System.out.println("Invalid ID. Please enter a 9-digit numeric ID.");
                continue;
            }
            if (hr.FindEmployeeByID(id) != null) {
                System.out.println("An employee with this ID already exists. Please enter a different ID.");
                continue;
            }
            break;
        }
        System.out.print("Enter full name: ");
        String fullName = sc.nextLine();

        System.out.print("Enter bank account: ");
        String bankAccount = sc.nextLine();

        double salary = 0;
        String salaryStr;
        while (true) {
            System.out.print("Enter salary: ");
            salaryStr = sc.nextLine();
            if (isNumeric(salaryStr)) {
                salary = Double.parseDouble(salaryStr);
                break;
            } else {
                System.out.println("Invalid salary. Please enter positive numbers only.");
            }
        }

        System.out.print("Enter employment terms: ");
        String employeeTerms = sc.nextLine();

        Date employeeStartDate = new Date();

        List<Role> roleQualifications = new ArrayList<>();
        System.out.println("Enter role qualifications (type 'done' to finish):");
        while (true) {
            System.out.print("Role: ");
            String roleName = sc.nextLine();
            if (roleName.equalsIgnoreCase("done")) break;

            Role existingRole = null;
            for (Role r : hr.getAllRoles()) {
                if (r.getRoleName().equalsIgnoreCase(roleName)) {
                    existingRole = r;
                    break;
                }
            }

            if (existingRole != null) {
                roleQualifications.add(existingRole);
            } else {
                System.out.println("Invalid Role.");
            }
        }

        List<AvailableShifts> availabilityConstraints = new ArrayList<>();
        Role loginRole = new Role("");

        Employee newEmployee = new Employee(id, fullName, bankAccount, salary,
                employeeTerms, employeeStartDate, roleQualifications,
                availabilityConstraints, loginRole);

        boolean addedSuccessfully = hr.addEmployee(newEmployee);
        if (addedSuccessfully) {
            System.out.println("Employee added successfully.");
        }
    }

    public static void removeEmployeeById(Scanner sc) {
        HRManager hr = getHRManager();
        System.out.print("Enter employee ID to remove: ");
        String id = sc.nextLine();

        Employee employee = hr.FindEmployeeByID(id);

        if (employee == null) {
            System.out.println("Employee with ID " + id + " not found.");
            return;
        }

        hr.removeAndArchiveEmployee(employee);
    }

    public static void printEmployeeById(Scanner sc){
        HRManager hr = getHRManager();
        System.out.print("Enter Employee ID to display: ");
        String id = sc.nextLine();

        Employee employee = hr.FindEmployeeByID(id);

        if (employee == null) {
            System.out.println("Employee with ID " + id + " not found.");
            return;
        }
        employee.printEmployee();

    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void addNewRoleToEmployee(Scanner sc) {
        HRManager hr = getHRManager();
        System.out.print("Enter Employee ID: ");
        String id = sc.nextLine();

        Employee employee = hr.FindEmployeeByID(id);

        if (employee == null) {
            System.out.println("Employee with ID " + id + " not found.");
            return;
        }

        System.out.println("Available roles in the system:");
        hr.printRoles();

        System.out.print("Enter role name to add: ");
        String roleName = sc.nextLine().trim();

        Role foundRole = null;
        for (Role role : hr.getAllRoles()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                foundRole = role;
                break;
            }
        }

        if (foundRole == null) {
            System.out.println("Role not found in the system.");
            return;
        }

        if (employee.getRoleQualifications().contains(foundRole)) {
            System.out.println("Employee already has this role.");
        } else {
            employee.addRoleQualification(foundRole);
            System.out.println("Role added to employee successfully.");
        }
    }

    public static void editEmployee(Scanner sc) {
        HRManager hr = getHRManager();

        Employee employee = null;
        while (employee == null) {
            System.out.print("Enter Employee ID to edit (or type 'exit' to cancel): ");
            String id = sc.nextLine().trim();

            if (id.equalsIgnoreCase("exit")) {
                System.out.println("Returning to Manager Menu...");
                return;
            }

            employee = hr.FindEmployeeByID(id);

            if (employee == null) {
                System.out.println("Employee with ID " + id + " not found. Please try again.");
            }
        }

        System.out.println("--- Editing Employee: " + employee.getFullName() + " ---");

        boolean editing = true;
        while (editing) {
            System.out.println("\nSelect field to edit:");
            System.out.println("1. Full Name");
            System.out.println("2. Bank Account");
            System.out.println("3. Salary");
            System.out.println("4. Role Qualifications");
            System.out.println("0. Return to Manager Menu");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter new full name: ");
                    String fullName = sc.nextLine().trim();
                    if (!fullName.isEmpty()) {
                        employee.setFullName(fullName);
                        System.out.println("Full name updated successfully.");
                    }
                    break;
                case "2":
                    System.out.print("Enter new bank account: ");
                    String bankAccount = sc.nextLine().trim();
                    if (!bankAccount.isEmpty()) {
                        employee.setBankAccount(bankAccount);
                        System.out.println("Bank account updated successfully.");
                    }
                    break;
                case "3":
                    System.out.print("Enter new salary: ");
                    String salaryStr = sc.nextLine().trim();
                    if (isNumeric(salaryStr)) {
                        double salary = Double.parseDouble(salaryStr);
                        employee.setSalary(salary);
                        System.out.println("Salary updated successfully.");
                    } else {
                        System.out.println("Invalid salary input. Must be a number.");
                    }
                    break;
                case "4":
                    System.out.println("Current role qualifications:");
                    for (Role role : employee.getRoleQualifications()) {
                        System.out.println("- " + role.getRoleName());
                    }
                    System.out.println("Do you want to:");
                    System.out.println("1. Replace all qualifications");
                    System.out.println("2. Add a new role");
                    System.out.print("Choice: ");
                    String roleChoice = sc.nextLine().trim();

                    if (roleChoice.equals("1")) {
                        List<Role> newQualifications = new ArrayList<>();
                        while (true) {
                            System.out.print("Enter role name (or 'done' to finish): ");
                            String roleName = sc.nextLine().trim();
                            if (roleName.equalsIgnoreCase("done")) {
                                break;
                            }
                            newQualifications.add(new Role(roleName));
                        }
                        employee.setRoleQualifications(newQualifications);
                        System.out.println("Role qualifications replaced successfully.");
                    } else if (roleChoice.equals("2")) {
                        System.out.print("Enter new role name to add: ");
                        String newRoleName = sc.nextLine().trim();
                        if (!newRoleName.isEmpty()) {
                            employee.getRoleQualifications().add(new Role(newRoleName));
                            System.out.println("New role added successfully.");
                        }
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                case "0":
                    System.out.println("Returning to Manager Main Menu...");
                    editing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }



    // --------------------------------Employee Management ------------------------------------
    // --------------------------------Role And Shift Management ------------------------------------
    public static void addNewRole(Scanner sc) {
        HRManager hr = getHRManager();
        // --- Always make sure "Shift Manager" role exists before starting ---
        boolean shiftManagerExists = false;
        for (Role role : hrManager.getAllRoles()) {
            if (role.getRoleName().equalsIgnoreCase("shift manager")) {
                shiftManagerExists = true;
                break;
            }
        }
        if (!shiftManagerExists) {
            Role shiftManagerRole = new Role("shift manager");
            hrManager.addRole(shiftManagerRole);
            System.out.println("System role 'Shift Manager' added automatically.");
        }
        // ----------------------------------------------------------------------

        while (true) {
            System.out.print("Enter new role name (or type 'done' to finish): ");
            String roleName = sc.nextLine().trim();

            if (roleName.equalsIgnoreCase("done")) {
                System.out.println("---Finished adding roles---");
                break;
            }

            // Check that the role name contains only letters
            if (!roleName.matches("[a-zA-Z 0]+")) {
                System.out.println("Invalid role name. Please use letters only.");
                continue;
            }

            // Check manually if the role already exists (case-insensitive)
            boolean exists = false;
            for (Role role : hr.getAllRoles()) {
                if (role.getRoleName().equalsIgnoreCase(roleName)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                System.out.println("Role already exists.");
            } else {
                Role newRole = new Role(roleName);
                hr.addRole(newRole);
                System.out.println("Role added successfully.");
            }
        }
    }


    public static void createShiftsForTheWeek( Scanner sc) {
        HRManager hr = getHRManager();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = LocalDate.now()
                .with(DayOfWeek.THURSDAY)
                .atTime(16, 0);

        if (now.isAfter(deadline)) {
            LocalDate upcomingSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));


            List<Shift> shiftsToArchive = new ArrayList<>();
            List<Shift> existingShifts = hr.getAllShifts();

            for (Shift s : existingShifts) {

                LocalDate shiftDate = ((java.sql.Date) s.getShiftDate()).toLocalDate();

                if (shiftDate.isBefore(upcomingSunday)) {
                    shiftsToArchive.add(s);
                }
            }

            if (!shiftsToArchive.isEmpty()) {
                hr.getArchivedShifts().archiveShift(shiftsToArchive);
                existingShifts.removeAll(shiftsToArchive);
            }


            boolean alreadyCreated = false;
            for (Shift s : existingShifts) {
                LocalDate shiftDate = ((java.sql.Date) s.getShiftDate()).toLocalDate();
                if (!shiftDate.isBefore(upcomingSunday)) {
                    alreadyCreated = true;
                    break;
                }
            }

            if (alreadyCreated) {
                System.out.println("Shifts for next week already exist. Cannot create again.");
                return;
            }

            for (int i = 0; i < 7; i++) {
                LocalDate shiftDate = upcomingSunday.plusDays(i);
                DomainLayer.DayOfWeek day = DomainLayer.DayOfWeek.valueOf(shiftDate.getDayOfWeek().toString());

                Date date = java.sql.Date.valueOf(shiftDate);

                Shift morningShift = new Shift(date, ShiftType.MORNING, day, new ArrayList<>(), new HashMap<>(), null);
                Shift eveningShift = new Shift(date, ShiftType.EVENING, day, new ArrayList<>(), new HashMap<>(), null);

                hr.addShift(morningShift);
                hr.addShift(eveningShift);
            }

            System.out.println("Shifts for next week were created (after Thursday 16:00).");

        } else {
            System.out.println("Shifts can only be created after Thursday at 16:00.");
        }
    }


    public static void defineRolesForSpecificShift(Scanner sc) {
        HRManager hr = getHRManager();
        List<Shift> shifts = hr.getAllShifts();
        if (shifts.isEmpty()) {
            System.out.println("No shifts available.");
            return;
        }


        System.out.println("All shifts for next week:");
        for (int i = 0; i < shifts.size(); i++) {
            Shift s = shifts.get(i);
            System.out.println(i+1 + ". " + s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
        }
        int index;
        Shift selectedShift;
        while (true) {
            System.out.print("Choose a shift to define required roles:");
            index = Integer.parseInt(sc.nextLine());
            if (index < 0 || index > shifts.size()) {
                System.out.println("Invalid shift index.");
                continue;
            }
            selectedShift = shifts.get(index-1);
            break;
        }


        List<Role> rolesToAssign = new ArrayList<>();
        Role shiftManager = new Role("Shift manager");
        rolesToAssign.add(shiftManager);
        System.out.println("Enter required roles for this shift (type 'done' to finish):");

        while (true) {
            System.out.print("Role: ");
            String roleInput = sc.nextLine();

            if (roleInput.equalsIgnoreCase("done")) break;

            Role found = null;
            for (Role r : hr.getAllRoles()) {
                if (r.getRoleName().equalsIgnoreCase(roleInput)) {
                    found = r;
                    break;
                }
            }

            if (found != null) {
                if (!rolesToAssign.contains(found)) {
                    rolesToAssign.add(found);
                    System.out.println("Role added.");
                } else {
                    System.out.println("Role already added.");
                }
            } else {
                System.out.println("Role not found in system.");
            }
        }

        hr.defineRequiredRolesForShift(selectedShift, rolesToAssign);
        System.out.println("Roles updated for shift on " + selectedShift.getShiftDate() + ".");
    }
    // --------------------------------Role And Shift Management ------------------------------------
    public static void assignEmployeeToShift(Scanner sc) {
        HRManager hr = getHRManager();

        List<Shift> shifts = hr.getAllShifts();
        if (shifts.isEmpty()) {
            System.out.println("No shifts available.");
            return;
        }

        System.out.println("Choose a shift:");
        for (int i = 0; i < shifts.size(); i++) {
            Shift s = shifts.get(i);
            System.out.println((i + 1) + ". " + s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
        }

        int shiftIndex = Integer.parseInt(sc.nextLine());
        if (shiftIndex < 1 || shiftIndex > shifts.size()) {
            System.out.println("Invalid shift selection.");
            return;
        }
        Shift selectedShift = shifts.get(shiftIndex - 1);

        List<Role> requiredRoles = new ArrayList<>(selectedShift.getShiftRequiredRoles());
        if (requiredRoles.isEmpty()) {
            System.out.println("No required roles defined for this shift. Please define roles before assigning employees.");
            return;
        }

        System.out.println("\nRequired roles for this shift:");
        for (int i = 0; i < requiredRoles.size(); i++) {
            System.out.println((i + 1) + ". " + requiredRoles.get(i).getRoleName());
        }

        List<Role> unassignedRoles = new ArrayList<>(requiredRoles);
        Map<Role, Employee> assignments = new HashMap<>();
        List<Employee> assignedEmployees = new ArrayList<>();

        while (!unassignedRoles.isEmpty()) {
            System.out.println("\nRoles left to assign:");
            for (int i = 0; i < unassignedRoles.size(); i++) {
                System.out.println((i + 1) + ". " + unassignedRoles.get(i).getRoleName());
            }

            System.out.print("Choose a role to assign (or 0 to exit): ");
            int roleChoice = Integer.parseInt(sc.nextLine());
            if (roleChoice == 0) {
                System.out.println("Exiting role assignment menu.");
                return;
            }
            if (roleChoice < 1 || roleChoice > unassignedRoles.size()) {
                System.out.println("Invalid role selection.");
                continue;
            }

            Role selectedRole = unassignedRoles.get(roleChoice - 1);
            System.out.println("\nAssigning role: " + selectedRole.getRoleName());

            List<Employee> candidates = new ArrayList<>();
            for (Employee e : hr.getEmployees()) {
                for (Role r : e.getRoleQualifications()) {
                    if (r.getRoleName().equalsIgnoreCase(selectedRole.getRoleName())) {
                        if (e.isAvailable(selectedShift.getShiftDay(), selectedShift.getShiftType()) &&
                                !assignedEmployees.contains(e)) {
                            candidates.add(e);
                            break;
                        }
                    }
                }
            }

            if (candidates.isEmpty()) {
                System.out.println("No available employees qualified for role: " + selectedRole.getRoleName());
                continue;
            }

            System.out.println("Available employees for " + selectedRole.getRoleName() + ":");
            for (int i = 0; i < candidates.size(); i++) {
                Employee e = candidates.get(i);
                System.out.println((i + 1) + ". " + e.getFullName() + " (ID: " + e.getId() + ")");
            }

            System.out.print("Choose an employee to assign for this role (or 0 to skip): ");
            int empChoice = Integer.parseInt(sc.nextLine());
            if (empChoice == 0) {
                System.out.println("Skipping assignment for role: " + selectedRole.getRoleName());
                continue;
            }
            if (empChoice < 1 || empChoice > candidates.size()) {
                System.out.println("Invalid employee selection.");
                continue;
            }

            Employee selectedEmployee = candidates.get(empChoice - 1);
            assignments.put(selectedRole, selectedEmployee);
            selectedShift.addEmployeeToShift(selectedEmployee, selectedRole);

            System.out.println("Assigned " + selectedEmployee.getId() + " as " + selectedRole.getRoleName());

            assignedEmployees.add(selectedEmployee);
            unassignedRoles.remove(selectedRole);
        }

        boolean hasShiftManager = false;
        for (Role role : assignments.keySet()) {
            if (role.getRoleName().equalsIgnoreCase("Shift Manager")) {
                hasShiftManager = true;
                break;
            }
        }

        if (!hasShiftManager || assignments.size() < requiredRoles.size()) {
            System.out.println("\nWarning: The shift is **invalid** because not all required roles were assigned or a Shift Manager is missing.");
            System.out.println("The shift will not be considered valid until all mandatory roles are assigned properly.");
            assignments.clear();
        } else {
            System.out.println("\nAll required roles have been assigned successfully. The shift is now valid.");
        }
    }

    public static void removeEmployeeFromShift(Scanner sc) {
        HRManager hr = getHRManager();
        List<Shift> shifts = hr.getAllShifts();
        if (shifts.isEmpty()) {
            System.out.println("No shifts available.");
            return;
        }

        System.out.println("Choose a shift:");
        for (int i = 0; i < shifts.size(); i++) {
            Shift s = shifts.get(i);
            System.out.println(i + ". " + s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
        }

        System.out.print("Enter shift number: ");
        int shiftIndex = Integer.parseInt(sc.nextLine());
        if (shiftIndex < 0  || shiftIndex >= shifts.size()) {
            System.out.println("Invalid shift selection.");
            return;
        }

        Shift selectedShift = shifts.get(shiftIndex);

        if (selectedShift.getShiftEmployees().isEmpty()) {
            System.out.println("No employees assigned to this shift.");
            return;
        }

        List<Employee> assignedEmployees = new ArrayList<>(selectedShift.getShiftEmployees().keySet());

        System.out.println("Assigned employees:");
        for (int i = 0; i < assignedEmployees.size(); i++) {
            Employee e = assignedEmployees.get(i);
            System.out.println((i + 1) + ". (ID: " + e.getId() + ") - " + e.getFullName());
        }

        System.out.print("Choose an employee to remove: ");
        int employeeIndex = Integer.parseInt(sc.nextLine());
        if (employeeIndex < 1  || employeeIndex > assignedEmployees.size()) {
            System.out.println("Invalid employee selection.");
            return;
        }

        Employee selectedEmployee = assignedEmployees.get(employeeIndex - 1);

        hr.removeEmployeeFromShift(selectedShift, selectedEmployee);
        System.out.println("Employee removed from the shift successfully.");
    }

    public static void viewAssignedShifts() {
        HRManager hr = getHRManager();
        List<Shift> shifts = hr.getAllShifts();
        if (shifts.isEmpty()) {
            System.out.println("No shifts available.");
            return;
        }

        boolean foundAssigned = false;
        System.out.println("--- Assigned Shifts ---");

        for (Shift shift : shifts) {
            if (!shift.getShiftEmployees().isEmpty()) {
                boolean hasShiftManager = false;
                for (Role role : shift.getShiftEmployees().values()) {
                    if (role.getRoleName().equalsIgnoreCase("Shift Manager")) {
                        hasShiftManager = true;
                        break;
                    }
                }

                boolean fullyAssigned = shift.getShiftRequiredRoles().isEmpty();

                System.out.println("Shift: " + shift.getShiftDate() + " - " + shift.getShiftDay() + " - " + shift.getShiftType());

                if (!hasShiftManager || !fullyAssigned) {
                    System.out.println("*** WARNING: Shift is incomplete or missing Shift Manager! ***");
                }

                System.out.println("Assigned Employees:");
                for (Map.Entry<Employee, Role> entry : shift.getShiftEmployees().entrySet()) {
                    Employee e = entry.getKey();
                    Role r = entry.getValue();
                    System.out.println("- (ID: " + e.getId() + ") " + e.getFullName() + " | Role: " + r.getRoleName());
                }
                System.out.println();
                foundAssigned = true;
            }
        }

        if (!foundAssigned) {
            System.out.println("No employees have been assigned to any shifts yet.");
        }
    }


    public static void displayArchivedShifts() {
        HRManager hr = getHRManager();
        List<Shift> archivedShifts =hr.getArchivedShifts().getAllArchivedShifts();
        if (archivedShifts.isEmpty()) {
            System.out.println("No archived shifts available.");
            return;
        }

        System.out.println("--- Archived Shifts ---");
        for (Shift s : archivedShifts) {
            System.out.println(s.getShiftDate() + " - " + s.getShiftDay() + " - " + s.getShiftType());
        }
    }

    public static void displayArchivedEmployees() {
        HRManager hr = getHRManager();
        List<Employee> archivedEmployees = hr.getArchivedEmployee().getArchivedEmployees();
        if (archivedEmployees.isEmpty()) {
            System.out.println("No archived employees available.");
            return;
        }

        System.out.println("--- Archived Employees ---");
        for (Employee e : archivedEmployees) {
            System.out.println("Name: " + e.getFullName() + ", ID: " + e.getId());
        }
    }
}

