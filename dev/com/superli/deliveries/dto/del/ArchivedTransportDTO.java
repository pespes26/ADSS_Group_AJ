package com.superli.deliveries.dto.del;

import java.util.List;

public class ArchivedTransportDTO {
    private String id;
    private String departureDateTime;
    private String truckId;
    private String driverId;
    private String originSiteId;
    private String status;
    private String archivedDateTime;
    private String finalStatus;
    private List<String> archiveNotes;

    public ArchivedTransportDTO() {}

    public ArchivedTransportDTO(String id, String departureDateTime, String truckId, String driverId,
                                String originSiteId, String status, String archivedDateTime,
                                String finalStatus, List<String> archiveNotes) {
        this.id = id;
        this.departureDateTime = departureDateTime;
        this.truckId = truckId;
        this.driverId = driverId;
        this.originSiteId = originSiteId;
        this.status = status;
        this.archivedDateTime = archivedDateTime;
        this.finalStatus = finalStatus;
        this.archiveNotes = archiveNotes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDepartureDateTime() { return departureDateTime; }
    public void setDepartureDateTime(String departureDateTime) { this.departureDateTime = departureDateTime; }

    public String getTruckId() { return truckId; }
    public void setTruckId(String truckId) { this.truckId = truckId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getOriginSiteId() { return originSiteId; }
    public void setOriginSiteId(String originSiteId) { this.originSiteId = originSiteId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getArchivedDateTime() { return archivedDateTime; }
    public void setArchivedDateTime(String archivedDateTime) { this.archivedDateTime = archivedDateTime; }

    public String getFinalStatus() { return finalStatus; }
    public void setFinalStatus(String finalStatus) { this.finalStatus = finalStatus; }

    public List<String> getArchiveNotes() { return archiveNotes; }
    public void setArchiveNotes(List<String> archiveNotes) { this.archiveNotes = archiveNotes; }
}