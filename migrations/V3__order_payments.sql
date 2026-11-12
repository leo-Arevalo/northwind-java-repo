-- Pagos de cada factura. Una factura puede tener varios pagos (pagos mixtos:
-- por ejemplo parte en efectivo y parte con tarjeta); la suma de los montos
-- tiene que dar el total, eso lo valida el backend al crear la factura.
-- orders.payment_type queda como etiqueta resumen: el metodo si hay uno solo,
-- "Mixto" si se combinan varios.
--
-- Segun el metodo se completan unas columnas u otras:
--   Efectivo      -> ninguna
--   Tarjeta       -> card_type (DEBIT/CREDIT), card_brand, pos_voucher_number,
--                    authorization_code; card_last4 es opcional;
--                    installments solo en credito
--   Transferencia -> transfer_reference; transfer_bank es opcional
--
-- Nunca se guarda el numero completo de la tarjeta ni el codigo de
-- seguridad: solo lo que el posnet entrega al comercio como comprobante.

CREATE TABLE order_payments (
    id                  INT           NOT NULL AUTO_INCREMENT,
    order_id            INT           NOT NULL,
    payment_method      VARCHAR(20)   NOT NULL,
    card_type           VARCHAR(10)   NULL,
    card_brand          VARCHAR(30)   NULL,
    card_last4          VARCHAR(4)    NULL,
    installments        INT           NULL,
    pos_voucher_number  VARCHAR(30)   NULL,
    authorization_code  VARCHAR(20)   NULL,
    transfer_reference  VARCHAR(50)   NULL,
    transfer_bank       VARCHAR(50)   NULL,
    amount              DECIMAL(19,4) NOT NULL,
    paid_at             DATETIME      NOT NULL,
    PRIMARY KEY (id),
    KEY idx_order_payments_order (order_id),
    CONSTRAINT fk_order_payments_order
        FOREIGN KEY (order_id) REFERENCES orders (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
