package Integration_And_Unit_Tests;

import Inventory.DTO.PeriodicOrderDTO;
import Inventory.Repository.*;
import InventorySupplier.SystemService.PeriodicOrderService;
import Suppliers.Repository.IInventoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

public class PeriodicOrderServiceTest {

    private IPeriodicOrderRepository periodicOrderRepository;
    private IItemRepository itemRepository;
    private IInventoryOrderRepository orderRepository;
    private PeriodicOrderService service;

    @BeforeEach
    public void setUp() {
        periodicOrderRepository = mock(IPeriodicOrderRepository.class);
        itemRepository = mock(IItemRepository.class);
        orderRepository = mock(IInventoryOrderRepository.class);

        service = new PeriodicOrderService(
                orderRepository,
                periodicOrderRepository,
                itemRepository
        );
    }

    @Test
    public void givenPeriodicOrders_whenStart_thenProcessOrdersForToday() throws Exception {
        String today = LocalDate.now().getDayOfWeek().name();

        PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO(
                0, 1001, 2, "2025-06-03", 0.0,
                200, "Supplier A", today, 300, 1,
                today, null, null, 0
        );
        when(periodicOrderRepository.getAllPeriodicOrders())
                .thenReturn(List.of(periodicOrder));

        boolean result = service.start(1);

        assert(result);
        verify(itemRepository, times(2)).addItem(any());
    }

    @Test
    public void givenNoOrdersForToday_whenStart_thenNoProcessing() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).getDayOfWeek().name();

        PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO(
                0, 1001, 2, "2025-06-03", 0.0,
                200, "Supplier A", tomorrow, 300, 1,
                tomorrow, null, null, 0
        );
        when(periodicOrderRepository.getAllPeriodicOrders())
                .thenReturn(List.of(periodicOrder));

        boolean result = service.start(1);

        assert(!result);
        verify(itemRepository, never()).addItem(any());
    }
}
