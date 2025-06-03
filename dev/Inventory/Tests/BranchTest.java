package Inventory.Tests;

import Inventory.Domain.Branch;
import Inventory.DTO.ItemDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Unit tests for the Branch class using JUnit 4.
 * Verifies item management and catalog tracking per branch.
 */
public class BranchTest {

    private Branch branch;
    private ItemDTO item1;
    private ItemDTO item2;

    /**
     * Initializes a new Branch and two ItemDTO instances before each test.
     */
    @Before
    public void setUp() {
        branch = new Branch(101);

        item1 = new ItemDTO();
        item1.setItemId(1);
        item1.setCatalogNumber(1234);
        item1.setLocation("Warehouse");
        item1.setSectionInStore("A1");

        item2 = new ItemDTO();
        item2.setItemId(2);
        item2.setCatalogNumber(5678);
        item2.setLocation("InteriorStore");
        item2.setSectionInStore("B2");
    }

    /**
     * Verifies that the branch ID is correctly returned.
     */
    @Test
    public void testGetBranchId() {
        assertEquals(101, branch.getBranchId());
    }

    /**
     * Verifies that items can be added and retrieved by their ID.
     */
    @Test
    public void testAddAndGetItem() {
        branch.addItem(item1);
        ItemDTO retrieved = branch.getItem(1);
        assertNotNull("Item should be found", retrieved);
        assertEquals(1234, retrieved.getCatalogNumber());
    }

    /**
     * Verifies that removing an item actually removes it from the branch.
     */
    @Test
    public void testRemoveItem() {
        branch.addItem(item1);
        branch.removeItem(1);
        assertNull("Item should be removed", branch.getItem(1));
    }

    /**
     * Verifies that catalog numbers are tracked correctly when items are added.
     */
    @Test
    public void testCatalogNumbersTrackedCorrectly() {
        branch.addItem(item1);
        branch.addItem(item2);
        HashSet<Integer> catalogNumbers = branch.getCatalogNumbers();

        assertTrue("Catalog number 1234 should be present", catalogNumbers.contains(1234));
        assertTrue("Catalog number 5678 should be present", catalogNumbers.contains(5678));
        assertEquals("Two unique catalog numbers expected", 2, catalogNumbers.size());
    }

    /**
     * Verifies that adding an item with a duplicate item ID replaces the old item.
     */
    @Test
    public void testDuplicateItemIdReplacesExisting() {
        branch.addItem(item1);

        ItemDTO duplicateItem = new ItemDTO();
        duplicateItem.setItemId(1);
        duplicateItem.setCatalogNumber(9999);
        duplicateItem.setLocation("Backup");
        duplicateItem.setSectionInStore("Z9");

        branch.addItem(duplicateItem);
        ItemDTO retrieved = branch.getItem(1);

        assertEquals("Item ID should remain 1", 1, retrieved.getItemId());
        assertEquals("Catalog number should be updated to 9999", 9999, retrieved.getCatalogNumber());
    }
}
