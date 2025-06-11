package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.dto.DestinationDocDTO;

public class DestinationDocMapper {

    public static DestinationDoc fromDTO(DestinationDocDTO dto, Site destinationSite) {
        if (dto == null || destinationSite == null) return null;

        return new DestinationDoc(
                dto.getDestinationDocId(),
                dto.getTransportId(),
                destinationSite,
                dto.getStatus()
        );
    }

    public static DestinationDocDTO toDTO(DestinationDoc doc) {
        if (doc == null) return null;

        return new DestinationDocDTO(
                doc.getDestinationDocId(),
                doc.getTransportId(),
                doc.getDestinationId().getSiteId(),
                doc.getStatus()
        );
    }
}