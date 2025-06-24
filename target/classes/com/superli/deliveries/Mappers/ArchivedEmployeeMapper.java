package com.superli.deliveries.Mappers;

import com.superli.deliveries.dto.HR.ArchivedEmployeeDTO;
import com.superli.deliveries.dto.HR.EmployeeDTO;

public class ArchivedEmployeeMapper {
    public static ArchivedEmployeeDTO fromEmployeeDTO(EmployeeDTO employee, String archivedDate) {
        return new ArchivedEmployeeDTO(
                employee.getId(),
                employee.getFullName(),
                employee.getBankAccount(),
                employee.getSalary(),
                employee.getEmploymentTerms(),
                employee.getStartDate(),
                employee.getSiteId()
        );
    }
}