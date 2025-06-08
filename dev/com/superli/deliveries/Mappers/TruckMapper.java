package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

public class TruckMapper {

    public static Truck fromDTO(TruckDTO dto, LicenseType licenseType) {
        if (dto == null || licenseType == null) return null;

        return new Truck(
                dto.getlicensePlate(),
                dto.getModel(),
                0f, // netWeight (TODO)
                0f, // maxWeight (TODO)
                dto.getRequiredLicenseType()
        );
    }

    public static TruckDTO toDTO(Truck truck) {
        if (truck == null) return null;

        return new TruckDTO(
                truck.getPlateNum(),
                truck.getModel(),
                truck.getNetWeight(),
                truck.getMaxWeight(),
                truck.getRequiredLicenseType()
        );
    }
}
