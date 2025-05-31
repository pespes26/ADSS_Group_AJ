package com.superli.deliveries.storage;

import com.superli.deliveries.domain.Transport;
import com.superli.deliveries.domain.ports.ITransportRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the in-memory storage for Transport objects.
 * Acts as an in-memory repository for transports.
 * Implements the ITransportRepository interface.
 */
public class TransportRepository implements ITransportRepository {

    private final Map<Integer, Transport> transportMap;

    /**
     * Constructs a new TransportRepository.
     */
    public TransportRepository() {
        this.transportMap = new ConcurrentHashMap<>();
    }

    @Override
    public void save(Transport transport) {
        Objects.requireNonNull(transport, "Transport cannot be null");
        transportMap.put(transport.getTransportId(), transport);
        System.out.println("Transport saved/updated in repository: " + transport.getTransportId());
    }

    @Override
    public Optional<Transport> findById(int transportId) {
        return Optional.ofNullable(transportMap.get(transportId));
    }

    @Override
    public Collection<Transport> findAll() {
        return Collections.unmodifiableCollection(transportMap.values());
    }

    @Override
    public Optional<Transport> deleteById(int transportId) {
        Transport removed = transportMap.remove(transportId);
        if (removed != null) {
            System.out.println("Transport removed from repository: " + transportId);
        }
        return Optional.ofNullable(removed);
    }

    @Override
    public void clearAll() {
        transportMap.clear();
        System.out.println("TransportRepository cleared.");
    }
}
