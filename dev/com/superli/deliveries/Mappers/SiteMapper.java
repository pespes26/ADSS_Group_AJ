package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

public class SiteMapper {

    public static Site fromDTO(SiteDTO dto, Zone zone) {
        if (dto == null || zone == null) return null;

        return new Site(
                String.valueOf(dto.getSiteId()),
                dto.getAddress(),
                null,
                dto.getContact(),                    // contact → contactName
                zone
        );
    }

    public static SiteDTO toDTO(Site site) {
        if (site == null) return null;

        return new SiteDTO(
                Integer.parseInt(site.getSiteId()),
                site.getAddress(),
                site.getContactPersonName(),
                site.getZone().getZoneId()
        );
    }
}
