import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class QuickDBTest {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        // Test Inventory.db
        try (Connection ignored = DriverManager.getConnection("jdbc:sqlite:Inventory.db")) {
            System.out.println("✅ Successfully connected to Inventory.db");
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to Inventory.db: " + e.getMessage());
        }

        // Test suppliers.db
        try (Connection ignored = DriverManager.getConnection("jdbc:sqlite:suppliers.db")) {
            System.out.println("✅ Successfully connected to suppliers.db");
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to suppliers.db: " + e.getMessage());
        }
    }
}
