package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

public class ZoneMapper {

    public static Zone fromDTO(ZoneDTO dto) {
        if (dto == null) return null;
        return new Zone(
                String.valueOf(dto.getId()),
                dto.getName()
        );
    }

    public static ZoneDTO toDTO(Zone zone) {
        if (zone == null) return null;
        return new ZoneDTO(
                Integer.parseInt(zone.getZoneId()),
                zone.getName()
        );
    }
}