package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.ArchivedEmployee;
import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.dto.HR.ArchivedEmployeeDTO;

import java.util.ArrayList;
import java.util.List;

public class ArchivedEmployeeMapper {

    // From DTO list to ArchivedEmployee entity
    public static ArchivedEmployee fromDTOList(List<ArchivedEmployeeDTO> dtos) {
        if (dtos == null) return null;

        List<Employee> employees = new ArrayList<>();
        for (ArchivedEmployeeDTO dto : dtos) {
            employees.add(new Employee(
                    String.valueOf(dto.getId()),
                    dto.getFullName(),
                    null,       // bankAccount
                    0.0,        // salary
                    null,       // employeeTerms
                    null,       // employeeStartDate
                    null,       // roleQualifications
                    null,       // availabilityConstraints
                    null        // loginRole
            ));
        }

        return new ArchivedEmployee(employees);
    }

    // From entity to DTO list
    public static List<ArchivedEmployeeDTO> toDTOList(ArchivedEmployee archivedEmployee, String archivedDate) {
        if (archivedEmployee == null || archivedDate == null) return null;

        List<ArchivedEmployeeDTO> dtos = new ArrayList<>();
        for (Employee e : archivedEmployee.getArchivedEmployees()) {
            dtos.add(new ArchivedEmployeeDTO(
                    Integer.parseInt(e.getId()),
                    e.getFullName(),
                    archivedDate
            ));
        }

        return dtos;
    }
}
