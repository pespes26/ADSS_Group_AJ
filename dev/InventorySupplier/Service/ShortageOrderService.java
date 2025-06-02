package InventorySupplier.Service;


import Suppliers.Domain.*;

public class ShortageOrderService implements WakeUpListener {
    private final OrderByShortageController orderByShortageController;

    public ShortageOrderService(IInventoryOrderRepository orderRepository) {
        this.orderByShortageController= new OrderByShortageController(orderRepository);

    }


    @Override
    public void onWakeUp() {
        // TODO HashMap<Integer,Integer> inventoryProducts = InventoryController.getProductsShortage();
//        List<OrderProductDetails> orderProductDetailsList =  orderByShortageController.getShortageOrderProductDetails(inventoryProducts);
        //TODO InventoryController.writeTheSuppliersDetails(List<OrderProductDetails> orderProductDetailsList);
    }
}
