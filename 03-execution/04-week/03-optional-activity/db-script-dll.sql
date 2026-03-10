-- ============================================================
-- FULL SCHEMA (PostgreSQL) - Ejecutar en pgAdmin
-- Fecha: 2026-03-04
-- ============================================================

-- Para UUIDs aleatorios
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- 0) Notas / Convenciones
-- ============================================================
-- Auditoría en todas las tablas:
-- created_at TIMESTAMPTZ DEFAULT NOW()
-- updated_at TIMESTAMPTZ
-- deleted_at TIMESTAMPTZ
-- created_by UUID
-- updated_by UUID
-- deleted_by UUID
-- status BOOLEAN DEFAULT true

-- ============================================================
-- MODULE 2: Parameter
-- - type_document
-- - person
-- - file
-- ============================================================

CREATE TABLE type_document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(30) UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true
);

CREATE TABLE file (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    file_path TEXT NOT NULL,
    mime_type VARCHAR(120),
    size_bytes BIGINT,
    checksum VARCHAR(128),

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true
);

CREATE TABLE person (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type_document_id UUID NOT NULL,
    document_number VARCHAR(50) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    birth_date DATE,
    photo_file_id UUID,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT uq_person_document UNIQUE (type_document_id, document_number),
    CONSTRAINT fk_person_type_document FOREIGN KEY (type_document_id) REFERENCES type_document(id),
    CONSTRAINT fk_person_photo_file FOREIGN KEY (photo_file_id) REFERENCES file(id)
);

-- ============================================================
-- MODULE 1: Security
-- - user
-- - role
-- - module
-- - view
-- - user_role
-- - role_module
-- - module_view
-- ============================================================

CREATE TABLE "user" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    person_id UUID UNIQUE,
    is_superuser BOOLEAN DEFAULT false,
    last_login_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT fk_user_person FOREIGN KEY (person_id) REFERENCES person(id)
);

CREATE TABLE role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true
);

CREATE TABLE module (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(60) UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    route VARCHAR(255),
    icon VARCHAR(100),
    sort_order INT DEFAULT 0,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true
);

CREATE TABLE view (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(80) UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    route VARCHAR(255),
    sort_order INT DEFAULT 0,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true
);

CREATE TABLE user_role (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

CREATE TABLE role_module (
    role_id UUID NOT NULL,
    module_id UUID NOT NULL,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    PRIMARY KEY (role_id, module_id),
    CONSTRAINT fk_role_module_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_module_module FOREIGN KEY (module_id) REFERENCES module(id) ON DELETE CASCADE
);

CREATE TABLE module_view (
    module_id UUID NOT NULL,
    view_id UUID NOT NULL,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    PRIMARY KEY (module_id, view_id),
    CONSTRAINT fk_module_view_module FOREIGN KEY (module_id) REFERENCES module(id) ON DELETE CASCADE,
    CONSTRAINT fk_module_view_view FOREIGN KEY (view_id) REFERENCES view(id) ON DELETE CASCADE
);

-- ============================================================
-- MODULE 3: Inventory
-- - category
-- - product
-- - supplier
-- - inventory
-- ============================================================

CREATE TABLE category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true
);

CREATE TABLE supplier (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    tax_id VARCHAR(60),
    contact_name VARCHAR(120),
    phone VARCHAR(50),
    email VARCHAR(255),
    address TEXT,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT uq_supplier_email UNIQUE (email)
);

CREATE TABLE product (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    description TEXT,
    sku VARCHAR(100) UNIQUE,
    barcode VARCHAR(120) UNIQUE,
    price NUMERIC(12, 2) NOT NULL DEFAULT 0,
    cost NUMERIC(12, 2) NOT NULL DEFAULT 0,
    category_id UUID,
    supplier_id UUID,
    image_file_id UUID,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    CONSTRAINT fk_product_image FOREIGN KEY (image_file_id) REFERENCES file(id)
);

CREATE TABLE inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL UNIQUE,
    quantity INTEGER NOT NULL DEFAULT 0,
    min_quantity INTEGER NOT NULL DEFAULT 0,
    max_quantity INTEGER,
    last_restock_date TIMESTAMPTZ,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE RESTRICT
);

-- ============================================================
-- MODULE 4: Sales
-- - customer
-- - order
-- - order_item
-- ============================================================

CREATE TABLE customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    person_id UUID NOT NULL UNIQUE,
    customer_code VARCHAR(60) UNIQUE,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT fk_customer_person FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE RESTRICT
);

CREATE TABLE "order" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(60) UNIQUE,
    customer_id UUID NOT NULL,
    order_date TIMESTAMPTZ DEFAULT NOW(),
    status_order VARCHAR(40) NOT NULL DEFAULT 'PENDING',
    subtotal NUMERIC(12, 2) NOT NULL DEFAULT 0,
    discount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    tax NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    notes TEXT,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE RESTRICT
);

CREATE TABLE order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    line_total NUMERIC(12, 2) NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES "order"(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE RESTRICT
);

-- ============================================================
-- MODULE 4 (separado): Method_payment
-- - method_payment
-- ============================================================

CREATE TABLE method_payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(40) UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true
);

-- ============================================================
-- MODULE 5: Billing
-- - invoice
-- - invoice_item
-- - payment
-- ============================================================

CREATE TABLE invoice (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number VARCHAR(60) UNIQUE,
    order_id UUID NOT NULL UNIQUE,
    invoice_date TIMESTAMPTZ DEFAULT NOW(),
    due_date TIMESTAMPTZ,
    status_invoice VARCHAR(40) NOT NULL DEFAULT 'PENDING',
    subtotal NUMERIC(12, 2) NOT NULL DEFAULT 0,
    tax NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    notes TEXT,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT fk_invoice_order FOREIGN KEY (order_id) REFERENCES "order"(id) ON DELETE RESTRICT
);

CREATE TABLE invoice_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL,
    product_id UUID NOT NULL,
    description TEXT,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    line_total NUMERIC(12, 2) NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT fk_invoice_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(id) ON DELETE CASCADE,
    CONSTRAINT fk_invoice_item_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE RESTRICT
);

CREATE TABLE payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL,
    method_payment_id UUID NOT NULL,
    payment_date TIMESTAMPTZ DEFAULT NOW(),
    amount_paid NUMERIC(12, 2) NOT NULL CHECK (amount_paid > 0),
    transaction_reference VARCHAR(255),

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    deleted_by UUID,
    status BOOLEAN DEFAULT true,

    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(id) ON DELETE RESTRICT,
    CONSTRAINT fk_payment_method FOREIGN KEY (method_payment_id) REFERENCES method_payment(id) ON DELETE RESTRICT
);

-- ============================================================
-- Comentarios (opcionales)
-- ============================================================
COMMENT ON TABLE "user" IS 'Usuarios del sistema.';
COMMENT ON TABLE "order" IS 'Órdenes/ventas realizadas por clientes.';
COMMENT ON TABLE invoice IS 'Factura asociada a una orden.';