package com.superli.deliveries.storage;

// --- ייבואים ---
import com.superli.deliveries.domain.Truck;
// ודא שה-import הזה מצביע על המיקום הנכון של הממשק אצלך
import com.superli.deliveries.domain.ports.ITruckRepository; // או com.superli.deliveries.domain.*
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
// אתה יכול להחליף ל-HashMap אם תרצה
import java.util.concurrent.ConcurrentHashMap;
// import java.util.HashMap;

/**
 * Manages the in-memory storage for Truck objects.
 * Acts as an in-memory repository for trucks.
 * This class implements the ITruckRepository interface.
 */
//             vvvvvvvvvvvvvvvvvvvvvvvvvvvvv הוספת implements
public class TruckRepository implements ITruckRepository {

    // שיניתי את שם המשתנה שיהיה ברור יותר מה המפתח
    private final Map<String, Truck> truckMapByPlate; // Key: licensePlateNumber

    /**
     * Constructs a new TruckRepository.
     */
    public TruckRepository() {
        this.truckMapByPlate = new ConcurrentHashMap<>(); // או new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public void save(Truck truck) { // <-- שינוי שם מ-addTruck
        Objects.requireNonNull(truck, "Truck cannot be null");
        Objects.requireNonNull(truck.getPlateNum(), "Truck license plate number cannot be null");
        truckMapByPlate.put(truck.getPlateNum(), truck);
        System.out.println("Truck saved/updated: " + truck.getPlateNum());
    }

    @Override // <-- הוספת Override
    public Optional<Truck> findByPlate(String licensePlateNumber) { // <-- שינוי שם מ-getTruckByLicensePlate
        return Optional.ofNullable(truckMapByPlate.get(licensePlateNumber));
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public Collection<Truck> findAll() { // <-- שינוי שם מ-getAllTrucks
        return Collections.unmodifiableCollection(truckMapByPlate.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public Optional<Truck> deleteByPlate(String licensePlateNumber) { // <-- שינוי שם מ-deleteTruck
        if (licensePlateNumber != null) {
            Truck removed = truckMapByPlate.remove(licensePlateNumber);
            if(removed != null) {
                System.out.println("Truck removed: " + licensePlateNumber);
            }
            return Optional.ofNullable(removed);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public void clearAll() {
        truckMapByPlate.clear();
        System.out.println("TruckRepository cleared.");
    }
}