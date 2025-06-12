package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.del.TransportDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TransportMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Transport fromDTO(TransportDTO dto, Truck truck, Driver driver, Site originSite) {
        if (dto == null || truck == null || driver == null || originSite == null) return null;

        return new Transport(
                dto.getTransportId(),
                truck,
                driver,
                originSite,
                LocalDateTime.parse(dto.getDepartureDateTime(), formatter)
        );
    }

    public static TransportDTO toDTO(Transport transport) {
        if (transport == null) return null;

        List<String> destinationDocIds = transport.getDestinationDocs().stream()
                .map(DestinationDoc::getDestinationDocId)
                .collect(Collectors.toList());

        return new TransportDTO(
                transport.getTransportId(),
                transport.getDepartureDateTime().format(formatter),
                transport.getTruck().getPlateNum(),
                transport.getDriver().getId(),
                transport.getOriginSite().getSiteId(),
                transport.getDepartureWeight(),
                transport.getStatus().name(),
                destinationDocIds
        );
    }
}