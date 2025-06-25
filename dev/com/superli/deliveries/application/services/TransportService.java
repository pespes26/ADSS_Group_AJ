package com.superli.deliveries.application.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.superli.deliveries.Mappers.TransportMapper;
import com.superli.deliveries.dataaccess.dao.del.TransportDAO;
import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.Transport;
import com.superli.deliveries.domain.core.TransportStatus;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.dto.del.TransportDTO;
import com.superli.deliveries.presentation.del.DeliveredItemDetailsView;
import com.superli.deliveries.presentation.del.DestinationDetailsView;
import com.superli.deliveries.presentation.del.SiteDetailsView;
import com.superli.deliveries.presentation.del.TransportDetailsView;
import com.superli.deliveries.presentation.del.TransportSummaryView;
import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.domain.core.Employee;

/**
 * Service layer for managing Transport operations and business logic.
 * Enhanced with additional methods for better separation of concerns.
 */
public class TransportService {

    private final TransportDAO transportDAO;
    private DriverService driverService;
    private TruckService truckService;
    private final SiteService siteService;
    private final DestinationDocService destinationDocService;

    // Counter for generating sequential transport IDs
    private int nextTransportId = 1;

    /**
     * Constructs a new TransportService with required dependencies.
     *
     * @param transportDAO DAO for transports
     * @param driverService Service for driver operations
     * @param truckService Service for truck operations
     * @param siteService Service for site operations
     * @param destinationDocService Service for destination document operations
     */
    public TransportService(TransportDAO transportDAO,
                            DriverService driverService,
                            TruckService truckService,
                            SiteService siteService,
                            DestinationDocService destinationDocService) {
        this.transportDAO = transportDAO;
        this.driverService = driverService;
        this.truckService = truckService;
        this.siteService = siteService;
        this.destinationDocService = destinationDocService;

        // Initialize nextTransportId based on existing transports
        initializeNextTransportId();
    }

    /**
     * Initializes the nextTransportId based on existing transports.
     * Sets it to one more than the highest existing ID, or 1 if no transports exist.
     */
    private void initializeNextTransportId() {
        try {
            nextTransportId = transportDAO.findAll().stream()
                    .mapToInt(dto -> {
                        // Extract numeric part from transport ID (e.g., "TR001" -> 1)
                        String id = dto.getTransportId();
                        return Integer.parseInt(id.replaceAll("[^0-9]", ""));
                    })
                    .max()
                    .orElse(0) + 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing next transport ID", e);
        }
    }

    /**
     * Generates the next transport ID and increments the counter.
     *
     * @return The next available transport ID
     */
    private String generateTransportId() {
        // Format: TR + padded number (e.g., TR001, TR002, etc.)
        return String.format("TR%03d", nextTransportId++);
    }

    /**
     * Returns all transports in the system.
     *
     * @return List of all transports
     */
    public List<Transport> getAllTransports() {
        try {
            return transportDAO.findAll().stream()
                    .map(dto -> {
                        Optional<Truck> truck = truckService.getTruckById(dto.getTruckId());
                        Optional<Driver> driver = driverService.getDriverById(dto.getDriverId());
                        Optional<Site> site = siteService.getSiteById(dto.getOriginSiteId());
                        
                        if (truck.isPresent() && driver.isPresent() && site.isPresent()) {
                            Transport transport = TransportMapper.fromDTO(dto, truck.get(), driver.get(), site.get());
                            // Load destination documents
                            List<DestinationDoc> docs = destinationDocService.getDocsByTransport(dto.getTransportId());
                            for (DestinationDoc doc : docs) {
                                transport.addDestinationDoc(doc);
                            }
                            return transport;
                        }
                        return null;
                    })
                    .filter(t -> t != null)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all transports", e);
        }
    }

