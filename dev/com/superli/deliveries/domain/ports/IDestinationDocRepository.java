package com.superli.deliveries.domain.ports;

import com.superli.deliveries.domain.core.DestinationDoc;

import java.util.Collection;
import java.util.Optional;

public interface IDestinationDocRepository {
    void save(DestinationDoc doc);
    Optional<DestinationDoc> findById(int destinationDocId);
    Collection<DestinationDoc> findAll();
    Optional<DestinationDoc> deleteById(int destinationDocId);
    void clearAll();
}
