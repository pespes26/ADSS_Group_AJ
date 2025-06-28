# SuperLi Delivery Management System

## Authors
- Full Name - Shahar Shamir, ID: 318861820
- Full Name - Lital Kupchick, ID: 318567914
- Full Name - Hadar Ofer, ID: 208583781
- Full Name - Bar Pesso, ID: 315175554

## Description
SuperLi Delivery Management System is a comprehensive console-based logistics and delivery management solution. It consists of two core modules: HR Management and Deliveries Management. The system uses SQLite for data persistence and is built with Java, offering a reliable and efficient platform for delivery operations management.

## Module Documentation

### 1. HR Management Module

#### Purpose
Manages all employee-related operations, including driver registration, role assignments, shift scheduling, and availability tracking.

#### Domain Classes
- `Employee`: Base class for all employees
- `Role`: Role: Defines a job title(Transport Manager, HR Manager) within the system, used for access control and shift requirements. (System Administrator, Transport Manager, HR Manager)
- Shift: Represents a work shift, including its date, type (morning/evening), required roles, and the assigned employees per role.
- ShiftType: Enum defining types of shifts, such as MORNING and EVENING.
- AvailableShifts: Tracks each employee's declared availability per day and shift type.
- ArchivedEmployee: Represents an employee who has been deactivated and moved to the archive
- ArchivedShift: Represents a completed shift that is no longer active, stored for historical tracking.
- HRManager: A managerial employee responsible for managing roles, shifts, assignments, and employee records.

#### Key Features
- Employee CRUD operations
- Shift creation and scheduling
- Defining required roles per shift
- Assigning employees to shifts
- Shift validation and warnings
- Archiving past employees and shifts
- Role-based access control
- Availability tracking


#### Module Structure- **DAO Layer**:
  - `EmployeeDAO`: Employee data access
  - `RoleDAO`: Role data access
  - `AvailableShiftDAO`: employee availability  data access 
  - `ShiftDAO`: Shift data access
  - `ArchivedEmployeeDAO`:  archived employees  data access
  - `ArchivedShiftDAO`:  archived shifts  data access
  - `ShiftRoleDAO`:  required roles per shift  data access

- **DTOs**:
  - `EmployeeDTO`: Transfers employee information between layers
  - `RoleDTO`: Transfers role definitions used for access and assignments
  - `ShiftDTO`: Transfers data related to a single work shift
  - `AvailableShiftDTO`: Represents an employee's availability per shift
  - `ArchivedEmployeeDTO`: Transfers data of inactive (archived) employees
  - `ArchivedShiftDTO`: Transfers data of past, archived shifts
  - `ShiftRoleDTO`: Transfers role requirements for a specific shift

- **Services**:
  - `ShiftService`: Shift management
  - `EmployeeManagmentService`: Employee management
  - `RoleService`: Role management

- **Controllers**: `
  - EmployeeController`: Manages employee-level interactions such as updating availability, viewing assigned shifts, and handling personal preferences.
  - `ManagerController`: Oversees all high-level HR operations, including employee CRUD, role management, shift creation and assignment, and viewing archived records.

#### Technologies
- Java 17
- SQLite 3.x
- JDBC
- SLF4J (Logging)

#### Sample Flow: Add a Driver
1. The user selects "Add Employee" from the HR manager menu.
2. The system prompts for full employee details: ID, name, bank account, salary, site, employment terms, and role qualifications.
3. The user selects one or more role qualifications for the employee.
4. If marked as a driver, the system prompts for the driver's license type (e.g., C1, C, etc.).
5. The system validates that the license type is compatible and stores it with the driver’s profile.


#### Dependencies
- Depends on Deliveries Module for:
  - License compatibility checks

### 2. Deliveries Module

#### Purpose
Manages all delivery operations, including transport planning, truck assignments, and delivery tracking.

#### Domain Classes
- `Transport`: Main transport entity
- `Truck`: Vehicle information and capacity
- `DestinationDoc`: Delivery document for each destination
- `DeliveredItem`: Items in a delivery
- `TransportStatus`: Status enum (PLANNED, DISPATCHED, COMPLETED, CANCELLED)
- `Product`: Product information and weight
- `Site`: Location information
- `Zone`: Geographic zone definition
- `Driver`: Extends Employee, adds license type and availability

