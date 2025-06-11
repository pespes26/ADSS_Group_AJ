package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

public class DriverMapper {

    public static Driver fromDTO(DriverDTO dto) {
        if (dto == null) return null;

        return new Driver(
                dto.getId(),                                 // id (String in Entity)
                dto.getFullName(),                           // fullName
                null,                                        // bankAccount (TODO)
                0.0,                                         // salary (TODO)
                null,                                        // employeeTerms (TODO)
                null,                                        // employeeStartDate (TODO)
                null,                                        // roleQualifications (TODO)
                null,                                        // availabilityConstraints (TODO)
                null,                                        // loginRole (TODO)
                LicenseType.valueOf(dto.getLicenseType())    // licenseType
        );
    }

    public static DriverDTO toDTO(Driver driver) {
        if (driver == null) return null;

        return new DriverDTO(
                driver.getId(),
                driver.getFullName(),
                driver.getLicenseType().name()
        );
    }
}
