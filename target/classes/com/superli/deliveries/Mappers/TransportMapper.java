package com.superli.deliveries.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.Transport;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.dto.del.TransportDTO;

public class TransportMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Transport fromDTO(TransportDTO dto, Truck truck, Driver driver, Site originSite) {
        if (dto == null || truck == null || driver == null || originSite == null) return null;

        Transport transport = new Transport(
                dto.getTransportId(),
                truck,
                driver,
                originSite,
                LocalDateTime.parse(dto.getDepartureDateTime(), formatter)
        );
        transport.setStatus(dto.getStatus());
        transport.setDepartureWeight(dto.getDepartureWeight());
        return transport;
    }

    public static TransportDTO toDTO(Transport transport) {
        if (transport == null) return null;
        return new TransportDTO(
                transport.getTransportId(),
                transport.getDepartureDateTime().toString(),
                transport.getTruck().getPlateNum(),
                transport.getDriver().getDriverId(),
                transport.getOriginSite().getSiteId(),
                transport.getDepartureWeight(),
                transport.getStatus(),
                transport.getDestinationDocs().stream()
                        .map(DestinationDoc::getDestinationDocId)
                        .collect(Collectors.toList())
        );
    }
}