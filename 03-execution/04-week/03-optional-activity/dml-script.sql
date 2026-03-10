/****************************************************************
* DML (Data Manipulation Language) - Inserción de Datos de Prueba
* Se insertan 10 registros por cada entidad.
****************************************************************/



DO $$
DECLARE
    -- Declaramos variables para almacenar los UUIDs generados
    -- Módulo Parámetros
    type_doc_ids UUID[];
    file_ids UUID[];
    person_ids UUID[];
    -- Módulo Seguridad
    user_ids UUID[];
    role_ids UUID[];
    module_ids UUID[];
    view_ids UUID[];
    -- Módulo Inventario
    category_ids UUID[];
    supplier_ids UUID[];
    product_ids UUID[];
    -- Módulo Ventas/Facturación
    customer_ids UUID[];
    order_ids UUID[];
    method_payment_ids UUID[];
    invoice_ids UUID[];

BEGIN

-- ============================================================
-- MODULE 2: Parameter
-- ============================================================

-- 1. Insertar en tablas sin dependencias
WITH inserted_type_docs AS (
    INSERT INTO type_document (code, name) VALUES
    ('DNI', 'Documento Nacional de Identidad'),
    ('CE', 'Carné de Extranjería'),
    ('RUC', 'Registro Único de Contribuyentes'),
    ('PAS', 'Pasaporte'),
    ('PTP', 'Permiso Temporal de Permanencia'),
    ('CUI', 'Código Único de Identificación'),
    ('LE', 'Libreta Electoral'),
    ('LM', 'Libreta Militar'),
    ('CEX', 'Cédula de Extranjería'),
    ('NIT', 'Número de Identificación Tributaria')
    RETURNING id
)
SELECT array_agg(id) INTO type_doc_ids FROM inserted_type_docs;

WITH inserted_files AS (
    INSERT INTO file (file_name, original_name, file_path, mime_type, size_bytes) VALUES
    ('user_1.jpg', 'profile.jpg', '/uploads/images/user_1.jpg', 'image/jpeg', 102400),
    ('user_2.png', 'avatar.png', '/uploads/images/user_2.png', 'image/png', 204800),
    ('product_a.jpg', 'prod_a.jpg', '/uploads/products/product_a.jpg', 'image/jpeg', 512000),
    ('product_b.webp', 'prod_b.webp', '/uploads/products/product_b.webp', 'image/webp', 307200),
    ('doc_report.pdf', 'report.pdf', '/uploads/docs/doc_report.pdf', 'application/pdf', 1048576),
    ('user_3.jpg', 'me.jpg', '/uploads/images/user_3.jpg', 'image/jpeg', 153600),
    ('user_4.gif', 'funny.gif', '/uploads/images/user_4.gif', 'image/gif', 2097152),
    ('product_c.jpg', 'item_c.jpg', '/uploads/products/product_c.jpg', 'image/jpeg', 409600),
    ('product_d.png', 'item_d.png', '/uploads/products/product_d.png', 'image/png', 614400),
    ('company_logo.svg', 'logo.svg', '/uploads/assets/company_logo.svg', 'image/svg+xml', 51200)
    RETURNING id
)
SELECT array_agg(id) INTO file_ids FROM inserted_files;


-- 2. Insertar 'person' que depende de 'type_document' y 'file'
WITH inserted_persons AS (
    INSERT INTO person (type_document_id, document_number, first_name, last_name, email, phone, photo_file_id) VALUES
    (type_doc_ids[1], '71234567', 'Ana', 'García', 'ana.garcia@example.com', '987654321', file_ids[1]),
    (type_doc_ids[2], '12345678', 'Luis', 'Martinez', 'luis.martinez@example.com', '912345678', file_ids[2]),
    (type_doc_ids[3], '20123456789', 'Sofía', 'Rodríguez', 'sofia.r@example.com', '923456789', NULL),
    (type_doc_ids[4], 'A1B2C3D4', 'Carlos', 'Hernández', 'carlos.h@example.com', '934567890', file_ids[6]),
    (type_doc_ids[1], '87654321', 'Laura', 'López', 'laura.lopez@example.com', '945678901', NULL),
    (type_doc_ids[2], '87654321X', 'David', 'Pérez', 'david.perez@example.com', '956789012', file_ids[7]),
    (type_doc_ids[3], '10987654321', 'María', 'Gómez', 'maria.gomez@example.com', '967890123', NULL),
    (type_doc_ids[4], 'Z9Y8X7W6', 'Javier', 'Díaz', 'javier.diaz@example.com', '978901234', NULL),
    (type_doc_ids[1], '11223344', 'Elena', 'Sánchez', 'elena.sanchez@example.com', '989012345', NULL),
    (type_doc_ids[5], 'PTP12345', 'Miguel', 'Torres', 'miguel.torres@example.com', '990123456', NULL)
    RETURNING id
)
SELECT array_agg(id) INTO person_ids FROM inserted_persons;


