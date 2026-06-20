CREATE INDEX idx_users_active ON erp.users (active);
CREATE INDEX idx_users_username_lower ON erp.users (LOWER(username));
CREATE INDEX idx_users_email_lower ON erp.users (LOWER(email));

CREATE INDEX idx_roles_active ON erp.roles (active);

CREATE INDEX idx_clients_active ON erp.clients (active);
CREATE INDEX idx_clients_type ON erp.clients (type);
CREATE INDEX idx_clients_tax_id ON erp.clients (tax_id);
CREATE INDEX idx_clients_name_lower ON erp.clients (LOWER(name));

CREATE INDEX idx_suppliers_active ON erp.suppliers (active);
CREATE INDEX idx_suppliers_tax_id ON erp.suppliers (tax_id);
CREATE INDEX idx_suppliers_name_lower ON erp.suppliers (LOWER(name));

CREATE INDEX idx_categories_parent_id ON erp.categories (parent_id);
CREATE INDEX idx_categories_active ON erp.categories (active);
CREATE INDEX idx_categories_name_lower ON erp.categories (LOWER(name));

CREATE INDEX idx_products_category_id ON erp.products (category_id);
CREATE INDEX idx_products_active ON erp.products (active);
CREATE INDEX idx_products_name_lower ON erp.products (LOWER(name));

CREATE INDEX idx_product_prices_product_id ON erp.product_prices (product_id);
CREATE INDEX idx_product_prices_current ON erp.product_prices (product_id, valid_to);
CREATE INDEX idx_product_images_product_id ON erp.product_images (product_id);

CREATE INDEX idx_warehouses_active ON erp.warehouses (active);
CREATE INDEX idx_warehouses_name_lower ON erp.warehouses (LOWER(name));

CREATE INDEX idx_inventory_items_product_id ON erp.inventory_items (product_id);
CREATE INDEX idx_inventory_items_warehouse_id ON erp.inventory_items (warehouse_id);
CREATE INDEX idx_inventory_items_quantity ON erp.inventory_items (quantity);

CREATE INDEX idx_inventory_movements_product_created ON erp.inventory_movements (product_id, created_at DESC);
CREATE INDEX idx_inventory_movements_warehouse_id ON erp.inventory_movements (warehouse_id);
CREATE INDEX idx_inventory_movements_type_created ON erp.inventory_movements (type, created_at DESC);
CREATE INDEX idx_inventory_movements_reference ON erp.inventory_movements (reference_type, reference_id);

CREATE INDEX idx_purchase_orders_supplier_id ON erp.purchase_orders (supplier_id);
CREATE INDEX idx_purchase_orders_status_created ON erp.purchase_orders (status, created_at DESC);
CREATE INDEX idx_purchase_order_lines_order_id ON erp.purchase_order_lines (order_id);
CREATE INDEX idx_purchase_order_lines_product_id ON erp.purchase_order_lines (product_id);
CREATE INDEX idx_purchase_receipts_order_id ON erp.purchase_receipts (order_id);

CREATE INDEX idx_sales_carts_client_id ON erp.sales_carts (client_id);
CREATE INDEX idx_sales_cart_items_cart_id ON erp.sales_cart_items (cart_id);
CREATE INDEX idx_sales_cart_items_product_id ON erp.sales_cart_items (product_id);

CREATE INDEX idx_sales_orders_client_id ON erp.sales_orders (client_id);
CREATE INDEX idx_sales_orders_user_id ON erp.sales_orders (user_id);
CREATE INDEX idx_sales_orders_status_created ON erp.sales_orders (status, created_at DESC);
CREATE INDEX idx_sales_order_lines_order_id ON erp.sales_order_lines (order_id);
CREATE INDEX idx_sales_order_lines_product_id ON erp.sales_order_lines (product_id);

CREATE INDEX idx_refresh_tokens_user_id ON erp.refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_active_hash ON erp.refresh_tokens (token_hash, revoked);
CREATE INDEX idx_refresh_tokens_expires_at ON erp.refresh_tokens (expires_at);

CREATE INDEX idx_password_reset_tokens_user_id ON erp.password_reset_tokens (user_id);
CREATE INDEX idx_password_reset_tokens_active_hash ON erp.password_reset_tokens (token_hash, used);
CREATE INDEX idx_password_reset_tokens_expires_at ON erp.password_reset_tokens (expires_at);
