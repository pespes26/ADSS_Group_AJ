package com.superli.deliveries.dto.del;

import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.core.Site;

public class DestinationDocDTO {
    private String destinationDocId;
    private String transportId;
    private String siteId;
    private String status;

    public DestinationDocDTO() {}

    public DestinationDocDTO(String destinationDocId, String transportId, String siteId, String status) {
        this.destinationDocId = destinationDocId;
        this.transportId = transportId;
        this.siteId = siteId;
        this.status = status;
    }

    public String getDestinationDocId() { return destinationDocId; }

    public String getTransportId() { return transportId; }
    public void setTransportId(String transportId) { this.transportId = transportId; }

    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}