-- ============================================================
-- MODULE 1: Security
-- ============================================================
WITH inserted_roles AS (
    INSERT INTO role (name, description) VALUES
    ('Superadministrador', 'Acceso total a todas las funciones del sistema.'),
    ('Administrador', 'Gestiona usuarios, inventario y configuraciones.'),
    ('Vendedor', 'Registra ventas y gestiona clientes.'),
    ('Almacenero', 'Controla el stock de productos y proveedores.'),
    ('Contador', 'Accede a facturas, pagos y reportes financieros.'),
    ('Cliente', 'Acceso de solo lectura a sus propios pedidos.'),
    ('Auditor', 'Acceso de solo lectura a todos los registros.'),
    ('Soporte Técnico', 'Acceso para mantenimiento del sistema.'),
    ('Gerente de Tienda', 'Supervisa operaciones diarias de una sucursal.'),
    ('Invitado', 'Acceso muy limitado, solo consulta pública.')
    RETURNING id
)
SELECT array_agg(id) INTO role_ids FROM inserted_roles;

WITH inserted_modules AS (
    INSERT INTO module (code, name, route, icon, sort_order) VALUES
    ('DASHBOARD', 'Dashboard', '/dashboard', 'dashboard', 1),
    ('SECURITY', 'Seguridad', '/security', 'security', 2),
    ('INVENTORY', 'Inventario', '/inventory', 'inventory_2', 3),
    ('SALES', 'Ventas', '/sales', 'point_of_sale', 4),
    ('BILLING', 'Facturación', '/billing', 'receipt_long', 5),
    ('REPORTS', 'Reportes', '/reports', 'analytics', 6),
    ('SETTINGS', 'Configuración', '/settings', 'settings', 7),
    ('PARAMETERS', 'Parámetros', '/parameters', 'tune', 8),
    ('E-COMMERCE', 'E-commerce', '/ecommerce', 'storefront', 9),
    ('CRM', 'CRM', '/crm', 'group', 10)
    RETURNING id
)
SELECT array_agg(id) INTO module_ids FROM inserted_modules;

WITH inserted_views AS (
    INSERT INTO view (code, name, route) VALUES
    ('USER_LIST', 'Lista de Usuarios', '/security/users'),
    ('USER_CREATE', 'Crear Usuario', '/security/users/new'),
    ('PRODUCT_LIST', 'Lista de Productos', '/inventory/products'),
    ('PRODUCT_EDIT', 'Editar Producto', '/inventory/products/edit'),
    ('NEW_ORDER', 'Nueva Venta', '/sales/new'),
    ('ORDER_HISTORY', 'Historial de Ventas', '/sales/history'),
    ('INVOICE_LIST', 'Lista de Facturas', '/billing/invoices'),
    ('GENERATE_REPORT', 'Generar Reporte', '/reports/generate'),
    ('ROLE_PERMISSIONS', 'Permisos de Rol', '/security/roles/permissions'),
    ('APP_SETTINGS', 'Ajustes de Aplicación', '/settings/app')
    RETURNING id
)
SELECT array_agg(id) INTO view_ids FROM inserted_views;

