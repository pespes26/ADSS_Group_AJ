package ControllerInventorySupplier;

import Suppliers.Domain.IInventoryOrderRepository;
import Suppliers.Domain.InventoryOrderRepositoryImpl;
import Suppliers.dataaccess.DAO.*;

public class Initializer {
    IProductSupplierDAO productSupplierDAO = new JdbcProductSupplierDAO();
    IDiscountDAO discountDAO = new JdbcDiscountDAO();
    IAgreementDAO agreementDAO = new JdbcAgreementDAO();
    ISupplierDAO supplierDAO = new JdbcSupplierDAO();
    IOrderDAO orderDAO = new JdbcOrderDAO();
    // יצירת Repository
    IInventoryOrderRepository orderRepository = new InventoryOrderRepositoryImpl(productSupplierDAO, discountDAO, orderDAO, supplierDAO, agreementDAO);

    public IInventoryOrderRepository getSupplierOrderRepository (){
        return orderRepository;
    }
}
