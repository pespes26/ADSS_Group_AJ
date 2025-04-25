package com.superli.deliveries.presentation;

import com.superli.deliveries.domain.TransportStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Summary view of a transport, used in overview displays.
 */
public class TransportSummaryView {

    private final int transportId;
    private final LocalDateTime departureDateTime;
    private final SiteDetailsView originSite;
    private final List<DestinationDetailsView> destinationsList;
    private final float totalWeight;
    private final TransportStatus status;

    public TransportSummaryView(int transportId,
                                LocalDateTime departureDateTime,
                                SiteDetailsView originSite,
                                List<DestinationDetailsView> destinationsList,
                                float totalWeight,
                                TransportStatus status) {
        if (originSite == null || destinationsList == null || status == null) {
            throw new IllegalArgumentException("originSite, destinationsList and status must not be null");
        }

        this.transportId = transportId;
        this.departureDateTime = departureDateTime;
        this.originSite = originSite;
        this.destinationsList = List.copyOf(destinationsList);
        this.totalWeight = totalWeight;
        this.status = status;
    }

    public int getTransportId() {
        return transportId;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public SiteDetailsView getOriginSite() {
        return originSite;
    }

    public List<DestinationDetailsView> getDestinationsList() {
        return destinationsList;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    public TransportStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "TransportSummaryView{" +
                "transportId=" + transportId +
                ", departureDateTime=" + departureDateTime +
                ", originSite=" + originSite +
                ", totalWeight=" + totalWeight +
                ", status=" + status +
                ", destinations=" + destinationsList.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportSummaryView)) return false;
        TransportSummaryView that = (TransportSummaryView) o;
        return transportId == that.transportId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportId);
    }
}