-- Insertar usuarios. Usamos crypt() para generar un hash de contraseña seguro.
-- Contraseña para todos: 'password123'
WITH inserted_users AS (
    INSERT INTO "user" (username, password_hash, person_id) VALUES
    ('agarcia', crypt('password123', gen_salt('bf')), person_ids[1]),
    ('lmartinez', crypt('password123', gen_salt('bf')), person_ids[2]),
    ('srodriguez', crypt('password123', gen_salt('bf')), person_ids[3]),
    ('chernandez', crypt('password123', gen_salt('bf')), person_ids[4]),
    ('llopez', crypt('password123', gen_salt('bf')), person_ids[5]),
    ('dperez', crypt('password123', gen_salt('bf')), person_ids[6]),
    ('mgomez', crypt('password123', gen_salt('bf')), person_ids[7]),
    ('jdiaz', crypt('password123', gen_salt('bf')), person_ids[8]),
    ('esanchez', crypt('password123', gen_salt('bf')), person_ids[9]),
    ('mtorres', crypt('password123', gen_salt('bf')), person_ids[10])
    RETURNING id
)
SELECT array_agg(id) INTO user_ids FROM inserted_users;

-- Relaciones muchos a muchos
INSERT INTO user_role (user_id, role_id) VALUES
(user_ids[1], role_ids[1]), -- agarcia -> Superadministrador
(user_ids[2], role_ids[2]), -- lmartinez -> Administrador
(user_ids[3], role_ids[3]), -- srodriguez -> Vendedor
(user_ids[4], role_ids[4]), -- chernandez -> Almacenero
(user_ids[5], role_ids[5]), -- llopez -> Contador
(user_ids[6], role_ids[6]), -- dperez -> Cliente
(user_ids[1], role_ids[2]), -- agarcia tambien es Administrador
(user_ids[2], role_ids[3]), -- lmartinez tambien es Vendedor
(user_ids[7], role_ids[3]), -- mgomez -> Vendedor
(user_ids[8], role_ids[4]); -- jdiaz -> Almacenero

INSERT INTO role_module (role_id, module_id) VALUES
(role_ids[1], module_ids[1]), (role_ids[1], module_ids[2]), (role_ids[1], module_ids[3]), (role_ids[1], module_ids[4]), -- Superadmin -> todos los modulos
(role_ids[2], module_ids[1]), (role_ids[2], module_ids[2]), (role_ids[2], module_ids[3]), -- Admin -> Dashboard, Seguridad, Inventario
(role_ids[3], module_ids[1]), (role_ids[3], module_ids[4]), -- Vendedor -> Dashboard, Ventas
(role_ids[4], module_ids[3]), -- Almacenero -> Inventario
(role_ids[5], module_ids[5]), (role_ids[5], module_ids[6]); -- Contador -> Facturación, Reportes

INSERT INTO module_view (module_id, view_id) VALUES
(module_ids[2], view_ids[1]), (module_ids[2], view_ids[2]), (module_ids[2], view_ids[9]), -- Seguridad -> Vistas de Usuario y Roles
(module_ids[3], view_ids[3]), (module_ids[3], view_ids[4]), -- Inventario -> Vistas de Producto
(module_ids[4], view_ids[5]), (module_ids[4], view_ids[6]), -- Ventas -> Vistas de Órdenes
(module_ids[5], view_ids[7]), -- Facturación -> Vista de Facturas
(module_ids[6], view_ids[8]), -- Reportes -> Vista de Generar Reporte
(module_ids[7], view_ids[10]); -- Configuración -> Vista de Ajustes

-- ============================================================
-- MODULE 3: Inventory
-- ============================================================
WITH inserted_categories AS (
    INSERT INTO category (name) VALUES
    ('Electrónica'), ('Ropa y Accesorios'), ('Hogar y Cocina'), ('Libros'), ('Deportes'),
    ('Juguetes y Juegos'), ('Salud y Cuidado Personal'), ('Herramientas'), ('Automotriz'), ('Alimentos y Bebidas')
    RETURNING id
)
SELECT array_agg(id) INTO category_ids FROM inserted_categories;

