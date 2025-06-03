package InventorySupplier.SystemService;

import Inventory.DTO.InventoryProductPeriodic;
import Inventory.DTO.ItemDTO;
import Inventory.DTO.OrderOnTheWayDTO;
import Inventory.DTO.PeriodicOrderDTO;
import Inventory.Repository.*;
import Suppliers.DTO.OrderProductDetailsDTO;
import Suppliers.Repository.IInventoryOrderRepository;
import Suppliers.Domain.PeriodicOrderController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PeriodicOrderService {
    private final PeriodicOrderController periodicOrderController;
    private final IPeriodicOrderRepository periodicOrderRepository;
    private final IOrderOnTheWayRepository onTheWayRepository;
    private final IItemRepository itemRepository;
    private final ScheduledExecutorService scheduler;

    // קונסטרקטור רגיל
    public PeriodicOrderService(IInventoryOrderRepository orderRepository, IPeriodicOrderRepository periodicOrderRepository,
                                IOrderOnTheWayRepository onTheWayRepository, IItemRepository itemRepository) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.periodicOrderController = new PeriodicOrderController(orderRepository);
        this.periodicOrderRepository = periodicOrderRepository;
        this.onTheWayRepository = onTheWayRepository;
        this.itemRepository = itemRepository;
    }

    // קונסטרקטור לטסטים
    public PeriodicOrderService(PeriodicOrderController controller,
                                IPeriodicOrderRepository periodicOrderRepository,
                                IOrderOnTheWayRepository onTheWayRepository,
                                IItemRepository itemRepository) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.periodicOrderController = controller;
        this.periodicOrderRepository = periodicOrderRepository;
        this.onTheWayRepository = onTheWayRepository;
        this.itemRepository = itemRepository;
    }

    public boolean start(int branchId) {
        Runnable periodicTask = () -> {
            try {
                String tomorrow = LocalDate.now().plusDays(1).getDayOfWeek().name(); // לדוגמה: TUESDAY
                List<PeriodicOrderDTO> periodicOrders = periodicOrderRepository.getAllPeriodicOrders()
                        .stream()
                        .filter(order -> order.getBranchId() == branchId &&
                                order.getDaysInTheWeek().toUpperCase().contains(tomorrow))
                        .toList();

                List<InventoryProductPeriodic> inventoryData = convertToInventoryPeriodic(periodicOrders);
                List<OrderProductDetailsDTO> detailsList = periodicOrderController.getPeriodicOrderProductDetails(inventoryData, branchId);

                List<PeriodicOrderDTO> newPeriodicOrders = convertToDTO(detailsList, branchId);

                for (OrderProductDetailsDTO details : detailsList) {
                    int catalogNumber = details.getProductId();
                    int quantity = details.getQuantity();
                    double costPrice = details.getPrice();
                    double discount = details.getDiscount();
                    double finalPrice = costPrice * (1 - discount);

                    String expectedDeliveryDate = LocalDate.now().plusDays(1).toString();

                    // שמירת כל הזמנה שנשלחה לטבלת orders_on_the_way
                    OrderOnTheWayDTO newOrder = new OrderOnTheWayDTO(
                            0,
                            catalogNumber,
                            quantity,
                            details.getSupplierId(),
                            branchId,
                            expectedDeliveryDate,
                            LocalDate.now().toString(),
                            details.getAgreementId(),
                            true
                    );
                    onTheWayRepository.insert(newOrder); // ✅ משתמש ב־mock

                    // הכנסת פריטים חדשים למחסן
                    for (int i = 0; i < quantity; i++) {
                        ItemDTO item = new ItemDTO(
                                catalogNumber,
                                branchId,
                                "Warehouse",
                                null,
                                false,
                                null
                        );
                        itemRepository.addItem(item); // ✅ משתמש ב־mock
                    }
                }

            } catch (Exception e) {
                System.err.println("❌ Error occurred during periodic order execution: " + e.getMessage());
                e.printStackTrace();
            }
        };

        periodicTask.run(); // קריאה מיידית
        return false;
    }

    private List<PeriodicOrderDTO> convertToDTO(List<OrderProductDetailsDTO> detailsList, int branchId) {
        List<PeriodicOrderDTO> dtos = new ArrayList<>();
        for (OrderProductDetailsDTO details : detailsList) {
            dtos.add(new PeriodicOrderDTO(
                    0,
                    details.getProductId(),
                    details.getQuantity(),
                    LocalDateTime.now().toString(),
                    details.getDiscount(),
                    details.getSupplierId(),
                    details.getSupplierName(),
                    String.join(",", details.getDeliveryDays()),
                    details.getAgreementId(),
                    branchId
            ));
        }
        return dtos;
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
