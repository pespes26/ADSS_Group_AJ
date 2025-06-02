//package InventorySupplier.SystemService;
//
//import Inventory.DTO.InventoryProductPeriodic;
//import Inventory.DTO.ItemDTO;
//import Inventory.DTO.PeriodicOrderDTO;
//import Inventory.Repository.IPeriodicOrderRepository;
//import Suppliers.DTO.OrderProductDetailsDTO;
//import Suppliers.Repository.IInventoryOrderRepository;
//import Suppliers.Domain.PeriodicOrderController;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
//public class PeriodicOrderService {
//    private final PeriodicOrderController periodicOrderController;
//    private final IPeriodicOrderRepository periodicOrderRepository;
//    private final ScheduledExecutorService scheduler;
//
//    public PeriodicOrderService(IInventoryOrderRepository orderRepository, IPeriodicOrderRepository periodicOrderRepository) {
//        this.scheduler = Executors.newSingleThreadScheduledExecutor();
//        this.periodicOrderController = new PeriodicOrderController(orderRepository);
//        this.periodicOrderRepository = periodicOrderRepository;
//    }
//
//    public void start(int branchId) {
//        Runnable periodicTask = () -> {
//            try {
//                List<PeriodicOrderDTO> periodicOrders = periodicOrderRepository.getAllPeriodicOrders();
//                List<InventoryProductPeriodic> inventoryData = convertToInventoryPeriodic(periodicOrders);
//                List<OrderProductDetailsDTO> detailsList = periodicOrderController.getPeriodicOrderProductDetails(inventoryData, branchId);
//
//                // עדכון המלאי לפי ההזמנה התקופתית
//                for (OrderProductDetailsDTO details : detailsList) {
//                    int catalogNumber = details.getProductId();
//                    int quantity = details.getQuantity();
//                    double costPrice = details.getPrice();
//                    double discount = details.getDiscount();
//                    double costAfterDiscount = costPrice * (1 - discount);
//
//                    for (int i = 0; i < quantity; i++) {
////                        ItemDTO item = new ItemDTO(catalogNumber, branchId, "Warehouse",costAfterDiscount, null, false, null));
////                        ItemRepository.insert(item); // או itemController.addItem(item)
//                    }
//                }
//
//                //                    public ItemDTO(int catalog_number, int branch_id, String storage_location,
////                        String section_in_store, boolean is_defect, String item_expiring_date)
//
//            } catch (Exception e) {
//                System.err.println("Error occurred during periodic order execution: " + e.getMessage());
//            }
//        };
//
//        // אפשר להריץ את זה בתזמון, או פשוט להריץ מיד:
//        periodicTask.run();
//    }
//
//    private List<PeriodicOrderDTO> convertToDTO(List<OrderProductDetails> detailsList) {
//        List<PeriodicOrderDTO> dtos = new ArrayList<>();
//        for (OrderProductDetails details : detailsList) {
//            dtos.add(new PeriodicOrderDTO(
//                    0, // orderId – מיותר בזמן יצירה, כי מסד הנתונים יוצר אותו אוטומטית
//                    details.getProductId(),
//                    details.getQuantity(),
//                    LocalDateTime.now().toString(), // order_date
//                    details.getDiscount(),
//                    details.getSupplierId(),
//                    String.join(",", details.getDeliveryDays()), // daysInTheWeek
//                    details.getAgreementId()
//            ));
//        }
//        return dtos;
//    }
//
//    private List<InventoryProductPeriodic> convertToInventoryPeriodic(List<PeriodicOrderDTO> orders) {
//        List<InventoryProductPeriodic> result = new ArrayList<>();
//        for (PeriodicOrderDTO dto : orders) {
//            result.add(new InventoryProductPeriodic(
//                    dto.getSupplierId(),
//                    dto.getAgreementId(),
//                    dto.getProductCatalogNumber(),
//                    dto.getQuantity()
//            ));
//        }
//        return result;
//    }
//
//    public void stop() {
//        scheduler.shutdown();
//    }
//}
