package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.dto.DestinationDocDTO;

public class DestinationDocMapper {

    public static DestinationDoc fromDTO(DestinationDocDTO dto, Site site) {
        if (dto == null || site == null) return null;

        return new DestinationDoc(
                dto.getId(),
                dto.getTransportId(),
                site,
                dto.getStatus()
        );
    }

    public static DestinationDocDTO toDTO(DestinationDoc doc) {
        if (doc == null) return null;

        return new DestinationDocDTO(
                doc.getDestinationDocId(),
                doc.getTransportId(),
                Integer.parseInt(doc.getDestinationId().getSiteId()),
                doc.getStatus()
        );
    }
}