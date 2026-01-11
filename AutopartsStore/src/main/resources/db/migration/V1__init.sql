CREATE DATABASE IF NOT EXISTS autoparts_store
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE autoparts_store;

-- ================
-- SECURITY / USERS
-- ================

CREATE TABLE roles (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(80) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(120) NOT NULL,
                       email VARCHAR(120) NULL UNIQUE,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
                            CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB;

-- ==========
-- CUSTOMERS
-- ==========

CREATE TABLE customers (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(140) NOT NULL,
                           phone VARCHAR(40),
                           email VARCHAR(120),
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE customer_addresses (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    customer_id BIGINT NOT NULL,
                                    country VARCHAR(80) NOT NULL DEFAULT 'Romania',
                                    city VARCHAR(80) NOT NULL,
                                    street VARCHAR(160) NOT NULL,
                                    postal_code VARCHAR(20),
                                    is_default BOOLEAN NOT NULL DEFAULT FALSE,
                                    CONSTRAINT fk_addr_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB;

CREATE INDEX idx_customer_name ON customers(name);

-- ======================
-- PRODUCT CATALOG
-- ======================

CREATE TABLE manufacturers (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               name VARCHAR(120) NOT NULL UNIQUE,
                               country VARCHAR(80),
                               website VARCHAR(200)
) ENGINE=InnoDB;

CREATE TABLE categories (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(120) NOT NULL,
                            parent_id BIGINT NULL,
                            CONSTRAINT uq_category_name_parent UNIQUE (name, parent_id),
                            CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
) ENGINE=InnoDB;

CREATE TABLE products (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          sku VARCHAR(60) NOT NULL UNIQUE,
                          name VARCHAR(200) NOT NULL,
                          description TEXT,
                          category_id BIGINT NOT NULL,
                          manufacturer_id BIGINT NOT NULL,
                          price DECIMAL(10,2) NOT NULL,
                          vat_rate DECIMAL(5,2) NOT NULL DEFAULT 19.00,
                          stock_qty INT NOT NULL DEFAULT 0,
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
                          CONSTRAINT fk_product_manufacturer FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(id)
) ENGINE=InnoDB;

CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_manufacturer ON products(manufacturer_id);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_stock ON products(stock_qty);

-- ======================
-- ORDERS / SALES
-- ======================

CREATE TABLE orders (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        order_no VARCHAR(40) NOT NULL UNIQUE,
                        customer_id BIGINT NOT NULL,
                        created_by_user_id BIGINT NOT NULL,

                        status VARCHAR(30) NOT NULL, -- DRAFT / CONFIRMED / PAID / CANCELED
                        order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        paid_date DATETIME NULL,
                        notes VARCHAR(500),

                        subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                        vat_total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                        total DECIMAL(12,2) NOT NULL DEFAULT 0.00,

                        CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
                        CONSTRAINT fk_orders_user FOREIGN KEY (created_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE INDEX idx_orders_order_date ON orders(order_date);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_customer ON orders(customer_id);

CREATE TABLE order_items (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,

                             quantity INT NOT NULL,
                             unit_price DECIMAL(10,2) NOT NULL,
                             vat_rate DECIMAL(5,2) NOT NULL DEFAULT 19.00,
                             discount DECIMAL(10,2) NOT NULL DEFAULT 0.00,

                             line_total DECIMAL(12,2) NOT NULL,

                             CONSTRAINT fk_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

CREATE TABLE payments (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          order_id BIGINT NOT NULL,
                          method VARCHAR(30) NOT NULL, -- CASH / CARD / TRANSFER
                          amount DECIMAL(12,2) NOT NULL,
                          paid_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          reference VARCHAR(120),

                          CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================
-- INVOICES + EXPORT HISTORY
-- ======================

CREATE TABLE invoices (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          invoice_no VARCHAR(40) NOT NULL UNIQUE,
                          order_id BIGINT NOT NULL UNIQUE,
                          issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          series VARCHAR(20),

                          subtotal DECIMAL(12,2) NOT NULL,
                          vat_total DECIMAL(12,2) NOT NULL,
                          total DECIMAL(12,2) NOT NULL,

                          CONSTRAINT fk_invoices_order FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB;

CREATE TABLE document_exports (
                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  doc_type VARCHAR(30) NOT NULL,     -- INVOICE / REPORT
                                  doc_id BIGINT NOT NULL,
                                  format VARCHAR(10) NOT NULL,       -- PDF / XLSX
                                  filename VARCHAR(255) NOT NULL,
                                  generated_by_user_id BIGINT NOT NULL,
                                  generated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_exports_user FOREIGN KEY (generated_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE INDEX idx_exports_doc ON document_exports(doc_type, doc_id);
CREATE INDEX idx_exports_generated_at ON document_exports(generated_at);

-- ======================
-- IMPORT XLSX TRACKING
-- ======================

CREATE TABLE import_batches (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                type VARCHAR(30) NOT NULL,         -- PRODUCTS etc.
                                filename VARCHAR(255) NOT NULL,
                                imported_by_user_id BIGINT NOT NULL,
                                imported_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                status VARCHAR(20) NOT NULL,       -- SUCCESS / FAILED / PARTIAL
                                summary_json JSON NULL,

                                CONSTRAINT fk_import_user FOREIGN KEY (imported_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- ======================
-- AUDIT / ACTIVITY LOG
-- ======================

CREATE TABLE activity_log (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NULL,
                              role_snapshot VARCHAR(100) NULL,
                              action VARCHAR(120) NOT NULL,      -- ex: "UPDATE_PRODUCT", "EXPORT_INVOICE_PDF"
                              entity VARCHAR(80) NULL,           -- ex: "Product", "Order"
                              entity_id BIGINT NULL,
                              sql_text TEXT NULL,                -- optional (dacă vrei să loghezi query/statement)
                              success BOOLEAN NOT NULL DEFAULT TRUE,
                              error_message VARCHAR(500) NULL,
                              ip_address VARCHAR(60) NULL,
                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE INDEX idx_log_created_at ON activity_log(created_at);
CREATE INDEX idx_log_user ON activity_log(user_id);
CREATE INDEX idx_log_action ON activity_log(action);
