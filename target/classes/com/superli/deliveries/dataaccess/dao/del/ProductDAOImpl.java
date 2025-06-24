package com.superli.deliveries.dataaccess.dao.del;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.dto.del.ProductDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;

public class ProductDAOImpl implements ProductDAO {
    private final Connection conn;

    public ProductDAOImpl() throws SQLException {
        this.conn = Database.getConnection();
    }

    @Override
    public List<ProductDTO> findAll() throws SQLException {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProductDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all products", e);
        }
        return products;
    }

    @Override
    public Optional<ProductDTO> findById(String id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProductDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding product by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public ProductDTO save(ProductDTO product) throws SQLException {
        String sql = "INSERT INTO products (id, name, weight) " +
                    "VALUES (?, ?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET " +
                    "name = ?, weight = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getId());
            stmt.setString(2, product.getName());
            stmt.setFloat(3, product.getWeight());
            
            // Values for UPDATE
            stmt.setString(4, product.getName());
            stmt.setFloat(5, product.getWeight());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving product", e);
        }
        
        return product;
    }

    @Override
    public void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting product with id: " + id, e);
        }
    }

    @Override
    public List<ProductDTO> findByCategory(String category) throws SQLException {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProductDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding products in category: " + category, e);
        }
        return products;
    }

    @Override
    public List<ProductDTO> findByWeightRange(float minWeight, float maxWeight) throws SQLException {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE weight BETWEEN ? AND ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, minWeight);
            stmt.setFloat(2, maxWeight);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProductDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding products in weight range", e);
        }
        return products;
    }

    @Override
    public List<ProductDTO> findAvailableProducts() throws SQLException {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProductDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding available products", e);
        }
        return products;
    }

    private ProductDTO mapResultSetToProductDTO(ResultSet rs) throws SQLException {
        ProductDTO dto = new ProductDTO();
        dto.setId(rs.getString("id"));
        dto.setName(rs.getString("name"));
        dto.setWeight(rs.getFloat("weight"));
        return dto;
    }
} 