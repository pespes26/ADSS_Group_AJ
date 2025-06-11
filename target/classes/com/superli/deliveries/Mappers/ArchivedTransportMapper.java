package com.superli.deliveries.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.superli.deliveries.domain.core.ArchivedTransport;
import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.TransportStatus;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.dto.ArchivedTransportDTO;

public class ArchivedTransportMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static ArchivedTransportDTO toDTO(ArchivedTransport transport) {
        if (transport == null) return null;
        return new ArchivedTransportDTO(
            transport.getTransportId(),
            transport.getDepartureDateTime().format(formatter),
            transport.getTruck().getPlateNum(),
            transport.getDriver().getId(),
            transport.getOriginSite().getSiteId(),
            transport.getStatus().name(),
            transport.getArchivedDateTime().format(formatter),
            transport.getFinalStatus(),
            transport.getArchiveNotes()
        );
    }

    public static ArchivedTransport fromDTO(ArchivedTransportDTO dto, Truck truck, Driver driver, Site originSite) {
        if (dto == null || truck == null || driver == null || originSite == null) return null;
        return new ArchivedTransport(
            Integer.parseInt(dto.getId()),
            LocalDateTime.parse(dto.getDepartureDateTime(), formatter),
            truck,
            driver,
            originSite,
            TransportStatus.valueOf(dto.getStatus()),
            LocalDateTime.parse(dto.getArchivedDateTime(), formatter),
            dto.getFinalStatus()
        );
    }
}
