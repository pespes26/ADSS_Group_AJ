package DomainLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HRManager {
    private List<Employee> employees;
    private List<Shift> shifts;
    private List<Role> allRoles;
    private ArchivedEmployee archivedEmployee;
    private ArchivedShifts archivedShifts;

    public HRManager() {
        this.employees = new ArrayList<>();
        this.shifts = new ArrayList<>();
        this.allRoles = new ArrayList<>();
        this.archivedEmployee = new ArchivedEmployee();
        this.archivedShifts = new ArchivedShifts();
    }

    /**
     * @return A list of all roles in the system.
     */
    public List<Role> getAllRoles() {
        return allRoles;
    }

    /**
     * @return A list of all shifts in the system.
     */
    public List<Shift> getAllShifts() {
        return shifts;
    }

    public List<Employee> getEmployees(){return employees;}

    public ArchivedEmployee getArchivedEmployee() {
        return archivedEmployee;
    }

    public ArchivedShifts getArchivedShifts() {
        return archivedShifts;
    }


    // ----------------------------------- Employee Management -----------------------------------
    /**
     * Adds a new employee to the employees list.
     * @param employee - the employee to add.
     */
    public boolean addEmployee(Employee employee) {
        if (FindEmployeeByID(employee.getId()) != null) {
            System.out.println("Employee with this ID already exists. Cannot add duplicate.");
            return false;
        }
        employees.add(employee);
        return true;
    }

    /**
     * Removes the given employee from the employee list if they exist.
     *
     * @param employee The employee object to remove.
     */
    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }

    /**
     * Searches for an employee by ID in the employees list.
     * @param id - the ID of the employee to search for.
     * @return if the employee was found - The matching employee
     *         otherwise - null.
     */
    public Employee FindEmployeeByID(String id) {
        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Removes the given employee from the active list, marks them as inactive, and archives them.
     *
     * @param employee - the employee to archive and remove.
     */
    public void removeAndArchiveEmployee(Employee employee) {
        Employee toRemove = FindEmployeeByID(employee.getId());

        if (toRemove == null) {
            System.out.println("Employee with ID " + employee.getId() + " not found.");
            return;
        }

        employees.remove(toRemove);
        archivedEmployee.addArchivedEmployee(toRemove);

        System.out.println("Employee with ID " + employee.getId() + " has been removed and archived.");
    }
    // ----------------------------------- End Employee Management -----------------------------------

    // ----------------------------------- Role and Shift Management -----------------------------------
    /**
     * Adds a new role if the role don't exist in the system.
     * @param role - the role to add.
     */
    public void addRole(Role role){
        if(!allRoles.contains(role)){
            allRoles.add(role);
        }
    }

    /**
     * Creates a new shift and adds it to the system if the shift don't exist.
     * @param shift - the shift to add.
     */
    public void addShift(Shift shift) {
        if(!shifts.contains(shift)) {
            shifts.add(shift);
        }
    }

    /**
     * Sets the required roles for a given shift.
     *
     * @param shift - the shift to update.
     * @param requiredRoles - the list of roles required for this shift.
     */
    public void defineRequiredRolesForShift(Shift shift, List<Role> requiredRoles) {
        shift.getShiftRequiredRoles().clear();
        shift.getShiftRequiredRoles().addAll(requiredRoles);
    }

    /**
     * Add one role for the given shift.
     * @param shift - the shift to update.
     * @param role - the role to add for this shift.
     */
    public void addRequiredRolesForShift(Shift shift, Role role) {
        shift.getShiftRequiredRoles().add(role);
    }

    /**
     * Ends the given shift and archives it the shift date has already passed.
     * Once archived, the shift is removed from the active shift list.
     *
     * @param shift - the shift to be ended and archived.
     * @param archive - The ArchivedShifts instance where the shift will be stored.
     */
   /* public void endShift(ArchivedShifts archive) {
        for ()
        if (shift.isPastShift()) {
            archive.archiveShift(shift);
            shifts.remove(shift);
        }
    }*/

    // ----------------------------------- End Role and Shift Management -----------------------------------

    // ----------------------------------- Shift Assignment -----------------------------------
    public void addEmployeeToShift(Shift shift,Employee employee){
        for (Role role : shift.getShiftRequiredRoles()) {
            if (employee.isQualified(role) && employee.isAvailable(shift.getShiftDay(),shift.getShiftType())){
                shift.addEmployeeToShift(employee,role);
                return;

                }
            }
        }

    /**
     * Removes the employee from given shift if they were assigned.
     *
     * @param shift - the shift to remove the employee from.
     * @param employee - the employee to remove from the shift.
     */
    public void removeEmployeeFromShift(Shift shift,Employee employee){
        if (shift.getShiftEmployees().containsKey(employee)){
            shift.removeEmployeeFromShift(employee);
        }

    }



    // ----------------------------------- End Shift Assignment -----------------------------------

    public void printEmployees(){
        for (Employee e: employees) {
            System.out.println("Employee full name:" + e.getFullName() +", Employee ID: "+ e.getId());
        }
    }

    public void printRoles(){
        System.out.print("Roles: [");
        for (int i=0; i<allRoles.size();i++) {
            if(i<allRoles.size()-1) {
                System.out.print(allRoles.get(i).getRoleName() + ", ");
            }
            else {
                System.out.print(allRoles.get(i).getRoleName());
            }
        }
        System.out.println("]");
    }



}
