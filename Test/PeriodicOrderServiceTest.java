

import Inventory.DTO.InventoryProductPeriodic;
import Inventory.DTO.PeriodicOrderDTO;
import Inventory.Repository.*;
import InventorySupplier.SystemService.PeriodicOrderService;
import Suppliers.DTO.OrderProductDetailsDTO;
import Suppliers.Domain.PeriodicOrderController;
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

        // משתמש בקונסטרקטור שמקבל controller אמיתי
        service = new PeriodicOrderService(periodicOrderController, periodicOrderRepository,
                orderOnTheWayRepository, itemRepository);
    }

    @Test
    public void givenPeriodicOrderForTomorrow_whenStart_thenOrderProcessedAndItemsAdded() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).getDayOfWeek().name();
        int branchId = 1;

        // Arrange – הזמנה תקופתית מתאימה למחר
        PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO(
                0, 1001, 2, "2025-06-03", 0.0,
                200, "ספק א'", tomorrow, 300, branchId
        );
        when(periodicOrderRepository.getAllPeriodicOrders()).thenReturn(List.of(periodicOrder));

        // Arrange – פרטי המוצר שהוזמן בפועל מהקונטרולר
        OrderProductDetailsDTO details = new OrderProductDetailsDTO(
                200, "ספק א'", new String[]{tomorrow}, 300,
                1001, 20.0, 0.1, 2
        );
        when(periodicOrderController.getPeriodicOrderProductDetails(any(), eq(1L)))
                .thenReturn(List.of(details));

        // Act
        service.start(branchId);

        // Assert – לוודא שהכל התרחש
        verify(periodicOrderRepository).getAllPeriodicOrders();
        verify(periodicOrderController).getPeriodicOrderProductDetails(any(), eq(1L));
        verify(orderOnTheWayRepository).insert(any());
        verify(itemRepository, times(2)).addItem(any()); // כי quantity = 2
    }

    //כאשר הקונטרולר מחזיר חריגה – לא נזרקת חריגה כלפי מעלה
    @Test
    public void givenControllerThrowsException_whenStart_thenHandledGracefully() throws Exception {
        PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO(
                0, 1001, 2, "2025-06-03", 0.0,
                200, "ספק א'", LocalDate.now().plusDays(1).getDayOfWeek().name(), 300, 1
        );
        when(periodicOrderRepository.getAllPeriodicOrders()).thenReturn(List.of(periodicOrder));
        when(periodicOrderController.getPeriodicOrderProductDetails(any(), anyLong()))
                .thenThrow(new RuntimeException("Simulated Failure"));

        // Act – אין צורך לצפות ל־Exception, רק לוודא שלא נזרקת
        service.start(1);

        // Assert
        verify(orderOnTheWayRepository, never()).insert(any());
        verify(itemRepository, never()).addItem(any());
    }


}
