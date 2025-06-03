# 📦 Inventory & Supplier Management System

A unified Java-based system for managing both inventory operations across multiple branches and supplier relationships.

---

## 👤 Developers

- **Yali Katz** – 211381009
- **Gal Fuerst** – 318256492
- **Maor Livni** – 316117365
- **Ran Erez** – 207285362

---

## 🚀 How to Run the System

### ✅ Prerequisites

Make sure the following are installed on your system:

- Java 17 or higher (Java 21+ recommended)
- Maven (for dependency & build management)
- SQLite JDBC Driver (auto-downloaded via Maven)

---

### ▶️ Launching the System

1. **Clone or download** this project.
2. Open the project in your Java IDE (e.g., IntelliJ).
3. Run the main class: `InventorySupplier.Presentation.InventorySupplierMainMenu`

---

## 🧭 Main Menu Navigation

When the system starts, you'll first be prompted:

**"Do you want to load data to database?"**

- `1` → Load sample data into the database and memory
- `2` → Start with an empty system (no sample data)

Then you'll see the main options:

    Welcome to the Inventory-Suppliers Menu! What would you like to manage?

    1. Inventory System  
    2. Supplier System  
    3. Exit the Inventory-Suppliers system

---

## 🏪 Inventory Module

1. Enter your **Branch ID** (1–10).
2. Access the Inventory Menu, which includes:

- Show item details
- Add/remove items
- Mark item as defective
- Update cost prices
- Apply supplier/store discounts
- Inventory & shortage reports
- Update demand/supply parameters
- Change storage location
- Exit inventory menu

Data is retrieved using DAOs and Repository patterns. Branch-specific operations are respected.

---

## 📦 Supplier Module

When selected:

- A sample dataset of **8 suppliers**, **agreements**, **products supplied**, and **discounts** is optionally loaded.
- You can:

   - Search supplier
   - Create supplier + agreement + product
   - View past supplier orders
   - Return to the main menu

All related tables are initialized via DAOs.

---

## ❌ Exit Option

- Selecting `3` will **clear all data**:
   - Inventory: products, items, orders, discounts
   - Supplier: suppliers, agreements, discounts, etc.
- Console confirms deletion.

---

## 📚 Libraries & Tools Used

| Tool / Library        | Purpose                                  |
|-----------------------|------------------------------------------|
| Java (17+)            | Core language                            |
| SQLite                | Embedded database                        |
| sqlite-jdbc           | JDBC bridge                              |
| Maven                 | Dependency/build tool                    |

---

## 🗃️ Database Schema Overview

All tables are created **dynamically** at runtime using the DAO layer.  
Data is inserted through DTO-based preloaders if chosen during startup.

---

### 🏪 Inventory Tables

| Table Name             | Description                                                          |
|------------------------|----------------------------------------------------------------------|
| `products`             | All products across the system                                       |
| `items`                | Instances of products in branches (Warehouse/Store)                  |
| `sold_items`           | Tracks when items were removed/sold                                  |
| `discounts`            | Store discounts per product per branch                               |
| `product_discounts`    | Mapping table for product → discount                                 |
| `branches`             | Represents 10 branch IDs                                             |
| `orders_on_the_way`    | Placed supplier orders awaiting delivery                             |
| `periodic_orders`      | Scheduled supplier orders (weekly)                                   |
| `shortage_orders`      | Orders triggered by shortages                                        |

---

### 📦 Supplier Tables

| Table Name             | Description                                                            |
|------------------------|------------------------------------------------------------------------|
| `suppliers`            | Name, contact, payment method, etc.                                   |
| `agreements`           | Days of delivery & standing order status                              |
| `products_supplied`    | Supplier's products, sizes, prices, agreements                        |
| `discounts`            | Tiered discounts from suppliers based on quantity                     |
| `orders`               | Actual purchase orders made to suppliers                              |
| `order_items`          | Items within each supplier order                                      |
| `delivery_terms`       | Derived from `agreements` – not physically stored                     |

---

## 🔁 Preloaded Sample Data (Optional)

### Products (via `preloadProducts()`)

10+ sample products inserted if not already present, each with:

- Unique catalog number
- Name, category, subcategory
- Supplier name
- Prices (base, retail, discount)
- Delivery days
- Associated agreement ID

### Items (via `preloadItems()`)

Each product has instances in various branches:

- Branch ID (1–10)
- Storage location: `Warehouse` or `Store`
- Expiration dates vary per item
- Unique shelf & location identifiers

### Periodic Orders (via `preloadPeriodicOrders()`)

- 3 sample orders created using top 3 supplier-agreement pairs
- Repeats on specific weekdays
- Tracked by DAO in `periodic_orders` table

### Suppliers & Agreements

Loaded using:

- `insertSampleData()` from `SuppliersInitializer`
- 8 sample suppliers
- 8 linked agreements (with different delivery schedules)
- Mapping `supplier_id → agreement_id` stored via `LinkedHashMap`

### Discounts & Product Mapping

Each product is:

- Supplied by one supplier
- Has a size, price, and discount tier
- Linked via `products_supplied` and `discounts` tables

---

## 💡 Notes

- DAO classes are responsible for table creation (`createTableIfNotExists`) and insertion.
- Data preloaders avoid duplication: check if product/item already exists before inserting.
- Periodic orders and shortage orders work alongside inventory & supplier systems.
- When exiting, all tables are cleared in proper dependency order.

