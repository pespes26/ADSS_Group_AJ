package com.superli.deliveries.dataaccess.dao.del;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.dto.del.DeliveredItemDTO;
import com.superli.deliveries.util.Database;

public class DeliveredItemDAOImpl implements DeliveredItemDAO {
    private final Connection conn;

    public DeliveredItemDAOImpl() throws SQLException {
        this.conn = Database.getConnection();
    }

    @Override
    public Optional<DeliveredItemDTO> findById(String id) throws SQLException {
        String sql = "SELECT * FROM delivered_items WHERE item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToDeliveredItemDTO(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<DeliveredItemDTO> findAll() throws SQLException {
        List<DeliveredItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM delivered_items";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(mapResultSetToDeliveredItemDTO(rs));
            }
        }
        return items;
    }

    @Override
    public DeliveredItemDTO save(DeliveredItemDTO item) throws SQLException {
        String sql = "INSERT INTO delivered_items (item_id, destination_doc_id, product_id, quantity, status) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT(item_id) DO UPDATE SET " +
                    "destination_doc_id = ?, product_id = ?, quantity = ?, status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getId());
            stmt.setString(2, item.getDestinationDocId());
            stmt.setString(3, item.getProductId());
            stmt.setInt(4, item.getQuantity());
            stmt.setString(5, "PENDING"); // סטטוס קבוע בעת שמירה

            // For update:
            stmt.setString(6, item.getDestinationDocId());
            stmt.setString(7, item.getProductId());
            stmt.setInt(8, item.getQuantity());
            stmt.setString(9, "PENDING"); // גם בעדכון

            stmt.executeUpdate();
            return item;
        }
    }

    @Override
    public void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM delivered_items WHERE item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    private DeliveredItemDTO mapResultSetToDeliveredItemDTO(ResultSet rs) throws SQLException {
        DeliveredItemDTO dto = new DeliveredItemDTO();
        dto.setId(rs.getString("item_id"));
        dto.setDestinationDocId(rs.getString("destination_doc_id"));
        dto.setProductId(rs.getString("product_id"));
        dto.setQuantity(rs.getInt("quantity"));
        return dto;
    }
} 