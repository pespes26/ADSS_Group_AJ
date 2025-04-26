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
 * Enhanced with additional methods for better separation of concerns.
 */
public class TransportService {

    private final ITransportRepository transportRepository;
    private final DriverService driverService;
    private final TruckService truckService;
    private final SiteService siteService;

    // Counter for generating sequential transport IDs
    private int nextTransportId = 1;

    /**
     * Constructs a new TransportService with required dependencies.
     *
     * @param transportRepository Repository for transports
     * @param driverService Service for driver operations
     * @param truckService Service for truck operations
     * @param siteService Service for site operations
     */
    public TransportService(ITransportRepository transportRepository,
                            DriverService driverService,
                            TruckService truckService,
                            SiteService siteService) {
        this.transportRepository = transportRepository;
        this.driverService = driverService;
        this.truckService = truckService;
        this.siteService = siteService;

        // Initialize nextTransportId based on existing transports
        initializeNextTransportId();
    }

    /**
     * Initializes the nextTransportId based on existing transports.
     * Sets it to one more than the highest existing ID, or 1 if no transports exist.
     */
    private void initializeNextTransportId() {
        nextTransportId = transportRepository.findAll().stream()
                .mapToInt(Transport::getTransportId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Generates the next transport ID and increments the counter.
     *
     * @return The next available transport ID
     */
    private int generateTransportId() {
        return nextTransportId++;
    }

    /**
     * Returns all transports in the system.
     *
     * @return List of all transports
     */
    public List<Transport> getAllTransports() {
        return new ArrayList<>(transportRepository.findAll());
    }

    /**
     * Finds a transport by its ID.
     *
     * @param transportId The ID of the transport to find
     * @return Optional containing the transport if found
     */
    public Optional<Transport> getTransportById(int transportId) {
        return transportRepository.findById(transportId);
    }

    /**
     * Saves a transport to the repository.
     *
     * @param transport The transport to save
     */
    public void saveTransport(Transport transport) {
        transportRepository.save(transport);
    }

    /**
     * Deletes a transport by its ID.
     *
     * @param transportId The ID of the transport to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteTransport(int transportId) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);

        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();

            // Release driver and truck resources
            releaseTransportResources(transport);

            // Now delete the transport
            return transportRepository.deleteById(transportId).isPresent();
        }

        return false;
    }

    /**
     * Automatically creates a new transport by finding an available compatible truck and driver.
     *
     * @return Optional containing the created transport if successful
     */
    public Optional<Transport> createTransportAuto() {
        // Get available trucks and drivers
        List<Truck> availableTrucks = truckService.getAvailableTrucks();
        List<Driver> availableDrivers = driverService.getAvailableDrivers();

        if (availableTrucks.isEmpty() || availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        // Find a compatible pair
        for (Truck truck : availableTrucks) {
            for (Driver driver : availableDrivers) {
                if (driver.getLicenseType().equals(truck.getRequiredLicenseType())) {
                    // Found a match, get a default origin site
                    Optional<Site> originSiteOpt = getDefaultOriginSite();
                    if (originSiteOpt.isEmpty()) {
                        return Optional.empty();
                    }

                    // Create transport
                    return createTransportWithTruckDriverAndSite(truck, driver, originSiteOpt.get());
                }
            }
        }

        // No compatible pair found
        return Optional.empty();
    }

    /**
     * Creates a transport manually with the given truck and driver.
     *
     * @param truck The truck to use
     * @param driver The driver to assign
     * @return Optional containing the created transport if successful
     */
    public Optional<Transport> createTransportManual(Truck truck, Driver driver) {
        // Check if truck and driver are available
        if (!truck.isAvailable() || !driver.isAvailable()) {
            return Optional.empty();
        }

        // Check license compatibility
        if (!driver.getLicenseType().equals(truck.getRequiredLicenseType())) {
            return Optional.empty();
        }

        // Get a default origin site
        Optional<Site> originSiteOpt = getDefaultOriginSite();
        if (originSiteOpt.isEmpty()) {
            return Optional.empty();
        }

        return createTransportWithTruckDriverAndSite(truck, driver, originSiteOpt.get());
    }

    /**
     * Creates a transport manually with the given truck, driver, and specific site.
     *
     * @param truck The truck to use
     * @param driver The driver to assign
     * @param originSite The origin site for the transport
     * @return Optional containing the created transport if successful
     */
    public Optional<Transport> createTransportManualWithSite(Truck truck, Driver driver, Site originSite,
                                                             LocalDateTime departureDateTime) {
        // Check if truck and driver are available
        if (!truck.isAvailable() || !driver.isAvailable()) {
            return Optional.empty();
        }

        // Check license compatibility
        if (!driver.getLicenseType().equals(truck.getRequiredLicenseType())) {
            return Optional.empty();
        }

        // Create transport with specified parameters
        int transportId = generateTransportId();

        Transport transport = new Transport(
                transportId,
                departureDateTime,
                truck,
                driver,
                originSite,
                TransportStatus.PLANNED
        );

        // Save to repository
        saveTransport(transport);

        // Mark truck and driver as unavailable
        truck.setAvailable(false);
        truckService.markTruckAsUnavailable(truck.getPlateNum());

        driver.setAvailable(false);
        driverService.markDriverAsUnavailable(driver.getDriverId());

        return Optional.of(transport);
    }

    /**
     * Helper method to create a transport with the given truck and driver.
     *
     * @param truck The truck to use
     * @param driver The driver to assign
     * @param originSite The origin site
     * @return Optional containing the created transport
     */
    private Optional<Transport> createTransportWithTruckDriverAndSite(Truck truck, Driver driver, Site originSite) {
        // Create transport with next available ID
        int transportId = generateTransportId();
        LocalDateTime departureTime = LocalDateTime.now().plusDays(1); // Default to tomorrow

        Transport transport = new Transport(
                transportId,
                departureTime,
                truck,
                driver,
                originSite,
                TransportStatus.PLANNED
        );

        // Save to repository
        saveTransport(transport);

        // Mark truck and driver as unavailable
        truck.setAvailable(false);
        truckService.markTruckAsUnavailable(truck.getPlateNum());

        driver.setAvailable(false);
        driverService.markDriverAsUnavailable(driver.getDriverId());

        return Optional.of(transport);
    }

    /**
     * Gets a default origin site for the transport.
     * In a real implementation, this would likely be selected by the user.
     *
     * @return Optional containing a site, or empty if no sites exist
     */
    private Optional<Site> getDefaultOriginSite() {
        List<Site> sites = siteService.getAllSites();
        if (sites.isEmpty()) {
            return Optional.empty();
        }

        // Return the first site as default
        return Optional.of(sites.get(0));
    }

    /**
     * Adds a destination document to a transport.
     *
     * @param transportId ID of the transport
     * @param doc The destination document to add
     * @return true if added successfully, false otherwise
     */
    public boolean addDestinationDocToTransport(int transportId, DestinationDoc doc) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();
            transport.addDestination(doc);
            transportRepository.save(transport);
            return true;
        }
        return false;
    }

    /**
     * Removes a destination document from a transport.
     *
     * @param transportId ID of the transport
     * @param doc The destination document to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeDestinationDocFromTransport(int transportId, DestinationDoc doc) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();
            boolean removed = transport.removeDestination(doc);
            if (removed) {
                transportRepository.save(transport);
            }
            return removed;
        }
        return false;
    }

    /**
     * Updates the status of a transport.
     *
     * @param transportId ID of the transport
     * @param newStatus New status to set
     * @return true if updated successfully, false otherwise
     */
    public boolean updateTransportStatus(int transportId, TransportStatus newStatus) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();

            // Check if this is a significant status change
            if ((transport.getStatus() != TransportStatus.COMPLETED &&
                    transport.getStatus() != TransportStatus.CANCELLED) &&
                    (newStatus == TransportStatus.COMPLETED || newStatus == TransportStatus.CANCELLED)) {
                // We're completing or cancelling - release resources
                releaseTransportResources(transport);
            }

            transport.setStatus(newStatus);
            transportRepository.save(transport);
            return true;
        }
        return false;
    }

    /**
     * Releases the truck and driver associated with a transport.
     *
     * @param transport The transport whose resources should be released
     */
    private void releaseTransportResources(Transport transport) {
        // Mark truck as available
        Truck truck = transport.getTruck();
        truck.setAvailable(true);
        truckService.markTruckAsAvailable(truck.getPlateNum());

        // Mark driver as available
        Driver driver = transport.getDriver();
        driver.setAvailable(true);
        driverService.markDriverAsAvailable(driver.getDriverId());
    }

    /**
     * Updates the departure date and time of a transport.
     *
     * @param transportId ID of the transport
     * @param newTime New departure date and time
     * @return true if updated successfully, false otherwise
     */
    public boolean updateDepartureDateTime(int transportId, LocalDateTime newTime) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();
            transport.setDepartureDateTime(newTime);
            transportRepository.save(transport);
            return true;
        }
        return false;
    }

    /**
     * Updates the departure weight of a transport.
     *
     * @param transportId ID of the transport
     * @param weight New departure weight
     * @return true if updated successfully, false otherwise
     */
    public boolean updateDepartureWeight(int transportId, float weight) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();

            // Check if weight exceeds truck capacity
            float maxAllowedWeight = transport.getTruck().getMaxWeight() - transport.getTruck().getNetWeight();
            if (weight > maxAllowedWeight) {
                System.out.println("⚠️ Warning: Weight (" + weight + " kg) exceeds truck capacity (" +
                        maxAllowedWeight + " kg). Cargo may need to be redistributed.");
            }

            transport.setDepartureWeight(weight);
            transportRepository.save(transport);
            return true;
        }
        return false;
    }

    /**
     * Retrieves summaries of all transports for display purposes.
     *
     * @return List of transport summaries
     */
    public List<TransportSummaryView> getAllTransportSummaries() {
        return transportRepository.findAll().stream()
                .map(this::createTransportSummary)
                .collect(Collectors.toList());
    }

    /**
     * Creates a summary view from a transport.
     *
     * @param transport The transport to summarize
     * @return A summary view of the transport
     */
    private TransportSummaryView createTransportSummary(Transport transport) {
        // Map origin site to site view
        Site site = transport.getOriginSite();
        SiteDetailsView siteView = new SiteDetailsView(
                site.getAddress(),
                site.getPhoneNumber(),
                site.getContactPersonName()
        );

        // Map destinations to destination views
        List<DestinationDetailsView> destinations = transport.getDestinationList().stream()
                .map(doc -> {
                    TransportStatus docStatus;
                    try {
                        docStatus = TransportStatus.valueOf(doc.getStatus().toUpperCase());
                    } catch (Exception e) {
                        docStatus = TransportStatus.PLANNED; // Default status
                    }

                    return new DestinationDetailsView(
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
                            docStatus
                    );
                })
                .collect(Collectors.toList());

        return new TransportSummaryView(
                transport.getTransportId(),
                transport.getDepartureDateTime(),
                siteView,
                destinations,
                transport.getDepartureWeight(),
                transport.getStatus()
        );
    }

    /**
     * Retrieves detailed information about a transport.
     *
     * @param transportId ID of the transport
     * @return Detailed view of the transport, or null if not found
     */
    public TransportDetailsView getTransportDetailsViewById(int transportId) {
        Optional<Transport> transportOpt = transportRepository.findById(transportId);

        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();

            // Create destination detail views
            List<DestinationDetailsView> destinations = transport.getDestinationList().stream()
                    .map(doc -> {
                        TransportStatus docStatus;
                        try {
                            docStatus = TransportStatus.valueOf(doc.getStatus().toUpperCase());
                        } catch (Exception e) {
                            docStatus = TransportStatus.PLANNED; // Default status
                        }

                        return new DestinationDetailsView(
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
                                docStatus
                        );
                    })
                    .collect(Collectors.toList());

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