WITH inserted_suppliers AS (
    INSERT INTO supplier (name, tax_id, email) VALUES
    ('ElectroTech S.A.', '20112233445', 'ventas@electrotech.com'),
    ('ModaGlobal Corp.', '20223344556', 'contacto@modaglobal.com'),
    ('HogarEsencial Ltd.', '20334455667', 'pedidos@hogaresencial.net'),
    ('Librería El Saber', '10445566778', 'info@elsaber.org'),
    ('DeporteTotal S.R.L.', '20556677889', 'soporte@deportetotal.com'),
    ('Juguetes Fantásticos', '20667788990', 'distribucion@juguetesfantasticos.com'),
    ('Bienestar Natural', '10778899001', 'comercial@bienestarnatural.com'),
    ('Ferretería Industrial', '20889900112', 'cotizaciones@ferreindustrial.com'),
    ('AutoPartes Express', '20990011223', 'clientes@autopartesexpress.com'),
    ('Distribuidora La Canasta', '20001122334', 'ventas@lacanasta.com.pe')
    RETURNING id
)
SELECT array_agg(id) INTO supplier_ids FROM inserted_suppliers;

WITH inserted_products AS (
    INSERT INTO product (name, sku, price, cost, category_id, supplier_id, image_file_id) VALUES
    ('Laptop Pro 15"', 'LP15-2024', 1200.00, 850.00, category_ids[1], supplier_ids[1], file_ids[3]),
    ('Camiseta de Algodón', 'CA-BLK-M', 25.50, 12.00, category_ids[2], supplier_ids[2], NULL),
    ('Licuadora PowerBlend', 'LI-PB-500', 80.00, 55.00, category_ids[3], supplier_ids[3], file_ids[4]),
    ('El Señor de los Anillos', 'BOOK-LOTR', 35.00, 20.00, category_ids[4], supplier_ids[4], NULL),
    ('Balón de Fútbol Pro', 'BAL-FP-05', 45.00, 28.00, category_ids[5], supplier_ids[5], file_ids[8]),
    ('Set de Construcción 500p', 'SET-C-500', 60.00, 40.00, category_ids[6], supplier_ids[6], NULL),
    ('Vitamina C 1000mg', 'VIT-C-100', 15.00, 8.00, category_ids[7], supplier_ids[7], NULL),
    ('Taladro Inalámbrico 18V', 'TAL-18V', 150.00, 110.00, category_ids[8], supplier_ids[8], file_ids[9]),
    ('Filtro de Aceite X-123', 'FIL-X123', 12.00, 7.50, category_ids[9], supplier_ids[9], NULL),
    ('Café Orgánico 1kg', 'CAF-ORG-1K', 22.00, 16.00, category_ids[10], supplier_ids[10], NULL)
    RETURNING id
)
SELECT array_agg(id) INTO product_ids FROM inserted_products;

INSERT INTO inventory (product_id, quantity, min_quantity)
SELECT id, floor(random() * 100 + 10)::int, 10 FROM unnest(product_ids) as t(id);

-- ============================================================
-- MODULE 4 & 5: Sales & Billing
-- ============================================================
WITH inserted_customers AS (
    INSERT INTO customer (person_id, customer_code) VALUES
    (person_ids[5], 'CUST-001'), (person_ids[6], 'CUST-002'), (person_ids[7], 'CUST-003'),
    (person_ids[8], 'CUST-004'), (person_ids[9], 'CUST-005'), (person_ids[10], 'CUST-006'),
    (person_ids[1], 'CUST-007'), (person_ids[2], 'CUST-008'), (person_ids[3], 'CUST-009'),
    (person_ids[4], 'CUST-010')
    RETURNING id
)
SELECT array_agg(id) INTO customer_ids FROM inserted_customers;

WITH inserted_payments AS (
    INSERT INTO method_payment (code, name) VALUES
    ('CASH', 'Efectivo'), ('DEBIT_CARD', 'Tarjeta de Débito'), ('CREDIT_CARD', 'Tarjeta de Crédito'),
    ('BANK_TRANSFER', 'Transferencia Bancaria'), ('PAYPAL', 'PayPal'), ('YAPE', 'Yape'),
    ('PLIN', 'Plin'), ('GIFT_CARD', 'Tarjeta de Regalo'), ('STORE_CREDIT', 'Crédito de Tienda'),
    ('CHECK', 'Cheque')
    RETURNING id
)
SELECT array_agg(id) INTO method_payment_ids FROM inserted_payments;

