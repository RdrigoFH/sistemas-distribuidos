CREATE TABLE IF NOT EXISTS cuentas (
    id SERIAL PRIMARY KEY,
    cliente VARCHAR(100) UNIQUE NOT NULL,
    saldo NUMERIC(14,2) NOT NULL CHECK (saldo >= 0)
);

INSERT INTO cuentas (cliente, saldo)
VALUES
    ('Cliente A', 100000.00),
    ('Cliente B', 50000.00),
    ('Cliente C', 75000.00)
ON CONFLICT (cliente) DO UPDATE
SET saldo = EXCLUDED.saldo;