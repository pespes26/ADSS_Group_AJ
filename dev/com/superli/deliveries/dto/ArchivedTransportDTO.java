package com.superli.deliveries.dto;

import java.util.List;

public class ArchivedTransportDTO {
    private int id;
    private String departureDateTime;
    private int truckId;
    private int driverId;
    private int originSiteId;
    private String status;
    private String archivedDateTime;
    private String finalStatus;
    private List<String> archiveNotes;

    public ArchivedTransportDTO() {}

    public ArchivedTransportDTO(int id, String departureDateTime, int truckId, int driverId,
                                int originSiteId, String status, String archivedDateTime,
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDepartureDateTime() { return departureDateTime; }
    public void setDepartureDateTime(String departureDateTime) { this.departureDateTime = departureDateTime; }

    public int getTruckId() { return truckId; }
    public void setTruckId(int truckId) { this.truckId = truckId; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public int getOriginSiteId() { return originSiteId; }
    public void setOriginSiteId(int originSiteId) { this.originSiteId = originSiteId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getArchivedDateTime() { return archivedDateTime; }
    public void setArchivedDateTime(String archivedDateTime) { this.archivedDateTime = archivedDateTime; }

    public String getFinalStatus() { return finalStatus; }
    public void setFinalStatus(String finalStatus) { this.finalStatus = finalStatus; }

    public List<String> getArchiveNotes() { return archiveNotes; }
    public void setArchiveNotes(List<String> archiveNotes) { this.archiveNotes = archiveNotes; }
}