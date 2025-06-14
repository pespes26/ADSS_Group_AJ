package com.superli.deliveries.dataaccess.dao.del;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.dto.del.SiteDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;

public class SiteDAOImpl implements SiteDAO {
    private final Connection conn;

    public SiteDAOImpl() throws SQLException {
        this.conn = Database.getConnection();
    }

    @Override
    public List<SiteDTO> findAll() throws SQLException {
        List<SiteDTO> sites = new ArrayList<>();
        String sql = "SELECT * FROM sites";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sites.add(mapResultSetToSiteDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all sites", e);
        }
        return sites;
    }

    @Override
    public Optional<SiteDTO> findById(String id) throws SQLException {
        String sql = "SELECT * FROM sites WHERE site_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToSiteDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding site by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public SiteDTO save(SiteDTO site) throws SQLException {
        String sql = "INSERT INTO sites (site_id, address, phone_number, contact_person_name, zone_id) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT(site_id) DO UPDATE SET " +
                    "address = ?, phone_number = ?, contact_person_name = ?, zone_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, site.getSiteId());
            stmt.setString(2, site.getAddress());
            stmt.setString(3, site.getPhoneNumber());
            stmt.setString(4, site.getContactPersonName());
            stmt.setString(5, site.getZoneId());
            
            // Values for UPDATE
            stmt.setString(6, site.getAddress());
            stmt.setString(7, site.getPhoneNumber());
            stmt.setString(8, site.getContactPersonName());
            stmt.setString(9, site.getZoneId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving site", e);
        }
        
        return site;
    }

    @Override
    public void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM sites WHERE site_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting site with id: " + id, e);
        }
    }

    @Override
    public List<SiteDTO> findByZoneId(String zoneId) throws SQLException {
        List<SiteDTO> sites = new ArrayList<>();
        String sql = "SELECT * FROM sites WHERE zone_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, zoneId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                sites.add(mapResultSetToSiteDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding sites in zone: " + zoneId, e);
        }
        return sites;
    }

    @Override
    public List<SiteDTO> findActiveSites() throws SQLException {
        List<SiteDTO> sites = new ArrayList<>();
        String sql = "SELECT * FROM sites";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sites.add(mapResultSetToSiteDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active sites", e);
        }
        return sites;
    }

    @Override
    public Optional<SiteDTO> findByAddress(String address) throws SQLException {
        String sql = "SELECT * FROM sites WHERE address = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToSiteDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding site by address: " + address, e);
        }
        return Optional.empty();
    }

    private SiteDTO mapResultSetToSiteDTO(ResultSet rs) throws SQLException {
        SiteDTO dto = new SiteDTO();
        dto.setSiteId(rs.getString("site_id"));
        dto.setAddress(rs.getString("address"));
        dto.setPhoneNumber(rs.getString("phone_number"));
        dto.setContactPersonName(rs.getString("contact_person_name"));
        dto.setZoneId(rs.getString("zone_id"));
        return dto;
    }
} 