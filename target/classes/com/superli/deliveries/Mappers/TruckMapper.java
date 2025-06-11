package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.dto.TruckDTO;

public class TruckMapper {

    public static Truck fromDTO(TruckDTO dto) {
        if (dto == null) return null;

        return new Truck(
                dto.getlicensePlate(),
                dto.getModel(),
                dto.getNetWeight(),
                dto.getMaxWeight(),
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
