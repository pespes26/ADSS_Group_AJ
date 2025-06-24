package com.superli.deliveries.Mappers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.dto.del.DriverDTO;

public class DriverMapper {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static Driver fromDTO(DriverDTO dto) {
        if (dto == null) return null;

        Driver driver = new Driver(
                dto.getId(),                                 // id
                dto.getFullName(),                           // fullName
                "000-000-000",                              // bankAccount (default)
                0.0,                                        // salary (default)
                -1,                                         // siteId (default)
                "Standard",                                 // employeeTerms (default)
                new Date(),                                 // employeeStartDate (default)
                new ArrayList<>(),                          // roleQualifications
                new ArrayList<>(),                          // availabilityConstraints
                new Role("DRIVER"),                         // loginRole
                LicenseType.valueOf(dto.getLicenseType())   // licenseType
        );
        driver.setAvailable(dto.isAvailable());  // Set the availability status from DTO
        return driver;
    }

    public static DriverDTO toDTO(Driver driver) {
        if (driver == null) return null;

        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setFullName(driver.getFullName());
        dto.setLicenseType(driver.getLicenseType().name());
        dto.setAvailable(driver.isAvailable());
        return dto;
    }
}
