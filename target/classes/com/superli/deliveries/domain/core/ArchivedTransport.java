package com.superli.deliveries.domain.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an archived transport event containing truck, driver, origin, delivery list, transport status, and archived details.
 */
public class ArchivedTransport extends Transport {

    private final LocalDateTime archivedDateTime;
    private final String finalStatus;
    private List<String> archiveNotes;

    /**
     * Constructs a new ArchivedTransport.
     *
     * @param transportId Unique identifier of the transport.
     * @param departureDateTime Planned departure date and time.
     * @param truck Assigned truck.
     * @param driver Assigned driver.
     * @param originSite Origin site of the transport.
     * @param status Initial status of the transport.
     * @param archivedDateTime Date and time when the transport was archived.
     * @param finalStatus Final status of the transport.
     */
    public ArchivedTransport(int transportId, LocalDateTime departureDateTime, Truck truck,
                             Driver driver, Site originSite, TransportStatus status,
                             LocalDateTime archivedDateTime, String finalStatus) {
        super(String.valueOf(transportId), truck, driver, originSite, departureDateTime);
        super.setStatus(status);
        if (archivedDateTime == null || finalStatus == null) {
            throw new IllegalArgumentException("Archived date and final status cannot be null.");
        }

        this.archivedDateTime = archivedDateTime;
        this.finalStatus = finalStatus;
        this.archiveNotes = new ArrayList<>();
    }

    // --- Getters ---

    public LocalDateTime getArchivedDateTime() {
        return archivedDateTime;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public List<String> getArchiveNotes() {
        return List.copyOf(archiveNotes);
    }

    // --- Setters ---

    public void addArchiveNote(String note) {
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException("Archive note cannot be null or blank.");
        }
        archiveNotes.add(note);
    }

    // --- Standard Methods ---

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("\n====== ARCHIVED TRANSPORT DETAILS ======\n");
        sb.append("Archived Date: ").append(archivedDateTime).append("\n");
        sb.append("Final Status: ").append(finalStatus).append("\n");

        sb.append("Archive Notes: ").append(archiveNotes.size()).append("\n");
        if (!archiveNotes.isEmpty()) {
            int i = 1;
            for (String note : archiveNotes) {
                sb.append("  ").append(i++).append(". ").append(note).append("\n");
            }
        }

        sb.append("==============================");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArchivedTransport)) return false;
        ArchivedTransport that = (ArchivedTransport) o;
        return super.equals(o) && Objects.equals(archivedDateTime, that.archivedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), archivedDateTime);
    }
}
