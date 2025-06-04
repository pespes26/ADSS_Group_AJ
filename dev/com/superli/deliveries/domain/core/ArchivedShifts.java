package com.superli.deliveries.domain.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages archived shifts in the system.
 */
public class ArchivedShifts {
    private List<Shift> pastShifts;

    public ArchivedShifts() {
        this.pastShifts = new ArrayList<>();
    }

    /**
     * Adds a completed shift to the archive.
     * @param shift - the shift to add to the archive.
     */
    public void archiveShift(List<Shift> shifts) {
        pastShifts.addAll(shifts);
    }

    /**
     * @return List of all archived shifts.
     */
    public List<Shift> getAllArchivedShifts() {
        return pastShifts;
    }

    /**
     * Clears the archive shifts.
     */
    public void clearArchive() {
        pastShifts.clear();
    }
}
