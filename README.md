# Autoparts Store

A web application for an auto parts shop, built with **Spring Boot**. Customers can browse a catalog of parts, fill a cart and place orders. Employees and admins manage the catalog, process orders, view sales reports and export them to Excel or PDF. Admins also get an audit trail of who changed what.

## Features

**Customers**
- Register and log in
- Browse products with filtering (category, manufacturer, search)
- Shopping cart and checkout, which creates an order with a generated number (`ORD-yyyyMMdd-HHmmss`)

**Employees and admins**
- Full CRUD for products, categories and manufacturers
- Order management: filterable, paginated order list, order details, status updates (`DRAFT`, `CONFIRMED`, `PAID`, `CANCELED`)
- Sales reports: sales summary and sales by category, exportable as **XLSX** and **PDF**

**Admins only**
- Activity log (audit trail) of create, update and delete actions, recorded automatically through an AOP aspect

**Security**
- Form login with BCrypt-hashed passwords
- Role-based access control for pages and API endpoints (`ROLE_CLIENT`, `ROLE_EMPLOYEE`, `ROLE_ADMIN`)

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.9 (Web, Security, Data JPA, JDBC, Validation, AOP) |
| Database | MySQL, with schema migrations via Flyway |
| Reports | Apache POI (XLSX), Apache PDFBox (PDF) |
| Frontend | Static HTML, CSS and vanilla JavaScript served by Spring Boot |
| Build | Maven (wrapper included) |

## Project structure

```
.
├── AutopartsStore/                  # Spring Boot application
│   ├── pom.xml
│   └── src/main/
│       ├── java/ro/ugal/autopartsstore/
│       │   ├── audit/               # @Audit annotation, aspect and service
│       │   ├── bootstrap/           # DataSeeder (creates default admin)
│       │   ├── controller/          # REST controllers
│       │   ├── domain/              # JPA entities and enums
│       │   ├── dto/                 # Request/response objects
│       │   ├── repository/          # Spring Data repositories
│       │   ├── security/            # Spring Security configuration
│       │   ├── service/             # Business logic
│       │   └── spec/                # JPA Specifications for filtering
│       └── resources/
│           ├── db/migration/        # Flyway migrations
│           └── static/              # HTML pages, CSS and JS
└── DB_ISI/                          # MySQL table dumps with sample data
```

## Getting started

### Prerequisites

- JDK 21
- MySQL 8 running locally on port 3306

### 1. Clone the repository

```bash
git clone https://github.com/AdelinChelariu/Magazin-Piese-Auto-SpringBoot.git
cd Magazin-Piese-Auto-SpringBoot
```

### 2. Set up the database

Create the database and load the sample data from `DB_ISI/`:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS autoparts_store CHARACTER SET utf8mb4;"
for f in DB_ISI/*.sql; do mysql -u root -p autoparts_store < "$f"; done
```

The dumps include the three roles (`ROLE_ADMIN`, `ROLE_EMPLOYEE`, `ROLE_CLIENT`) that registration depends on, plus sample categories, manufacturers, products and orders.

### 3. Configure the connection

The app reads its database settings from `AutopartsStore/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/autoparts_store?useSSL=false&serverTimezone=UTC
spring.datasource.username=<your MySQL user>
spring.datasource.password=<your MySQL password>
```

You can also override these with environment variables instead of editing the file:

```bash
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password
```

### 4. Run

```bash
cd AutopartsStore
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

Open <http://localhost:8080/login.html>.

### Default admin account

On first start, `DataSeeder` creates an admin user if none exists:

| Username | Password |
|---|---|
| `admin` | `admin123` |

**Change this password before exposing the app to anyone else.** New customers can register at `/register.html`.

## Roles and access

| Area | Client | Employee | Admin |
|---|:---:|:---:|:---:|
| Shop, cart, checkout | ✅ | ✅ (pages) | ✅ (pages) |
| Product, category and manufacturer management pages | – | ✅ | ✅ |
| Order management | – | ✅ | ✅ |
| Reports and exports | – | ✅ | ✅ |
| Activity log | – | – | ✅ |

## API overview

| Endpoint | Methods | Access |
|---|---|---|
| `/api/auth/register` | POST | Public |
| `/api/me` | GET | Authenticated |
| `/api/products`, `/api/categories`, `/api/manufacturers` | GET | Any logged-in user |
| `/api/categories/manage/**`, `/api/manufacturers/manage/**` | GET, POST, PUT, DELETE | Employee, Admin |
| `/api/client/checkout` | POST | Client |
| `/api/orders/manage` | GET, `/{id}` GET, `/{id}/status` PUT | Employee, Admin |
| `/api/reports/sales-summary` | GET | Employee, Admin |
| `/api/reports/sales-by-category` (`.xlsx`, `.pdf`) | GET | Employee, Admin |
| `/api/activity-log` | GET | Admin |

## Database

The schema covers users and roles, customers and addresses, manufacturers, categories, products, orders and order items, payments, invoices, document exports, import batches and the activity log. Payments support `CASH`, `CARD` and `TRANSFER`; exports support `PDF` and `XLSX`.

## Notes

- Do not commit real database credentials. Keep them in environment variables or a local, untracked config file.
- CSRF protection is currently disabled in the security configuration, which is fine for local development but should be revisited before any real deployment.
