package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.Zone;
import com.superli.deliveries.dto.del.SiteDTO;

public class SiteMapper {

    public static Site fromDTO(SiteDTO dto, Zone zone) {
        if (dto == null || zone == null) return null;

        return new Site(
                dto.getSiteId(),
                dto.getAddress(),
                dto.getPhoneNumber(),
                dto.getContactPersonName(),
                zone
        );
    }

    public static SiteDTO toDTO(Site site) {
        if (site == null) return null;

        return new SiteDTO(
                site.getSiteId(),
                site.getAddress(),
                site.getPhoneNumber(),
                site.getContactPersonName(),
                site.getZone().getZoneId()
        );
    }
}
