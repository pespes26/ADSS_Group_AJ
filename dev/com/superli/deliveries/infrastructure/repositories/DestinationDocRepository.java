package com.superli.deliveries.infrastructure.repositories;

import com.superli.deliveries.domain.core.DestinationDoc;
import com.superli.deliveries.domain.ports.IDestinationDocRepository;

import java.util.*;

/**
 * In-memory repository for managing DestinationDoc objects.
 * Implements the IDestinationDocRepository interface.
 */
public class DestinationDocRepository implements IDestinationDocRepository {

    private final Map<Integer, DestinationDoc> destinationMap; // Key: destinationDocId

    public DestinationDocRepository() {
        this.destinationMap = new HashMap<>();
    }

    @Override
    public void save(DestinationDoc destinationDoc) {
        Objects.requireNonNull(destinationDoc, "DestinationDoc cannot be null.");
        destinationMap.put(destinationDoc.getDestinationDocId(), destinationDoc);
        System.out.println("DestinationDoc saved: " + destinationDoc.getDestinationDocId());
    }

    @Override
    public Optional<DestinationDoc> findById(int destinationDocId) {
        return Optional.ofNullable(destinationMap.get(destinationDocId));
    }

    @Override
    public Collection<DestinationDoc> findAll() {
        return Collections.unmodifiableCollection(destinationMap.values());
    }

    @Override
    public Optional<DestinationDoc> deleteById(int destinationDocId) {
        DestinationDoc removed = destinationMap.remove(destinationDocId);
        if (removed != null) {
            System.out.println("DestinationDoc removed: " + destinationDocId);
        }
        return Optional.ofNullable(removed);
    }

    @Override
    public void clearAll() {
        destinationMap.clear();
        System.out.println("DestinationDocRepository cleared.");
    }
}
