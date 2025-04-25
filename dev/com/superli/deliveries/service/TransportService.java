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
    private final DriverService driverService;
    private final TruckService truckService;
    private final SiteService siteService;
    private int nextTransportId = 1; // Simple ID generator

    public TransportService(ITransportRepository transportRepository,
                            DriverService driverService,
                            TruckService truckService,
                            SiteService siteService) {
        this.transportRepository = transportRepository;
        this.driverService = driverService;
        this.truckService = truckService;
        this.siteService = siteService;
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

    /**
     * Attempt to automatically create a transport by finding compatible driver and truck.
     *
     * @return An Optional containing the created Transport if successful, empty otherwise
     */
    public Optional<Transport> createTransportAuto() {
        // Get available trucks and drivers
        List<Truck> availableTrucks = truckService.getAvailableTrucks();
        List<Driver> availableDrivers = driverService.getAvailableDrivers();

        if (availableTrucks.isEmpty() || availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        // Find a compatible truck and driver
        for (Truck truck : availableTrucks) {
            for (Driver driver : availableDrivers) {
                if (driver.getLicenseType() == truck.getRequiredLicenseType()) {
                    // Found a match! Create the transport
                    return createTransportWithTruckAndDriver(truck, driver);
                }
            }
        }

        // No compatible truck/driver found
        return Optional.empty();
    }

    /**
     * Create a transport with manually selected truck and driver.
     *
     * @param truck The selected truck
     * @param driver The selected driver
     * @return An Optional containing the created Transport if successful, empty otherwise
     */
    public Optional<Transport> createTransportManual(Truck truck, Driver driver) {
        // Verify truck and driver are available and compatible
        if (!truckService.isTruckAvailable(truck.getPlateNum())) {
            return Optional.empty();
        }

        if (!driver.isAvailable()) {
            return Optional.empty();
        }

        if (driver.getLicenseType() != truck.getRequiredLicenseType()) {
            return Optional.empty();
        }

        return createTransportWithTruckAndDriver(truck, driver);
    }

    /**
     * Helper method to create a transport with the given truck and driver.
     */
    private Optional<Transport> createTransportWithTruckAndDriver(Truck truck, Driver driver) {
        // Get default origin site
        List<Site> sites = siteService.getAllSites();
        if (sites.isEmpty()) {
            return Optional.empty(); // No sites to use as origin
        }

        // For simplicity, use the first site as origin
        Site originSite = sites.get(0);

        // Create the transport
        Transport transport = new Transport(
                nextTransportId++,
                LocalDateTime.now().plusDays(1), // Schedule for tomorrow
                truck,
                driver,
                originSite,
                TransportStatus.PLANNED
        );

        // Mark resources as unavailable
        truckService.markTruckAsUnavailable(truck.getPlateNum());
        driverService.markDriverAsUnavailable(driver.getDriverId());

        // Save the transport
        transportRepository.save(transport);

        return Optional.of(transport);
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
                    transport.getTruck().getRequiredLicenseType().getValue(),
                    transport.getTruck().getMaxWeight()
            );

            DriverDetailsView driverView = new DriverDetailsView(
                    transport.getDriver().getDriverId(),
                    transport.getDriver().getName(),
                    transport.getDriver().getLicenseType().getValue()
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