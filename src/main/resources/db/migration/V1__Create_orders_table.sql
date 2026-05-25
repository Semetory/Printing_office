CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    fullname VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    format VARCHAR(10) NOT NULL,
    paper VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    payment VARCHAR(50),
    total INT NOT NULL,
    status VARCHAR(50) DEFAULT 'Принят',
    created_at TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS order_files (
    order_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    CONSTRAINT fk_order_files_orders FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );