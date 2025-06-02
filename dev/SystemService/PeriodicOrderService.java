package SystemService;

import Suppliers.DTO.InventoryProductPeriodic;
import Suppliers.DTO.OrderProductDetails;
import Suppliers.Domain.IInventoryOrderRepository;
import Suppliers.Domain.InventoryOrderRepositoryImpl;
import Suppliers.Domain.OrderByShortageController;
import Suppliers.Domain.PeriodicOrderController;
import Suppliers.dataaccess.DAO.*;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicOrderService {
    private final PeriodicOrderController periodicOrderController;
    private final ScheduledExecutorService scheduler;

    public PeriodicOrderService( IInventoryOrderRepository orderRepository) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.periodicOrderController = new PeriodicOrderController(orderRepository);
        }


    public void start() {
        Runnable periodicTask = () -> {
            try {
                // בקשת פריטים להזמנה תקופתית
                //TODO List<InventoryProductPeriodic> request = inventoryController.getPeriodicOrderData();


                // ביצוע הזמנה
                //TODO List<OrderProductDetails> supplierData = periodicOrderController.getPeriodicOrderProductDetails(request);

                // עדכון המלאי שהוזמנה הזמנה
                //TODO inventoryController.registerPendingOrder(supplierData);

                System.out.println("Periodic order was successfully completed.");
            } catch (Exception e) {
                System.err.println("Error occurred during periodic order execution: " + e.getMessage());
            }
        };

        // הפעלת המשימה כל שבוע (7 ימים = 7 * 24 * 60 * 60 שניות)
        long weekInSeconds = 7L * 24 * 60 * 60;

        scheduler.scheduleAtFixedRate(
                periodicTask,
                0,                // התחלה מידית
                weekInSeconds,    // מרווח של שבוע
                TimeUnit.SECONDS
        );
    }

    public void stop() {
        scheduler.shutdown();
    }
    }