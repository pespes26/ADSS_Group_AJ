package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmployeeMapper {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static Employee fromDTO(EmployeeDTO dto, List<Role> roleQualifications) {
        if (dto == null) return null;
        Date startDate = null;
        try {
            startDate = dateFormat.parse(dto.getStartDate());
        } catch (ParseException e) {
            e.printStackTrace(); // או טיפול לפי הצורך
        }

        return new Employee(
                String.valueOf(dto.getId()),
                dto.getFullName(),
                dto.getBankAccount(),
                dto.getSalary(),
                dto.getEmploymentTerms(),
                startDate,
                roleQualifications,
                null,      // availabilityConstraints
                null       // loginRole
        );
    }

    public static EmployeeDTO toDTO(Employee employee) {
        if (employee == null) return null;

        List<Role> roles = employee.getRoleQualifications();
        String role = roles.isEmpty() ? null : roles.get(0).getRoleName();
        int qualificationLevel = 1;

        return new EmployeeDTO(
                employee.getId(),
                employee.getFullName(),
                employee.getBankAccount(),
                employee.getSalary(),
                employee.getEmployeeTerms(),
                dateFormat.format(employee.getEmployeeStartDate()),
                null, // license
                role,
                qualificationLevel
        );
    }
}
