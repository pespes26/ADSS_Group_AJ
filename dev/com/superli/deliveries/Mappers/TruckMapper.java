package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

public class TruckMapper {

    public static Truck fromDTO(TruckDTO dto, LicenseType licenseType) {
        if (dto == null || licenseType == null) return null;

        return new Truck(
                dto.getLicensePlate(), // plateNum
                dto.getModel(),
                0f, // netWeight (TODO)
                0f, // maxWeight (TODO)
                licenseType
        );
    }

    public static TruckDTO toDTO(Truck truck, int id) {
        if (truck == null) return null;

        return new TruckDTO(
                id,
                truck.getModel(),
                truck.getPlateNum()
        );
    }
}