    /**
     * Finds a transport by its ID.
     *
     * @param transportId The ID of the transport to find
     * @return Optional containing the transport if found
     */
    public Optional<Transport> getTransportById(String transportId) {
        try {
            Optional<TransportDTO> dtoOpt = transportDAO.findById(transportId);
            if (dtoOpt.isPresent()) {
                TransportDTO dto = dtoOpt.get();
                Optional<Truck> truck = truckService.getTruckById(dto.getTruckId());
                Optional<Driver> driver = driverService.getDriverById(dto.getDriverId());
                Optional<Site> site = siteService.getSiteById(dto.getOriginSiteId());
                
                if (truck.isPresent() && driver.isPresent() && site.isPresent()) {
                    Transport transport = TransportMapper.fromDTO(dto, truck.get(), driver.get(), site.get());
                    // Load destination documents
                    List<DestinationDoc> docs = destinationDocService.getDocsByTransport(transportId);
                    for (DestinationDoc doc : docs) {
                        transport.addDestinationDoc(doc);
                    }
                    return Optional.of(transport);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting transport by id: " + transportId, e);
        }
    }

    /**
     * Saves a transport to the database.
     *
     * @param transport The transport to save
     * @return The saved transport
     */
    public Transport saveTransport(Transport transport) {
        try {
            // Validate weight against truck capacity
            if (transport.getDepartureWeight() > transport.getTruck().getMaxWeight()) {
                throw new IllegalArgumentException("Transport weight exceeds truck's maximum capacity");
            }

            TransportDTO dto = TransportMapper.toDTO(transport);
            TransportDTO savedDto = transportDAO.save(dto);
            
            // Get required dependencies for mapping
            Optional<Truck> truck = truckService.getTruckById(savedDto.getTruckId());
            Optional<Driver> driver = driverService.getDriverById(savedDto.getDriverId());
            Optional<Site> site = siteService.getSiteById(savedDto.getOriginSiteId());
            
            if (truck.isEmpty() || driver.isEmpty() || site.isEmpty()) {
                throw new RuntimeException("Required dependencies not found for transport mapping");
            }
            
            return TransportMapper.fromDTO(savedDto, truck.get(), driver.get(), site.get());
        } catch (SQLException e) {
            throw new RuntimeException("Error saving transport", e);
        }
    }

    /**
     * Deletes a transport by its ID.
     *
     * @param transportId The ID of the transport to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteTransport(String transportId) {
        try {
            Optional<Transport> transportOpt = getTransportById(transportId);

            if (transportOpt.isPresent()) {
                Transport transport = transportOpt.get();

                // Release driver and truck resources
                releaseTransportResources(transport);

                // Now delete the transport
                transportDAO.deleteById(transportId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting transport: " + transportId, e);
        }
    }

    /**
     * Automatically creates a new transport by finding an available compatible truck and driver.
     *
     * @return Optional containing the created transport if successful
     */
    public Optional<Transport> createTransportAuto() {
        List<Truck> availableTrucks = truckService.getAvailableTrucks();
        if (availableTrucks.isEmpty()) {
            return Optional.empty();
        }
        // For demo, use current day and MORNING shift. In real app, prompt user or use context.
        java.time.DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();
        ShiftType shift = ShiftType.MORNING;
        List<Employee> eligibleEmployees = com.superli.deliveries.application.services.EmployeeManagmentService.findAvailableOn(today, new com.superli.deliveries.domain.core.Role("DRIVER"), shift);
        List<Driver> eligibleDrivers = eligibleEmployees.stream()
            .map(e -> driverService.getDriverById(e.getId()).orElse(null))
            .filter(d -> d != null && d.isAvailable())
            .collect(java.util.stream.Collectors.toList());
        if (eligibleDrivers.isEmpty()) {
            return Optional.empty();
        }
        for (Truck truck : availableTrucks) {
            for (Driver driver : eligibleDrivers) {
                if (driver.getLicenseType().equals(truck.getRequiredLicenseType())) {
                    Optional<Site> originSiteOpt = getDefaultOriginSite();
                    if (originSiteOpt.isEmpty()) {
                        return Optional.empty();
                    }
                    return createTransportWithTruckDriverAndSite(truck, driver, originSiteOpt.get());
                }
            }
        }
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
        Optional<Site> originSiteOpt = getDefaultOriginSite();
        if (originSiteOpt.isEmpty()) {
            return Optional.empty();
        }
        // For demo, use current day and MORNING shift. In real app, prompt user or use context.
        java.time.DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();
        ShiftType shift = ShiftType.MORNING;
        List<Employee> eligibleEmployees = com.superli.deliveries.application.services.EmployeeManagmentService.findAvailableOn(today, new com.superli.deliveries.domain.core.Role("DRIVER"), shift);
        boolean isEligible = eligibleEmployees.stream().anyMatch(e -> e.getId().equals(driver.getDriverId()));
        if (!isEligible || driverService.getDriverById(driver.getDriverId()).isEmpty() || !driver.isAvailable()) {
            return Optional.empty();
        }
        return createTransportWithTruckDriverAndSite(truck, driver, originSiteOpt.get());
    }

    /**
     * Creates a transport manually with the given truck, driver, and site.
     *
     * @param truck The truck to use
     * @param driver The driver to assign
     * @param site The origin site
     * @param departureDateTime The scheduled departure time
     * @return Optional containing the created transport if successful
     */
    public Optional<Transport> createTransportManualWithSite(Truck truck, Driver driver, Site site, java.time.LocalDateTime departureDateTime) {
        if (!driver.getLicenseType().equals(truck.getRequiredLicenseType())) {
            return Optional.empty();
        }
        // For demo, use current day and MORNING shift. In real app, prompt user or use context.
        java.time.DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();
        ShiftType shift = ShiftType.MORNING;
        List<Employee> eligibleEmployees = com.superli.deliveries.application.services.EmployeeManagmentService.findAvailableOn(today, new com.superli.deliveries.domain.core.Role("DRIVER"), shift);
        boolean isEligible = eligibleEmployees.stream().anyMatch(e -> e.getId().equals(driver.getDriverId()));
        if (!isEligible || driverService.getDriverById(driver.getDriverId()).isEmpty() || !driver.isAvailable()) {
            return Optional.empty();
        }
        String transportId = generateTransportId();
        Transport transport = new Transport(transportId, truck, driver, site, departureDateTime);
        saveTransport(transport);
        return Optional.of(transport);
    }

    /**
     * Creates a transport with the given truck, driver, and site.
     *
     * @param truck The truck to use
     * @param driver The driver to assign
     * @param originSite The origin site
     * @return Optional containing the created transport if successful
     */
    private Optional<Transport> createTransportWithTruckDriverAndSite(Truck truck, Driver driver, Site originSite) {
        if (!driver.getLicenseType().equals(truck.getRequiredLicenseType())) {
            return Optional.empty();
        }
        // Check eligibility: driver must be in drivers table and available
        if (driverService.getDriverById(driver.getDriverId()).isEmpty() || !driver.isAvailable() || !truck.isAvailable()) {
            return Optional.empty();
        }
        String transportId = generateTransportId();
        Transport transport = new Transport(transportId, truck, driver, originSite);
        // Mark driver and truck as unavailable
        driverService.markDriverAsUnavailable(driver.getDriverId());
        truckService.markTruckAsUnavailable(truck.getPlateNum());
        saveTransport(transport);
        return Optional.of(transport);
    }

    /**
     * Gets the default origin site.
     *
     * @return Optional containing the default origin site if found
     */
    private Optional<Site> getDefaultOriginSite() {
        return siteService.getAllSites().stream()
                .findFirst();
    }

    /**
     * Adds a destination document to a transport.
     *
     * @param transportId The ID of the transport
     * @param doc The destination document to add
     * @return true if added successfully, false otherwise
     */
    public boolean addDestinationDocToTransport(String transportId, DestinationDoc doc) {
        Optional<Transport> transportOpt = getTransportById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();
            transport.addDestinationDoc(doc);
            saveTransport(transport);
            return true;
        }
        return false;
    }

    /**
     * Removes a destination document from a transport.
     *
     * @param transportId The ID of the transport
     * @param doc The destination document to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeDestinationDocFromTransport(String transportId, DestinationDoc doc) {
        Optional<Transport> transportOpt = getTransportById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();
            transport.removeDestinationDoc(doc);
            saveTransport(transport);
            return true;
        }
        return false;
    }

    /**
     * Gets the valid next statuses for a given current status.
     *
     * @param currentStatus The current status of the transport
     * @return List of valid next statuses
     */
    public List<TransportStatus> getValidNextStatuses(TransportStatus currentStatus) {
        List<TransportStatus> validStatuses = new ArrayList<>();
        
        switch (currentStatus) {
            case PLANNED:
                validStatuses.add(TransportStatus.DISPATCHED);
                validStatuses.add(TransportStatus.CANCELLED);
                break;
            case DISPATCHED:
                validStatuses.add(TransportStatus.COMPLETED);
                validStatuses.add(TransportStatus.CANCELLED);
                break;
            case COMPLETED:
            case CANCELLED:
            case SELFDELIVERY:
                // No valid transitions
                break;
        }
        
        return validStatuses;
    }

    /**
     * Validates if a status transition is allowed.
     *
     * @param currentStatus The current status
     * @param newStatus The proposed new status
     * @return true if the transition is valid, false otherwise
     */
    public boolean isValidStatusTransition(TransportStatus currentStatus, TransportStatus newStatus) {
        return getValidNextStatuses(currentStatus).contains(newStatus);
    }

    /**
     * Updates the status of a transport with validation.
     *
     * @param transportId The ID of the transport
     * @param newStatus The new status to set
     * @return true if the status was updated successfully, false otherwise
     */
    public boolean updateTransportStatus(String transportId, TransportStatus newStatus) {
        Optional<Transport> transportOpt = getTransportById(transportId);
        if (transportOpt.isEmpty()) {
            return false;
        }

        Transport transport = transportOpt.get();
        TransportStatus currentStatus = transport.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            return false;
        }

        // Update status
        transport.setStatus(newStatus);
        saveTransport(transport);

        // If transport is completed or cancelled, release resources
        if (newStatus == TransportStatus.COMPLETED || newStatus == TransportStatus.CANCELLED) {
            releaseTransportResources(transport);
        }

        return true;
    }

