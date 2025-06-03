package Integration_And_Unit_Tests;

/**
 * Test class for the PeriodicOrderService which handles periodic inventory orders.
 * These tests verify the service's ability to process periodic orders, interact with suppliers,
 * and manage inventory updates. The class uses Mockito to mock dependencies and verify
 * correct interaction between system components.
 */

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
    private PeriodicOrderController periodicOrderController;
    private PeriodicOrderService service;

    @BeforeEach
    public void setUp() {
        periodicOrderRepository = mock(IPeriodicOrderRepository.class);
        orderOnTheWayRepository = mock(IOrderOnTheWayRepository.class);
        itemRepository = mock(IItemRepository.class);
        periodicOrderController = mock(PeriodicOrderController.class);
        IInventoryOrderRepository orderRepository = mock(IInventoryOrderRepository.class);
        periodicOrderController = new PeriodicOrderController(orderRepository);
        service = new PeriodicOrderService(orderRepository, periodicOrderRepository,
                orderOnTheWayRepository, itemRepository);
    }

    /**
     * Tests the successful processing of a periodic order scheduled for tomorrow.
     * This test verifies that:
     * 1. The service correctly identifies orders due for tomorrow
     * 2. Order details are properly retrieved from the supplier controller
     * 3. The order is recorded in the OrderOnTheWay repository
     * 4. The correct number of items are added to inventory
     *
     * @throws Exception if any database or processing errors occur
     */
    @Test
    public void givenPeriodicOrderForTomorrow_whenStart_thenOrderProcessedAndItemsAdded() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).getDayOfWeek().name();
        int branchId = 1;

        // Arrange - Create periodic order for tomorrow
        PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO(
                0, 1001, 2, "2025-06-03", 0.0,
                200, "Supplier A", tomorrow, 300, branchId,
                tomorrow, null, null, 0
        );
        when(periodicOrderRepository.getAllPeriodicOrders()).thenReturn(List.of(periodicOrder));

        // Arrange - Product details retrieved from supplier controller
        OrderProductDetailsDTO details = new OrderProductDetailsDTO(
                200, "Supplier A", new String[]{tomorrow}, 300,
                1001, 20.0, 0.1, 2
        );
        when(periodicOrderController.getPeriodicOrderProductDetails(any(), eq(1L)))
                .thenReturn(List.of(details));

        // Act - Process the periodic order
        service.start(branchId);

        // Assert - Verify all expected interactions occurred
        verify(periodicOrderRepository).getAllPeriodicOrders();
        verify(periodicOrderController).getPeriodicOrderProductDetails(any(), eq(1L));
        verify(orderOnTheWayRepository).insert(any());
        verify(itemRepository, times(2)).addItem(any()); // Verify 2 items added (matching quantity)
    }

    /**
     * Tests the service's error handling when the controller throws an exception.
     * Verifies that exceptions from the controller are properly caught and handled,
     * preventing them from propagating up the call stack.
     * Also ensures that no inventory changes occur when an error is encountered.
     *
     * @throws Exception if any unexpected errors occur during test execution
     */
    @Test
    public void givenControllerThrowsException_whenStart_thenHandledGracefully() throws Exception {
        PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO(
                0, 1001, 2, "2025-06-03", 0.0,
                200, "Supplier A", LocalDate.now().plusDays(1).getDayOfWeek().name(), 300, 1,
                LocalDate.now().plusDays(1).getDayOfWeek().name(), null, null, 0
        );
        when(periodicOrderRepository.getAllPeriodicOrders()).thenReturn(List.of(periodicOrder));
        when(periodicOrderController.getPeriodicOrderProductDetails(any(), anyLong()))
                .thenThrow(new RuntimeException("Simulated Failure"));

        // Act - No need to expect exception, just verify it's handled
        service.start(1);

        // Assert - Verify no changes occurred due to error
        verify(orderOnTheWayRepository, never()).insert(any());
        verify(itemRepository, never()).addItem(any());
    }
}