#### Key Features
- Transport planning and scheduling
- Truck capacity management
- Delivery document creation
- Item tracking and weight calculation
- Status management
- Driver-truck compatibility checking
- Product and site management
- Zone-based delivery planning
- Driver management with license types
- Driver-truck compatibility checking

#### Module Structure
- **DAO Layer**:
  - `TransportDAO`: Transport data access
  - `TruckDAO`: Truck data access
  - `DestinationDocDAO`: Document data access
  - `DeliveredItemDAO`: Item data access
  - `ProductDAO`: Product data access
  - `SiteDAO`: Site data access
  - `ZoneDAO`: Zone data access
  - `DriverDAO`: Driver data access

- **DTOs**:
  - `TransportDTO`: Transport data transfer
  - `TruckDTO`: Truck data transfer
  - `DestinationDocDTO`: Document data transfer
  - `DeliveredItemDTO`: Item data transfer
  - `ProductDTO`: Product data transfer
  - `SiteDTO`: Site data transfer
  - `ZoneDTO`: Zone data transfer
  - `DriverDTO`: Driver data transfer

- **Services**:
  - `TransportService`: Transport management
  - `TruckService`: Truck operations
  - `DestinationDocService`: Document handling
  - `DeliveredItemService`: Item management
  - `ProductService`: Product management
  - `SiteService`: Site operations
  - `ZoneService`: Zone management
  - `DriverService`: Driver operations

- **Controllers**:
  - `TransportController`: Transport UI
  - `TruckController`: Truck UI
  - `ProductController`: Product UI
  - `SiteController`: Site UI
  - `DriverController`: Driver UI

#### Technologies
- Java 17
- SQLite 3.x
- JDBC
- SLF4J (Logging)

#### Sample Flow: Create Transport
1. User selects "Create Transport"
2. System prompts for origin site
3. User selects destination
4. System checks driver availability
5. User assigns truck
6. System validates capacity
7. Transport is created
8. Status set to PLANNED

#### Dependencies
- Depends on HR Module for:
  - Driver availability
  - License verification
  - Role-based access

### 3. Module Integration

#### Data Relationships
```
[HR Module]                [Deliveries Module]
   |                             |
   |-- Employee -----------------→|
   |-- Driver -------------------→|
   |                             |
   |                             |-- Transport
   |                             |-- DestinationDoc
   |                             |-- DeliveredItem
   |                             |-- Product
   |                             |-- Site
   |                             |-- Zone
   |                             |
   |←-- Availability ------------|
   |←-- Role-based Access -------|
```

#### Cross-Service Calls
- **Deliveries → HR**:
  - `TransportService` calls `DriverService` for:
    - Availability checks
    - License verification
    - Assignment validation

- **HR → Deliveries**:
  - `DriverService` coordinates with `TransportService` for:
    - Availability updates
    - Assignment validation
    - Status changes

#### Initialization Flow
1. `DataInitializer` starts initialization
2. Creates tables in order:
   - HR Module:
     - Roles
     - Employees
     - Drivers
   - Deliveries Module:
     - Zones
     - Sites
     - Products
     - Trucks
     - Transports
     - Destination Docs
     - Delivered Items

3. Initializes sample data:
   - Basic roles and permissions
   - Test employees and drivers
   - Sample products and sites
   - Zone definitions
   - Initial transports

#### Data Consistency
- Foreign key constraints ensure referential integrity
- Service layer validates business rules
- Controllers enforce user permissions
- DAO layer handles data persistence
- Transaction management ensures atomicity

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.x
- SQLite 3.x

### Building the Project
1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd adss_p2final
   ```

2. Build with Maven:
   ```bash
   mvn clean compile
   ```

3. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.superli.deliveries.application.MainApp"
   ```

## Known Issues and Limitations
- Self-delivery feature is partially implemented
- Advanced reporting features are pending
- Real-time tracking is not implemented
- Mobile interface is not available

## License
This project is for academic use only.