    /**
     * Releases resources (driver and truck) associated with a transport.
     * This is called when a transport is completed or cancelled.
     *
     * @param transport The transport whose resources should be released
     */
    private void releaseTransportResources(Transport transport) {
        if (transport != null) {
            // Release driver (set available = true)
            driverService.markDriverAsAvailable(transport.getDriver().getDriverId());
            // Release truck (set available = true)
            truckService.markTruckAsAvailable(transport.getTruck().getPlateNum());
        }
    }

    /**
     * Updates the departure date and time of a transport.
     *
     * @param transportId The ID of the transport
     * @param newTime The new departure time
     * @return true if updated successfully, false otherwise
     */
    public boolean updateDepartureDateTime(String transportId, LocalDateTime newTime) {
        Optional<Transport> transportOpt = getTransportById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();
            transport.setDepartureDateTime(newTime);
            saveTransport(transport);
            return true;
        }
        return false;
    }

    /**
     * Updates the departure weight of a transport.
     *
     * @param transportId The ID of the transport
     * @param weight The new departure weight
     * @return true if updated successfully, false otherwise
     */
    public boolean updateDepartureWeight(String transportId, float weight) {
        Optional<Transport> transportOpt = getTransportById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();
            transport.setDepartureWeight(weight);
            saveTransport(transport);
            return true;
        }
        return false;
    }

    /**
     * Gets all transport summaries.
     *
     * @return List of transport summaries
     */
    public List<TransportSummaryView> getAllTransportSummaries() {
        return getAllTransports().stream()
                .map(this::createTransportSummary)
                .collect(Collectors.toList());
    }

    /**
     * Creates a transport summary from a transport.
     *
     * @param transport The transport to summarize
     * @return The transport summary
     */
    private TransportSummaryView createTransportSummary(Transport transport) {
        Site originSite = transport.getOriginSite();
        SiteDetailsView originSiteView = new SiteDetailsView(
            originSite.getAddress(),
            originSite.getPhoneNumber(),
            originSite.getContactPersonName()
        );

        List<DestinationDetailsView> destinationViews = transport.getDestinationDocs().stream()
            .map(doc -> {
                Site destSite = doc.getDestinationId();
                SiteDetailsView destSiteView = new SiteDetailsView(
                    destSite.getAddress(),
                    destSite.getPhoneNumber(),
                    destSite.getContactPersonName()
                );
                return new DestinationDetailsView(
                    doc.getDestinationDocId(),
                    destSiteView,
                    doc.getDeliveryItems().stream()
                        .map(item -> new DeliveredItemDetailsView(item.getProductId(), item.getQuantity()))
                        .collect(Collectors.toList()),
                    TransportStatus.valueOf(doc.getStatus())
                );
            })
            .collect(Collectors.toList());

        return new TransportSummaryView(
            Integer.parseInt(transport.getTransportId()),
            transport.getDepartureDateTime(),
            originSiteView,
            destinationViews,
            transport.getDepartureWeight(),
            transport.getStatus()
        );
    }

    /**
     * Gets detailed view of a transport by ID.
     *
     * @param transportId The ID of the transport
     * @return The transport details view
     */
    public TransportDetailsView getTransportDetailsViewById(String transportId) {
        Optional<Transport> transportOpt = getTransportById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();
            
            List<DestinationDetailsView> destinationViews = transport.getDestinationDocs().stream()
                .map(doc -> {
                    Site destSite = doc.getDestinationId();
                    SiteDetailsView destSiteView = new SiteDetailsView(
                        destSite.getAddress(),
                        destSite.getPhoneNumber(),
                        destSite.getContactPersonName()
                    );
                    return new DestinationDetailsView(
                        doc.getDestinationDocId(),
                        destSiteView,
                        doc.getDeliveryItems().stream()
                            .map(item -> new DeliveredItemDetailsView(item.getProductId(), item.getQuantity()))
                            .collect(Collectors.toList()),
                        TransportStatus.valueOf(doc.getStatus())
                    );
                })
                .collect(Collectors.toList());

            return new TransportDetailsView(
                Integer.parseInt(transport.getTransportId()),
                transport.getDepartureDateTime(),
                transport.getTruck(),
                transport.getDriver(),
                transport.getOriginSite(),
                destinationViews,
                transport.getDepartureWeight(),
                transport.getStatus()
            );
        }
        return null;
    }

    /**
     * Checks if a driver is currently assigned to any active transport.
     * @param driverId The ID of the driver to check
     * @return true if the driver is assigned to an active transport, false otherwise
     * @throws SQLException if there's an error accessing the database
     */
    public boolean isDriverAssignedToActiveTransport(String driverId) throws SQLException {
        return transportDAO.findActiveTransportsByDriver(driverId).size() > 0;
    }

    /**
     * Sets the driver service. Used to handle circular dependency.
     * @param driverService The driver service to set
     */
    public void setDriverService(DriverService driverService) {
        this.driverService = driverService;
    }

    /**
     * Sets the truck service. Used to handle circular dependency.
     * @param truckService The truck service to set
     */
    public void setTruckService(TruckService truckService) {
        this.truckService = truckService;
    }

    public Transport createTransport(Driver driver, Truck truck, Site sourceSite, Site destinationSite,
                                   LocalDateTime plannedStartTime, LocalDateTime plannedEndTime) {
        // Validate driver and truck availability
        if (!driverService.isDriverAvailable(driver.getDriverId())) {
            throw new IllegalStateException("Driver is not available for transport");
        }
        if (!truckService.isTruckAvailable(truck.getPlateNum())) {
            throw new IllegalStateException("Truck is not available for transport");
        }

        // Create and save the transport
        Transport transport = new Transport(
            generateTransportId(),
            truck,
            driver,
            sourceSite,
            plannedStartTime
        );

        try {
            // Save the transport first
            transportDAO.save(TransportMapper.toDTO(transport));
            
            // Mark driver as unavailable
            driverService.markDriverAsUnavailable(driver.getDriverId());
            
            return transport;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating transport", e);
        }
    }

}