-- Insertar órdenes, luego items, luego facturas y finalmente pagos.
WITH inserted_orders AS (
    INSERT INTO "order" (order_number, customer_id, total_amount) VALUES
    ('ORD-2024-001', customer_ids[1], 1225.50), ('ORD-2024-002', customer_ids[2], 80.00),
    ('ORD-2024-003', customer_ids[3], 35.00), ('ORD-2024-004', customer_ids[1], 90.00),
    ('ORD-2024-005', customer_ids[4], 60.00), ('ORD-2024-006', customer_ids[5], 30.00),
    ('ORD-2024-007', customer_ids[2], 150.00), ('ORD-2024-008', customer_ids[6], 12.00),
    ('ORD-2024-009', customer_ids[7], 44.00), ('ORD-2024-010', customer_ids[8], 1200.00)
    RETURNING id
)
SELECT array_agg(id) INTO order_ids FROM inserted_orders;

INSERT INTO order_item (order_id, product_id, quantity, unit_price, line_total) VALUES
(order_ids[1], product_ids[1], 1, 1200.00, 1200.00), (order_ids[1], product_ids[2], 1, 25.50, 25.50),
(order_ids[2], product_ids[3], 1, 80.00, 80.00),
(order_ids[3], product_ids[4], 1, 35.00, 35.00),
(order_ids[4], product_ids[5], 2, 45.00, 90.00),
(order_ids[5], product_ids[6], 1, 60.00, 60.00),
(order_ids[6], product_ids[7], 2, 15.00, 30.00),
(order_ids[7], product_ids[8], 1, 150.00, 150.00),
(order_ids[8], product_ids[9], 1, 12.00, 12.00),
(order_ids[9], product_ids[10], 2, 22.00, 44.00),
(order_ids[10], product_ids[1], 1, 1200.00, 1200.00);

WITH inserted_invoices AS (
    INSERT INTO invoice (invoice_number, order_id, total_amount, status_invoice) VALUES
    ('F001-001', order_ids[1], 1225.50, 'PAID'), ('F001-002', order_ids[2], 80.00, 'PAID'),
    ('F001-003', order_ids[3], 35.00, 'PENDING'), ('F001-004', order_ids[4], 90.00, 'PAID'),
    ('F001-005', order_ids[5], 60.00, 'PENDING'), ('F001-006', order_ids[6], 30.00, 'PAID'),
    ('F001-007', order_ids[7], 150.00, 'PAID'), ('F001-008', order_ids[8], 12.00, 'OVERDUE'),
    ('F001-009', order_ids[9], 44.00, 'PENDING'), ('F001-010', order_ids[10], 1200.00, 'PAID')
    RETURNING id
)
SELECT array_agg(id) INTO invoice_ids FROM inserted_invoices;

-- Los items de la factura pueden ser los mismos que los de la orden.
INSERT INTO invoice_item (invoice_id, product_id, description, quantity, unit_price, line_total)
SELECT inv.id, oi.product_id, p.name, oi.quantity, oi.unit_price, oi.line_total
FROM invoice inv
JOIN "order" o ON inv.order_id = o.id
JOIN order_item oi ON o.id = oi.order_id
JOIN product p ON oi.product_id = p.id;

-- Insertar pagos para las facturas pagadas.
INSERT INTO payment (invoice_id, method_payment_id, amount_paid, transaction_reference) VALUES
(invoice_ids[1], method_payment_ids[3], 1225.50, 'TXN_CC_111'),
(invoice_ids[2], method_payment_ids[6], 80.00, 'TXN_YAPE_222'),
(invoice_ids[4], method_payment_ids[2], 90.00, 'TXN_DEBIT_444'),
(invoice_ids[6], method_payment_ids[1], 30.00, NULL),
(invoice_ids[7], method_payment_ids[5], 150.00, 'TXN_PAYPAL_777'),
(invoice_ids[10], method_payment_ids[4], 1200.00, 'TXN_BANK_1010');

RAISE NOTICE '¡Datos de prueba insertados correctamente!';

END $$;
