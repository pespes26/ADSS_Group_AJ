package domain.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages archived shifts in the system.
 */
public class ArchivedShifts {
    private final List<Shift> pastShifts;

    /**
     * Constructs a new ArchivedShifts with an empty list.
     */
    public ArchivedShifts() {
        this.pastShifts = new ArrayList<>();
    }

    /**
     * Adds completed shifts to the archive.
     *
     * @param shifts The shifts to add to the archive.
     */
    public void archiveShift(List<Shift> shifts) {
        if (shifts == null) {
            throw new IllegalArgumentException("Shifts list cannot be null.");
        }
        pastShifts.addAll(shifts);
    }

    /**
     * Gets all archived shifts.
     *
     * @return A copy of the list of archived shifts.
     */
    public List<Shift> getAllArchivedShifts() {
        return List.copyOf(pastShifts);
    }

    /**
     * Clears the archive of all shifts.
     */
    public void clearArchive() {
        pastShifts.clear();
    }

    @Override
    public String toString() {
        return "ArchivedShifts{" +
                "pastShifts=" + pastShifts +
                '}';
    }
}
