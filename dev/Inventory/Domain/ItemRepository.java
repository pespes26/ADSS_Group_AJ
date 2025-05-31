package Inventory.Domain;

import Inventory.DTO.ItemDTO;

import java.sql.SQLException;
import java.util.List;

public interface ItemRepository {
    void addItem(ItemDTO item);
    void updateItem(ItemDTO item);
    void deleteItem(int itemId);
    ItemDTO getItemById(int itemId);
    List<ItemDTO> getAllItems();
    //List<Item> getItemsByProductId(int productId);
    void markItemAsDefect(int itemId);
    //List<Item> getDefectiveItems();
}
