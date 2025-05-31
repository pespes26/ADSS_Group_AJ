package Inventory.Domain;

import java.util.List;
import java.util.Optional;

import Inventory.DAO.JdbcProductDAO;
import Inventory.DTO.ItemDTO;
import Inventory.DAO.IItemsDAO;
import Inventory.DAO.JdbcItemDAO;
import java.sql.SQLException;

public class ItemRepositoryImpl implements ItemRepository {
    private final IItemsDAO itemDAO;

    public ItemRepositoryImpl() {

        this.itemDAO = new JdbcItemDAO();
    }

    @Override
    public void addItem(ItemDTO item) {
        try{
        itemDAO.Insert(item);
    }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }}

    @Override
    public void updateItem(ItemDTO item) {
        try{
        itemDAO.Update(item);
    }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }}

    @Override
    public void deleteItem(int itemId) {
        try {
            itemDAO.DeleteByItemId(itemId);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemDTO getItemById(int itemId) {
        try {
            return itemDAO.GetById(itemId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ItemDTO> getAllItems() {
        try{
        return itemDAO.getAllItems();
        }

    catch (SQLException e) {
        throw new RuntimeException(e);
    }}

    //@Override
    //public List<Item> getItemsByProductId(int productId) {
       // return itemDAO.(productId);
    //}

    @Override
    public void markItemAsDefect(int itemId) {
        try{
        itemDAO.signAsDefective(itemId);
    }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }}


    //@Override
    //public List<Item> getDefectiveItems() {
       // return itemDAO.findDefectiveItems();
   // }
}