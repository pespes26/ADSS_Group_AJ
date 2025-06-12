package com.superli.deliveries.application.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
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

/**
 * Service layer for managing Transport operations and business logic.
 * Enhanced with additional methods for better separation of concerns.
 */
public class TransportService {

    private final TransportDAO transportDAO;
    private final DriverService driverService;
    private final TruckService truckService;
    private final SiteService siteService;

    // Counter for generating sequential transport IDs
    private int nextTransportId = 1;

    /**
     * Constructs a new TransportService with required dependencies.
     *
     * @param transportDAO DAO for transports
     * @param driverService Service for driver operations
     * @param truckService Service for truck operations
     * @param siteService Service for site operations
     */
    public TransportService(TransportDAO transportDAO,
                            DriverService driverService,
                            TruckService truckService,
                            SiteService siteService) {
        this.transportDAO = transportDAO;
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
        try {
            nextTransportId = transportDAO.findAll().stream()
                    .mapToInt(dto -> Integer.parseInt(dto.getTransportId()))
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
        return String.valueOf(nextTransportId++);
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
                            return TransportMapper.fromDTO(dto, truck.get(), driver.get(), site.get());
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
                    return Optional.of(TransportMapper.fromDTO(dto, truck.get(), driver.get(), site.get()));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting transport by id: " + transportId, e);
        }
    }

    /**
     * Saves a transport to the repository.
     *
     * @param transport The transport to save
     */
    public void saveTransport(Transport transport) {
        try {
            transportDAO.save(TransportMapper.toDTO(transport));
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
        List<Driver> availableDrivers = driverService.getAvailableDrivers();

        if (availableTrucks.isEmpty() || availableDrivers.isEmpty()) {
            return Optional.empty();
        }

        for (Truck truck : availableTrucks) {
            for (Driver driver : availableDrivers) {
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
    public Optional<Transport> createTransportManualWithSite(Truck truck, Driver driver, 
            Site site, LocalDateTime departureDateTime) {
        if (!driver.getLicenseType().equals(truck.getRequiredLicenseType())) {
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

        String transportId = generateTransportId();
        Transport transport = new Transport(transportId, truck, driver, originSite);
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
     * Updates the status of a transport.
     *
     * @param transportId The ID of the transport
     * @param newStatus The new status
     * @return true if updated successfully, false otherwise
     */
    public boolean updateTransportStatus(String transportId, TransportStatus newStatus) {
        Optional<Transport> transportOpt = getTransportById(transportId);
        if (transportOpt.isPresent()) {
            Transport transport = transportOpt.get();
            transport.setStatus(newStatus);
            saveTransport(transport);
            return true;
        }
        return false;
    }

    /**
     * Releases resources associated with a transport.
     *
     * @param transport The transport whose resources to release
     */
    private void releaseTransportResources(Transport transport) {
        // Release truck
        truckService.releaseTruck(transport.getTruck());

        // Release driver
        driverService.releaseDriver(transport.getDriver());
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
}