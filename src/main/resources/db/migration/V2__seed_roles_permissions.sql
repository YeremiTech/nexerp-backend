INSERT INTO erp.permissions (code, description)
VALUES
    ('DASHBOARD_READ', 'Ver dashboard'),
    ('REPORT_READ', 'Ver reportes'),
    ('ROLE_READ', 'Ver roles y permisos'),
    ('ROLE_WRITE', 'Gestionar roles y permisos'),
    ('USER_READ', 'Ver usuarios'),
    ('USER_WRITE', 'Gestionar usuarios'),
    ('CLIENT_READ', 'Ver clientes'),
    ('CLIENT_WRITE', 'Gestionar clientes'),
    ('SUPPLIER_READ', 'Ver proveedores'),
    ('SUPPLIER_WRITE', 'Gestionar proveedores'),
    ('CATEGORY_READ', 'Ver categorias'),
    ('CATEGORY_WRITE', 'Gestionar categorias'),
    ('PRODUCT_READ', 'Ver productos'),
    ('PRODUCT_WRITE', 'Gestionar productos'),
    ('INVENTORY_READ', 'Ver inventario'),
    ('INVENTORY_WRITE', 'Gestionar inventario'),
    ('PURCHASE_READ', 'Ver compras'),
    ('PURCHASE_WRITE', 'Gestionar compras'),
    ('SALE_READ', 'Ver ventas'),
    ('SALE_WRITE', 'Gestionar ventas')
ON CONFLICT (code) DO NOTHING;

INSERT INTO erp.roles (name, description, active)
VALUES
    ('ADMIN', 'Administrador con acceso completo', TRUE),
    ('OPERATOR', 'Operador con permisos transaccionales del ERP', TRUE),
    ('VIEWER', 'Usuario de solo lectura', TRUE)
ON CONFLICT (name) DO NOTHING;

INSERT INTO erp.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM erp.roles r
CROSS JOIN erp.permissions p
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO erp.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM erp.roles r
JOIN erp.permissions p ON p.code IN (
    'DASHBOARD_READ',
    'CLIENT_READ', 'CLIENT_WRITE',
    'SUPPLIER_READ', 'SUPPLIER_WRITE',
    'CATEGORY_READ',
    'PRODUCT_READ', 'PRODUCT_WRITE',
    'INVENTORY_READ', 'INVENTORY_WRITE',
    'PURCHASE_READ', 'PURCHASE_WRITE',
    'SALE_READ', 'SALE_WRITE',
    'REPORT_READ'
)
WHERE r.name = 'OPERATOR'
ON CONFLICT DO NOTHING;

INSERT INTO erp.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM erp.roles r
JOIN erp.permissions p ON p.code IN (
    'DASHBOARD_READ',
    'REPORT_READ',
    'ROLE_READ',
    'USER_READ',
    'CLIENT_READ',
    'SUPPLIER_READ',
    'CATEGORY_READ',
    'PRODUCT_READ',
    'INVENTORY_READ',
    'PURCHASE_READ',
    'SALE_READ'
)
WHERE r.name = 'VIEWER'
ON CONFLICT DO NOTHING;
