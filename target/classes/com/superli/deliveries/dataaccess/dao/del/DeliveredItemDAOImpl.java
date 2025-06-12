package com.superli.deliveries.dataaccess.dao.del;

import com.superli.deliveries.dto.del.DeliveredItemDTO;
import com.superli.deliveries.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeliveredItemDAOImpl implements DeliveredItemDAO {

    @Override
    public Optional<DeliveredItemDTO> findById(String id) throws SQLException {
        String sql = "SELECT * FROM delivered_items WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDeliveredItemDTO(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<DeliveredItemDTO> findAll() throws SQLException {
        List<DeliveredItemDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM delivered_items";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(mapResultSetToDeliveredItemDTO(rs));
            }
        }
        return items;
    }

    @Override
    public DeliveredItemDTO save(DeliveredItemDTO item) throws SQLException {
        if (item.getId() == null) {
            return insert(item);
        } else {
            return update(item);
        }
    }

    @Override
    public void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM delivered_items WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    private DeliveredItemDTO insert(DeliveredItemDTO item) throws SQLException {
        String sql = "INSERT INTO delivered_items (destination_doc_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getDestinationDocId());
            stmt.setString(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getString(1));
                }
            }
            return item;
        }
    }

    private DeliveredItemDTO update(DeliveredItemDTO item) throws SQLException {
        String sql = "UPDATE delivered_items SET destination_doc_id = ?, product_id = ?, quantity = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getDestinationDocId());
            stmt.setString(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setString(4, item.getId());
            stmt.executeUpdate();
            return item;
        }
    }

    private DeliveredItemDTO mapResultSetToDeliveredItemDTO(ResultSet rs) throws SQLException {
        DeliveredItemDTO dto = new DeliveredItemDTO();
        dto.setId(rs.getString("id"));
        dto.setDestinationDocId(rs.getString("destination_doc_id"));
        dto.setProductId(rs.getString("product_id"));
        dto.setQuantity(rs.getInt("quantity"));
        return dto;
    }
} 