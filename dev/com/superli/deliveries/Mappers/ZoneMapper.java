package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.Zone;
import com.superli.deliveries.dto.del.ZoneDTO;

public class ZoneMapper {

    public static Zone toDomain(ZoneDTO dto) {
        if (dto == null) return null;
        return new Zone(
                dto.getId(),
                dto.getName()
        );
    }

    public static ZoneDTO toDTO(Zone zone) {
        if (zone == null) return null;
        return new ZoneDTO(
                zone.getZoneId(),
                zone.getName(),
                0.0f, // Default capacity since Zone doesn't have this field
                true  // Default active state since Zone doesn't have this field
        );
    }
}