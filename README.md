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
3. Run the main class:

ControllerInventorySupplier.InventorySupplierMainMenu

yaml
Copy
Edit

---

## 🧭 Main Menu Navigation

After launching, you'll see:

Welcome to the Inventory-Suppliers Menu! What would you like to manage?

Inventory System

Supplier System

Exit the Inventory-Suppliers system

yaml
Copy
Edit

Choose one of the options:

---

### 🏪 Option 1: Inventory System

1. You'll be asked: Do you want to load existing data from the database?

    -Load existing data

    -Start with an empty system

2. Then enter a **Branch ID** (from 1 to 10).

3. The inventory menu will open with 14 options, including:
- Show item details
- Add/remove items
- Mark item as defective
- Update cost prices
- Apply supplier/store discounts
- Generate inventory and shortage reports
- Update supply days & demand level
- Change storage location
- Exit inventory menu

Each action is interactive and includes instructions.

---

### 🧾 Option 2: Supplier System

1. You'll be prompted:

Do you want to initialize the system with sample data? (yes/no)

- Type `yes` to load demo suppliers and agreements.
- Type `no` to work with an empty supplier DB.

2. The Suppliers Module menu includes:

- Search supplier
- Create new supplier (and agreements/products)
- Search for past orders
- Return to the main menu

---

### ❌ Option 3: Exit The Inventory-Suppliers System

- All data from both modules will be **cleared** from the database.
- Console will display:





---

## 📚 Libraries & Tools Used

| Tool / Library        | Purpose                                  |
|-----------------------|------------------------------------------|
| Java (17+)            | Main programming language                |
| SQLite                | Embedded relational database             |
| sqlite-jdbc           | JDBC driver to connect Java with SQLite |
| Maven                 | Dependency management & build tool       |

---

## 🗃️ Database Schema Overview

When the system is first launched, all necessary tables are created automatically (if they don’t already exist). Sample data is loaded optionally depending on user selection.

---

### 🏪 Inventory Tables

| Table Name             | Description                                                            | Example Data Loaded                                           |
|------------------------|------------------------------------------------------------------------|---------------------------------------------------------------|
| `products`             | Holds all product definitions and metadata                             | `Orange Juice`, `White Rice`, `Dish Soap`, etc.              |
| `items`                | Physical instances of products (per branch, location, expiration)      | Warehouse/Store locations for each product                   |
| `sold_items`           | Records sale history of deleted/sold items                             | Item with catalog `1005`, sold on `2025-07-15`               |
| `discounts`            | Store discounts per product, per branch                                | 20% off `1004` in branch 1                                    |
| `product_discounts`    | Mapping of products to discounts                                        | Auto-generated from `discounts`                              |
| `branches`             | Information about the 10 available branches                            | Branches 1 to 10                                              |
| `orders_on_the_way`    | Supplier orders already placed and awaiting arrival                    | Empty on init                                                 |
| `periodic_orders`      | Supplier orders made on a weekly schedule                              | Empty on init                                                 |
| `defective_items`      | Tracks items marked defective                                           | Not preloaded                                                 |

---

### 📦 Supplier Tables

| Table Name             | Description                                                        | Example Data Loaded                                           |
|------------------------|--------------------------------------------------------------------|---------------------------------------------------------------|
| `suppliers`            | Supplier information                                               | `Prigat`, `Tnuva`, `Osem`, `Heinz`, `Sano`, etc.              |
| `agreements`           | Delivery terms per supplier, including delivery days              | e.g., `Prigat` → Monday, Wednesday, Friday                    |
| `products_supplied`    | Which supplier supplies which product, with size & cost info       | `Orange Juice 1L` supplied by `Prigat` at 6.5₪ per unit       |
| `orders`               | Purchase orders (empty by default)                                | Created during system usage                                  |
| `order_items`          | Line items in orders (empty by default)                           | Populated upon order creation                                |
| `delivery_terms`       | Tracks delivery schedules by supplier                             | Derived from `agreements`                                    |

---

### 📦 Sample Product Data (preloaded if selected)

| Catalog Number | Product Name           | Category    | Subcategory | Supplier | Delivery Days              |
|----------------|------------------------|-------------|-------------|----------|----------------------------|
| 1004           | Orange Juice 1L        | Beverages   | Juices      | Prigat   | MONDAY, WEDNESDAY, FRIDAY |
| 1006           | White Rice 1kg         | Grocery     | Rice        | Osem     | SUNDAY, WEDNESDAY          |
| 1012           | Dish Soap 750ml        | Cleaning    | Detergents  | Sano     | THURSDAY                   |

---

### 🧾 Sample Supplier Data (preloaded if selected)

| Supplier Name | Contact | Payment Method     | Delivery Days             |
|---------------|---------|--------------------|----------------------------|
| Prigat        | 0123    | Cash, Prepaid      | MONDAY, WEDNESDAY, FRIDAY |
| Tnuva         | 0124    | Bank Transfer      | TUESDAY, THURSDAY         |
| Osem          | 0125    | Bank Transfer      | SUNDAY, WEDNESDAY         |
| Heinz         | 0126    | Cash, Prepaid      | THURSDAY                   |
| Sano          | 0127    | Bank Transfer      | MONDAY                     |

> For full preload data, see `InventoryInitializer.preloadProducts()` and `SuppliersInitializer.insertSampleData()`.

---

### 💡 Notes

- Preloaded items are spread across **10 different branches** and locations (Warehouse/Store).
- Discount entries are applied to select products across multiple branches.
- All tables are **created and populated automatically** during system startup.