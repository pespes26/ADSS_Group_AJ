package com.superli.deliveries.dto;

import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Site;

public class DestinationDocDTO {
    private int destinationDocId;
    private int transportId;
    private int siteId;
    private String status;

    public DestinationDocDTO() {}

    public DestinationDocDTO(int destinationDocId, int transportId, int siteId, String status) {
        this.destinationDocId = destinationDocId;
        this.transportId = transportId;
        this.siteId = siteId;
        this.status = status;
    }

    public int getDestinationDocId() { return destinationDocId; }

    public int getTransportId() { return transportId; }
    public void setTransportId(int transportId) { this.transportId = transportId; }

    public int getSiteId() { return siteId; }
    public void setSiteId(int siteId) { this.siteId = siteId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}