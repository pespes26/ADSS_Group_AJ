package InventorySupplier.SystemService;

import Inventory.DTO.ShortageOrderDTO;
import Inventory.Repository.IShortageOrderRepository;
import Suppliers.DTO.OrderProductDetailsDTO;
import Suppliers.Domain.OrderByShortageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ShortageOrderServiceTest {

    private IShortageOrderRepository shortageOrderRepository;
    private OrderByShortageController mockController;
    private ShortageOrderService service;

    @BeforeEach
    public void setup() {
        shortageOrderRepository = mock(IShortageOrderRepository.class);
        mockController = mock(OrderByShortageController.class);

        // קונסטרקטור שעושה Inject ל־Controller
        service = new ShortageOrderService(mockController, shortageOrderRepository);
    }

    @Test
    public void givenValidShortage_whenOnWakeUp_thenShortageOrderInserted() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        shortage.put(1001, 4);

        OrderProductDetailsDTO dto = new OrderProductDetailsDTO(
                201, "ספק מהיר", new String[]{"TUESDAY"}, 301,
                1001, 12.0, 0.15, 4
        );

        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of(dto));

        service.onWakeUp(shortage, 2);

        verify(mockController).getShortageOrderProductDetails(eq(shortage), anyLong());
        verify(shortageOrderRepository).insert(any(ShortageOrderDTO.class));
    }

    @Test
    public void givenEmptyShortage_whenOnWakeUp_thenNoInsert() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of());

        service.onWakeUp(shortage, 1);

        verify(shortageOrderRepository, never()).insert(any());
    }

    @Test
    public void givenShortageWithZeroQuantity_whenOnWakeUp_thenNoInsertHappens() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        shortage.put(1002, 0);  // כמות אפס

        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of()); // מחזיר רשימה ריקה כי אין מה להזמין

        service.onWakeUp(shortage, 3);

        verify(shortageOrderRepository, never()).insert(any());
    }



    @Test
    public void givenValidShortage_whenOnWakeUp_thenInsertCorrectValues() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        shortage.put(1004, 7);

        OrderProductDetailsDTO dto = new OrderProductDetailsDTO(
                202, "ספק א'", new String[]{"WEDNESDAY"}, 302,
                1004, 25.0, 0.1, 7
        );

        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of(dto));

        service.onWakeUp(shortage, 5);

        // בודק אילו ערכים באמת נשלחו למחלקת הריפוזיטורי
        ArgumentCaptor<ShortageOrderDTO> captor = ArgumentCaptor.forClass(ShortageOrderDTO.class);
        verify(shortageOrderRepository).insert(captor.capture());

        ShortageOrderDTO captured = captor.getValue();
        assertEquals(1004, captured.getProductCatalogNumber());
        assertEquals(7, captured.getQuantity());
        assertEquals(202, captured.getSupplierId());
//        assertEquals(302, captured.getAgreementId());
        assertEquals(5, captured.getBranchId());
    }


}
