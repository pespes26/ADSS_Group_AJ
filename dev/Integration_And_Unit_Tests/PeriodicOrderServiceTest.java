package Integration_And_Unit_Tests;

import Inventory.DTO.PeriodicOrderDTO;
import Inventory.Repository.*;
import InventorySupplier.SystemService.PeriodicOrderService;
import Suppliers.DTO.OrderProductDetailsDTO;
import Suppliers.Domain.PeriodicOrderController;
import Suppliers.Repository.IInventoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

public class PeriodicOrderServiceTest {

    private IPeriodicOrderRepository periodicOrderRepository;
    private IOrderOnTheWayRepository orderOnTheWayRepository;
    private IItemRepository itemRepository;
    private IInventoryOrderRepository orderRepository;
    private PeriodicOrderController periodicOrderController;
    private PeriodicOrderService service;

    @BeforeEach
    public void setUp() {
        periodicOrderRepository = mock(IPeriodicOrderRepository.class);
        orderOnTheWayRepository = mock(IOrderOnTheWayRepository.class);
        itemRepository = mock(IItemRepository.class);
        orderRepository = mock(IInventoryOrderRepository.class);
        periodicOrderController = mock(PeriodicOrderController.class);

        service = new PeriodicOrderService(
                orderRepository,
                periodicOrderRepository,
                orderOnTheWayRepository,
                itemRepository,
                periodicOrderController
        );
    }



    @Test
    public void givenControllerThrowsException_whenStart_thenHandledGracefully() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).getDayOfWeek().name();

        PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO(
                0, 1001, 2, "2025-06-03", 0.0,
                200, "Supplier A", tomorrow, 300, 1,
                tomorrow, null, null, 0
        );
        when(periodicOrderRepository.getAllPeriodicOrders())
                .thenReturn(List.of(periodicOrder));

        when(periodicOrderController.getPeriodicOrderProductDetails(any(), anyLong()))
                .thenThrow(new RuntimeException("Simulated Failure"));

        service.start(1);

        verify(orderOnTheWayRepository, never()).insert(any());
        verify(itemRepository, never()).addItem(any());
    }
}
