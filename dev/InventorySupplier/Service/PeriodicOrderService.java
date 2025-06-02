package InventorySupplier.Service;

import Inventory.DTO.InventoryProductPeriodic;
import Inventory.DTO.PeriodicOrderDTO;
import Inventory.Repository.IPeriodicOrderRepository;
import Suppliers.Domain.IInventoryOrderRepository;
import Suppliers.Domain.PeriodicOrderController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PeriodicOrderService {
    private final PeriodicOrderController periodicOrderController;
    private final IPeriodicOrderRepository periodicOrderRepository;
    private final ScheduledExecutorService scheduler;

    public PeriodicOrderService(IInventoryOrderRepository orderRepository,
                                IPeriodicOrderRepository periodicOrderRepository) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.periodicOrderController = new PeriodicOrderController(orderRepository);
        this.periodicOrderRepository = periodicOrderRepository;
    }

    public void start() {
        Runnable periodicTask = () -> {
            try {
                // שליפת ההזמנות התקופתיות מה־Repository
                List<PeriodicOrderDTO> periodicOrders = periodicOrderRepository.getAllPeriodicOrders();

                // המרה ל־InventoryProductPeriodic
                List<InventoryProductPeriodic> inventoryData = convertToInventoryPeriodic(periodicOrders);

                // (בשלב מאוחר יותר) העברה לספקים + עדכון המלאי
                for (InventoryProductPeriodic inv : inventoryData) {
                    System.out.printf("SupplierID: %d | AgreementID: %d | Catalog: %d | Quantity: %d%n",
                            inv.getSupplierId(), inv.getAgreementID(), inv.getCatalogNumber(), inv.getQuantity());
                }

            } catch (Exception e) {
                System.err.println("Error occurred during periodic order execution: " + e.getMessage());
            }
        };

        long weekInSeconds = 7L * 24 * 60 * 60;
        scheduler.scheduleAtFixedRate(periodicTask, 0, weekInSeconds, TimeUnit.SECONDS);
    }

    private List<InventoryProductPeriodic> convertToInventoryPeriodic(List<PeriodicOrderDTO> orders) {
        List<InventoryProductPeriodic> result = new ArrayList<>();
        for (PeriodicOrderDTO dto : orders) {
            result.add(new InventoryProductPeriodic(
                    dto.getSupplierId(),
                    dto.getAgreementId(),
                    dto.getProductCatalogNumber(),
                    dto.getQuantity()
            ));
        }
        return result;
    }

    public void stop() {
        scheduler.shutdown();
    }
}
