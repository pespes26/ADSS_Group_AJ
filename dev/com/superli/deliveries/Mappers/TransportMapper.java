package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TransportMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Transport fromDTO(TransportDTO dto, Truck truck, Driver driver, Site originSite) {
        if (dto == null || truck == null || driver == null || originSite == null) return null;

        return new Transport(
                dto.getId(),
                LocalDateTime.parse(dto.getDepartureDateTime(), formatter),
                truck,
                driver,
                originSite,
                TransportStatus.valueOf(dto.getStatus())
        );
    }

    public static TransportDTO toDTO(Transport transport) {
        if (transport == null) return null;

        return new TransportDTO(
                transport.getTransportId(),
                transport.getDepartureDateTime().format(formatter),
                Integer.parseInt(transport.getTruck().getPlateNum()),        // assuming plateNum is int-like
                Integer.parseInt(transport.getDriver().getId()),
                Integer.parseInt(transport.getOriginSite().getSiteId()),
                transport.getDepartureWeight(),                              // assuming getter exists
                transport.getStatus().name()
        );
    }
}