package PresentationLayer;

import DomainLayer.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class SampleDataLoader {

    public static void loadSampleData(HRManager hrManager) {
        // יצירת תפקידים
        Role shiftManagerRole = new Role("Shift Manager");
        Role cashierRole = new Role("Cashier");
        Role bakerRole = new Role("Baker");
        Role singerRole = new Role("Singer");

        hrManager.addRole(shiftManagerRole);
        hrManager.addRole(cashierRole);
        hrManager.addRole(bakerRole);
        hrManager.addRole(singerRole);

        List<Role> aliceRoles = Arrays.asList(shiftManagerRole);
        List<Role> bobRoles = Arrays.asList(cashierRole, singerRole);
        List<Role> charlieRoles = Arrays.asList(bakerRole);
        List<Role> dianaRoles = Arrays.asList(cashierRole);

        Employee alice = new Employee("111111111", "Alice Smith", "111-222", 10000,
                "Full-Time", new Date(System.currentTimeMillis()), new ArrayList<>(aliceRoles), new ArrayList<>(), shiftManagerRole);
        Employee bob = new Employee("222222222", "Bob Johnson", "333-444", 9000,
                "Part-Time", new Date(System.currentTimeMillis()), new ArrayList<>(bobRoles), new ArrayList<>(), cashierRole);
        Employee charlie = new Employee("333333333", "Charlie Brown", "555-666", 8500,
                "Full-Time", new Date(System.currentTimeMillis()), new ArrayList<>(charlieRoles), new ArrayList<>(), bakerRole);
        Employee diana = new Employee("444444444", "Diana Prince", "777-888", 8700,
                "Part-Time", new Date(System.currentTimeMillis()), new ArrayList<>(dianaRoles), new ArrayList<>(), cashierRole);

        hrManager.addEmployee(alice);
        hrManager.addEmployee(bob);
        hrManager.addEmployee(charlie);
        hrManager.addEmployee(diana);

        createShiftsForNextWeek(hrManager);

        for (Shift shift : hrManager.getAllShifts()) {
            shift.getShiftRequiredRoles().add(shiftManagerRole);
            shift.getShiftRequiredRoles().add(cashierRole);
        }


        updateEmployeeAvailability(alice, DomainLayer.DayOfWeek.values(), ShiftType.MORNING);
        updateEmployeeAvailability(bob, DomainLayer.DayOfWeek.values(), ShiftType.EVENING);
        updateEmployeeAvailability(charlie, new DomainLayer.DayOfWeek[]{DomainLayer.DayOfWeek.TUESDAY, DomainLayer.DayOfWeek.WEDNESDAY, DomainLayer.DayOfWeek.THURSDAY}, ShiftType.MORNING);
        updateEmployeeAvailability(diana, new DomainLayer.DayOfWeek[]{DomainLayer.DayOfWeek.SUNDAY, DomainLayer.DayOfWeek.MONDAY, DomainLayer.DayOfWeek.WEDNESDAY}, ShiftType.MORNING);


        for (Shift shift : hrManager.getAllShifts()) {
            if (!shift.isEmployeeAssigned(alice) && alice.isAvailable(shift.getShiftDay(), shift.getShiftType())) {
                shift.addEmployeeToShift(alice, shiftManagerRole);
            }
            if (!shift.isShiftFullyAssigned()) {
                if (bob.isAvailable(shift.getShiftDay(), shift.getShiftType())) {
                    shift.addEmployeeToShift(bob, cashierRole);
                } else if (diana.isAvailable(shift.getShiftDay(), shift.getShiftType())) {
                    shift.addEmployeeToShift(diana, cashierRole);
                }
            }
        }
        Shift firstShift = hrManager.getAllShifts().get(0);
        firstShift.getShiftEmployees().clear();
        firstShift.getShiftRequiredRoles().clear();

        firstShift.getShiftRequiredRoles().add(shiftManagerRole);
        firstShift.getShiftRequiredRoles().add(cashierRole);

        firstShift.addEmployeeToShift(alice, shiftManagerRole);
        firstShift.addEmployeeToShift(diana, cashierRole);
    }

    private static void createShiftsForNextWeek(HRManager hrManager) {
        LocalDate upcomingSunday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        for (int i = 0; i < 7; i++) {
            LocalDate day = upcomingSunday.plusDays(i);
            DomainLayer.DayOfWeek dayEnum = mapJavaDayToDomainDay(day.getDayOfWeek());
            Date date = java.sql.Date.valueOf(day);

            Shift morningShift = new Shift(date, ShiftType.MORNING, dayEnum, new ArrayList<>(), new HashMap<>(), null);
            Shift eveningShift = new Shift(date, ShiftType.EVENING, dayEnum, new ArrayList<>(), new HashMap<>(), null);

            hrManager.addShift(morningShift);
            hrManager.addShift(eveningShift);
        }
    }

    private static void updateEmployeeAvailability(Employee employee, DomainLayer.DayOfWeek[] days, ShiftType shiftType) {
        for (DomainLayer.DayOfWeek day : days) {
            employee.getAvailabilityConstraints().add(new AvailableShifts(day, shiftType));
        }
    }

    private static DomainLayer.DayOfWeek mapJavaDayToDomainDay(java.time.DayOfWeek javaDay) {
        switch (javaDay) {
            case SUNDAY:
                return DomainLayer.DayOfWeek.SUNDAY;
            case MONDAY:
                return DomainLayer.DayOfWeek.MONDAY;
            case TUESDAY:
                return DomainLayer.DayOfWeek.TUESDAY;
            case WEDNESDAY:
                return DomainLayer.DayOfWeek.WEDNESDAY;
            case THURSDAY:
                return DomainLayer.DayOfWeek.THURSDAY;
            default:
                return null;
        }
    }
}

