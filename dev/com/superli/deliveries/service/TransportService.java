package com.superli.deliveries.service;

import com.superli.deliveries.domain.*;
import com.superli.deliveries.domain.ports.ITransportRepository;
import com.superli.deliveries.presentation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing Transport operations and business logic.
 */
public class TransportService {

    private final ITransportRepository transportRepository;

    public TransportService(ITransportRepository transportRepository) {
        this.transportRepository = transportRepository;
    }

    public List<Transport> getAllTransports() {
        return new ArrayList<>(transportRepository.findAll());
    }

    public Optional<Transport> getTransportById(int transportId) {
        return transportRepository.findById(transportId);
    }

    public void saveTransport(Transport transport) {
        transportRepository.save(transport);
    }

    public boolean deleteTransport(int transportId) {
        return transportRepository.deleteById(transportId).isPresent();
    }

    public boolean addDestinationDocToTransport(int transportId, DestinationDoc doc) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            transportOpt.get().addDestination(doc);
            return true;
        }
        return false;
    }

    public boolean removeDestinationDocFromTransport(int transportId, DestinationDoc doc) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            return transportOpt.get().removeDestination(doc);
        }
        return false;
    }

    public boolean updateTransportStatus(int transportId, TransportStatus newStatus) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            transportOpt.get().setStatus(newStatus);
            return true;
        }
        return false;
    }

    public boolean updateDepartureDateTime(int transportId, LocalDateTime newTime) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            transportOpt.get().setDepartureDateTime(newTime);
            return true;
        }
        return false;
    }

    public boolean updateDepartureWeight(int transportId, float weight) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            transportOpt.get().setActualDepartureWeight(weight);
            return true;
        }
        return false;
    }

    public List<TransportSummaryView> getAllTransportSummaries() {
        return transportRepository.findAll().stream()
                .map(transport -> {
                    // Map origin site
                    Site site = transport.getOriginSite();
                    SiteDetailsView siteView = new SiteDetailsView(
                            site.getAddress(),
                            site.getPhoneNumber(),
                            site.getContactPersonName()
                    );

                    // Map destinations
                    List<DestinationDetailsView> destinations = transport.getDestinationList().stream()
                            .map(doc -> new DestinationDetailsView(
                                    doc.getDestinationDocId(),
                                    new SiteDetailsView(
                                            doc.getDestinationId().getAddress(),
                                            doc.getDestinationId().getPhoneNumber(),
                                            doc.getDestinationId().getContactPersonName()
                                    ),
                                    doc.getDeliveryItems().stream()
                                            .map(item -> new DeliveredItemDetailsView(
                                                    item.getProductId(),
                                                    item.getQuantity()
                                            )).collect(Collectors.toList()),
                                    TransportStatus.valueOf(doc.getStatus().toUpperCase())
                            ))
                            .collect(Collectors.toList());

                    return new TransportSummaryView(
                            transport.getTransportId(),
                            transport.getDepartureDateTime(),
                            siteView,
                            destinations,
                            transport.getDepartureWeight(),
                            transport.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }


    public TransportDetailsView getTransportDetailsViewById(int transportId) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);

        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();

            SiteDetailsView originSiteView = new SiteDetailsView(
                    transport.getOriginSite().getAddress(),
                    transport.getOriginSite().getPhoneNumber(),
                    transport.getOriginSite().getContactPersonName()
            );

            TruckDetailsView truckView = new TruckDetailsView(
                    transport.getTruck().getPlateNum(),
                    transport.getTruck().getModel(),
                    transport.getTruck().getMaxWeight()
            );

            DriverDetailsView driverView = new DriverDetailsView(
                    transport.getDriver().getName(),
                    transport.getDriver().getName(),
                    transport.getDriver().getLicenseType()
            );

            List<DestinationDetailsView> destinations = transport.getDestinationList().stream().map(doc ->
                    new DestinationDetailsView(
                            doc.getDestinationDocId(),
                            new SiteDetailsView(
                                    doc.getDestinationId().getAddress(),
                                    doc.getDestinationId().getPhoneNumber(),
                                    doc.getDestinationId().getContactPersonName()
                            ),
                            doc.getDeliveryItems().stream().map(item ->
                                    new DeliveredItemDetailsView(
                                            item.getProductId(),
                                            item.getQuantity()
                                    )
                            ).collect(Collectors.toList()),
                            TransportStatus.valueOf(doc.getStatus().toUpperCase())
                    )
            ).collect(Collectors.toList());

            return new TransportDetailsView(
                    transport.getTransportId(),
                    transport.getDepartureDateTime(),
                    transport.getTruck(),
                    transport.getDriver(),
                    transport.getOriginSite(),
                    destinations,
                    transport.getDepartureWeight(),
                    transport.getStatus()
            );

        }
        return null;
    }

}