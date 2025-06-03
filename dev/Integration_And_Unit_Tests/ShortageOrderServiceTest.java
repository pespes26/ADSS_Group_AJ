package Integration_And_Unit_Tests;

import Inventory.DTO.ShortageOrderDTO;
import Inventory.Repository.IShortageOrderRepository;
import InventorySupplier.SystemService.ShortageOrderService;
import Suppliers.DTO.OrderProductDetailsDTO;
import Suppliers.Domain.OrderByShortageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test class for ShortageOrderService.
 * Tests the functionality of handling shortage orders in the system, including
 * order creation, validation, and interaction with the repository.
 */
public class ShortageOrderServiceTest {

    private IShortageOrderRepository shortageOrderRepository;
    private OrderByShortageController mockController;
    private ShortageOrderService service;

    /**
     * Sets up the test environment before each test.
     * Creates mock objects for ShortageOrderRepository and OrderByShortageController.
     */
    @BeforeEach
    public void setup() {
        shortageOrderRepository = mock(IShortageOrderRepository.class);
        mockController = mock(OrderByShortageController.class);        // Constructor that injects the Controller
        service = new ShortageOrderService(mockController, shortageOrderRepository);
    }

    /**
     * Tests that a valid shortage triggers order creation and repository insertion.
     * Verifies the correct interaction with the controller and repository.
     */
    @Test
    public void givenValidShortage_whenOnWakeUp_thenShortageOrderInserted() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        shortage.put(1001, 4);

        OrderProductDetailsDTO dto = new OrderProductDetailsDTO(
                201, "Fast Supplier", new String[]{"TUESDAY"}, 301,
                1001, 12.0, 0.15, 4
        );

        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of(dto));

        service.onWakeUp(shortage, 2);

        verify(mockController).getShortageOrderProductDetails(eq(shortage), anyLong());
        verify(shortageOrderRepository).insert(any(ShortageOrderDTO.class));
    }

    /**
     * Tests that no order is inserted when the shortage list is empty.
     */
    @Test
    public void givenEmptyShortage_whenOnWakeUp_thenNoInsert() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of());

        service.onWakeUp(shortage, 1);

        verify(shortageOrderRepository, never()).insert(any());
    }

    /**
     * Tests that no order is created when the shortage quantity is zero.
     */
    @Test
    public void givenShortageWithZeroQuantity_whenOnWakeUp_thenNoInsertHappens() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();        shortage.put(1002, 0);  // zero quantity

        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of()); // Returns empty list since there's nothing to order

        service.onWakeUp(shortage, 3);

        verify(shortageOrderRepository, never()).insert(any());
    }



    /**
     * Tests that the correct values are passed to the repository when creating a shortage order.
     * Verifies product details, quantities, and supplier information.
     */
    @Test
    public void givenValidShortage_whenOnWakeUp_thenInsertCorrectValues() throws Exception {
        HashMap<Integer, Integer> shortage = new HashMap<>();
        shortage.put(1004, 7);

        OrderProductDetailsDTO dto = new OrderProductDetailsDTO(
                202, "Supplier A", new String[]{"WEDNESDAY"}, 302,
                1004, 25.0, 0.1, 7
        );

        when(mockController.getShortageOrderProductDetails(eq(shortage), anyLong()))
                .thenReturn(List.of(dto));

        service.onWakeUp(shortage, 5);        // Verify which values were actually sent to the repository
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
