package com.superli.deliveries.dataaccess.dao.del;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.dto.del.DestinationDocDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;

public class DestinationDocDAOImpl implements DestinationDocDAO {
    private final Connection conn;

    public DestinationDocDAOImpl() throws SQLException {
        this.conn = Database.getConnection();
    }

    @Override
    public List<DestinationDocDTO> findAll() {
        List<DestinationDocDTO> docs = new ArrayList<>();
        String sql = "SELECT * FROM destination_docs";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                docs.add(mapResultSetToDocDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all destination documents", e);
        }
        return docs;
    }

    @Override
    public Optional<DestinationDocDTO> findById(String id) {
        String sql = "SELECT * FROM destination_docs WHERE destination_doc_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToDocDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding destination document by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public DestinationDocDTO save(DestinationDocDTO doc) {
        String sql = "INSERT INTO destination_docs (destination_doc_id, transport_id, destination_site_id, status) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT(destination_doc_id) DO UPDATE SET " +
                    "transport_id = ?, destination_site_id = ?, status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doc.getDestinationDocId());
            stmt.setString(2, doc.getTransportId());
            stmt.setString(3, doc.getSiteId());
            stmt.setString(4, doc.getStatus());
            stmt.setString(5, doc.getTransportId());
            stmt.setString(6, doc.getSiteId());
            stmt.setString(7, doc.getStatus());

            stmt.executeUpdate();
            return doc;
        } catch (SQLException e) {
            throw new DataAccessException("Error saving destination document", e);
        }
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM destination_docs WHERE destination_doc_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting destination document: " + id, e);
        }
    }

    @Override
    public List<DestinationDocDTO> findByTransportId(String transportId) {
        List<DestinationDocDTO> docs = new ArrayList<>();
        String sql = "SELECT * FROM destination_docs WHERE transport_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transportId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                docs.add(mapResultSetToDocDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding destination documents by transport: " + transportId, e);
        }
        return docs;
    }

    @Override
    public List<DestinationDocDTO> findByStatus(String status) {
        List<DestinationDocDTO> docs = new ArrayList<>();
        String sql = "SELECT * FROM destination_docs WHERE status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                docs.add(mapResultSetToDocDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding destination documents by status: " + status, e);
        }
        return docs;
    }

    @Override
    public List<DestinationDocDTO> findPendingDocs() {
        List<DestinationDocDTO> docs = new ArrayList<>();
        String sql = "SELECT * FROM destination_docs WHERE status = 'PLANNED'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                docs.add(mapResultSetToDocDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding pending destination documents", e);
        }
        return docs;
    }

    private DestinationDocDTO mapResultSetToDocDTO(ResultSet rs) throws SQLException {
        return new DestinationDocDTO(
            rs.getString("destination_doc_id"),
            rs.getString("transport_id"),
            rs.getString("destination_site_id"),
            rs.getString("status")
        );
    }
} 