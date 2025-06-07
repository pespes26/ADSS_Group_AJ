package presentation;

import java.time.LocalDateTime;
import java.util.List;

import domain.core.*;

public class TransportDetailsView {
    private final int transportId;
    private final LocalDateTime departureDateTime;
    private final Truck truck;
    private final Driver driver;
    private final Site originSite;
    private final List<DestinationDetailsView> destinationDetails;
    private final float departureWeight;
    private final TransportStatus status;

    public TransportDetailsView(int transportId, LocalDateTime departureDateTime, Truck truck, Driver driver,
                                Site originSite, List<DestinationDetailsView> destinationDetails, float departureWeight, TransportStatus status) {
        if (truck == null || driver == null || originSite == null || destinationDetails == null || status == null) {
            throw new IllegalArgumentException("None of the fields can be null.");
        }

        this.transportId = transportId;
        this.departureDateTime = departureDateTime;
        this.truck = truck;
        this.driver = driver;
        this.originSite = originSite;
        this.destinationDetails = List.copyOf(destinationDetails);
        this.departureWeight = departureWeight;
        this.status = status;
    }

    // Getters
    public int getTransportId() {
        return transportId;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public Truck getTruck() {
        return truck;
    }

    public Driver getDriver() {
        return driver;
    }

    public Site getOriginSite() {
        return originSite;
    }

    public List<DestinationDetailsView> getDestinationDetails() {
        return destinationDetails;
    }

    public float getDepartureWeight() {
        return departureWeight;
    }

    public TransportStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "TransportDetailsView{" +
                "transportId=" + transportId +
                ", departureDateTime=" + departureDateTime +
                ", truck=" + truck +
                ", driver=" + driver +
                ", originSite=" + originSite +
                ", destinationDetailsCount=" + destinationDetails.size() +
                ", departureWeight=" + departureWeight +
                ", status=" + status +
                '}';
    }
}