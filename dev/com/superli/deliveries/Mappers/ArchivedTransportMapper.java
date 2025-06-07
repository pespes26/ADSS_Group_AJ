package com.superli.deliveries.Mappers;

import com.superli.deliveries.dto.*;
import com.superli.deliveries.domain.core.*;

import java.time.format.DateTimeFormatter;

public class ArchivedTransportMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static ArchivedTransportDTO toDTO(ArchivedTransport at) {
        if (at == null) return null;
        return new ArchivedTransportDTO(
                at.getTransportId(),
                at.getDepartureDateTime().format(formatter),
                Integer.parseInt(at.getTruck().getPlateNum()),
                Integer.parseInt(at.getDriver().getId()),
                Integer.parseInt(at.getOriginSite().getSiteId()),
                at.getStatus().name(),
                at.getArchivedDateTime().format(formatter),
                at.getFinalStatus(),
                at.getArchiveNotes()
        );
    }

    // From Entity to DTO
    public static TransportDTO toDTO(Transport transport) {
        if (transport == null) return null;

        return new TransportDTO(
                transport.getTransportId(),
                transport.getDepartureDateTime().format(formatter),
                Integer.parseInt(transport.getTruck().getPlateNum()),  // assuming plateNum is numeric string
                Integer.parseInt(transport.getDriver().getId()),
                Integer.parseInt(transport.getOriginSite().getSiteId()),
                transport.getDepartureWeight(),
                transport.getStatus().name()
        );
    }
}
