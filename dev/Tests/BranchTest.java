package Tests;

import Domain.Branch;
import Domain.Item;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Unit tests for the Branch class using JUnit 4.
 * Verifies item management and catalog tracking per branch.
 */
public class BranchTest {

    private Branch branch;
    private Item item1;
    private Item item2;

    /**
     * Initializes a new Branch and two Item instances before each test.
     */
    @Before
    public void setUp() {
        branch = new Branch(101);

        item1 = new Item();
        item1.setItemId(1);
        item1.setCatalog_number(1234);
        item1.setStorageLocation("Warehouse");
        item1.setSectionInStore("A1");

        item2 = new Item();
        item2.setItemId(2);
        item2.setCatalog_number(5678);
        item2.setStorageLocation("InteriorStore");
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
        Item retrieved = branch.getItem(1);
        assertNotNull("Item should be found", retrieved);
        assertEquals(1234, retrieved.getCatalogNumber());
    }

    /**
     * Verifies that the branch returns all added items in its item map.
     */
    @Test
    public void testGetItems() {
        branch.addItem(item1);
        branch.addItem(item2);
        HashMap<Integer, Item> items = branch.getItems();
        assertEquals("Two items should be stored", 2, items.size());
        assertTrue(items.containsKey(1));
        assertTrue(items.containsKey(2));
    }

    /**
     * Verifies that catalog numbers are tracked in the branch when items are added.
     */
    @Test
    public void testCatalogNumbersAdded() {
        branch.addItem(item1);
        branch.addItem(item2);
        HashSet<Integer> catalogNumbers = branch.getCatalogNumbers();
        assertEquals("Two catalog numbers should be tracked", 2, catalogNumbers.size());
        assertTrue(catalogNumbers.contains(1234));
        assertTrue(catalogNumbers.contains(5678));
    }

    /**
     * Verifies that retrieving a non-existent item returns null.
     */
    @Test
    public void testGetNonExistentItem() {
        Item notFound = branch.getItem(999);
        assertNull("Non-existent item should return null", notFound);
    }
}
