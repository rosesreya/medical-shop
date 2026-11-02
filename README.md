# Medical Shop Inventory and Billing Management System

A full-stack pharmacy management application built using **Flutter, Spring Boot, and PostgreSQL**. The system is designed to simplify medical shop operations by managing products, batch-level inventory, purchases, customers, and sales billing.

## Tech Stack

| Layer | Technologies |
|---|---|
| Frontend | Flutter, Dart |
| Backend | Java 21, Spring Boot |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Security | Spring Security, JWT |
| Build Tool | Maven |

## Features

### 1. Product Management
- Add, update, view, and manage medicine details.
- Maintain product information such as name, price, and category.

### 2. Inventory Management
- Track stock at the product and batch levels.
- Maintain batch expiry information.
- Implement **FEFO (First-Expiry-First-Out)** stock allocation.
- Monitor available stock and identify low-stock products.

### 3. Purchase Management
- Record medicine purchases.
- Manage purchased quantities and batch information.
- Update inventory based on purchase entries.

### 4. Billing and Invoicing
- Create bills by selecting products and quantities.
- Calculate bill totals, GST, and discounts.
- Deduct stock when a sale is completed.
- Use transactional processing to maintain consistency between billing and inventory updates.

### 5. Customer Management
- Maintain customer information.
- Support walk-in and regular customers.

### 6. User Management
- Support Admin and Staff roles.
- Apply role-based access to relevant operations.
- Use JWT-based authentication.

## Architecture and Design

The backend follows a layered, modular architecture inspired by **Clean Architecture** and **Domain-Driven Design (DDD)** principles.

Key design considerations include:

- Separation of business logic and persistence concerns.
- Domain-focused business rules.
- DTO-based communication between application layers.
- Transaction management for operations involving multiple database changes.
- Batch-level inventory modelling to support expiry-aware stock allocation.

## Project Structure

```text
medical-shop/
│
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── mvnw
│
└── frontend/
    ├── lib/
    ├── pubspec.yaml
    └── test/
```

## Prerequisites

Make sure the following are installed:

- Java JDK 21
- PostgreSQL
- Flutter SDK
- Maven (or use the included Maven Wrapper)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/rosesreya/medical-shop.git
cd medical-shop
```

### 2. Configure the Database

Create a PostgreSQL database:

```sql
CREATE DATABASE medical_shop;
```

Configure the database connection in the Spring Boot application configuration.

Do not commit database passwords, JWT secrets, or other sensitive configuration values to the repository.

### 3. Run the Backend

Navigate to the backend directory:

```bash
cd backend
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

### 4. Run the Flutter Application

Open another terminal and navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
flutter pub get
```

Run the application:

```bash
flutter run
```

Ensure that the frontend API base URL points to the backend host accessible from your emulator, device, or local environment.

## Future Improvements

- Advanced sales and inventory reports.
- Dashboard analytics.
- Additional validation and automated tests.
- Deployment and production environment configuration.

## Author

**Sreya Rose**

[GitHub Profile](https://github.com/rosesreya)  
[Project Repository](https://github.com/rosesreya/medical-shop)
