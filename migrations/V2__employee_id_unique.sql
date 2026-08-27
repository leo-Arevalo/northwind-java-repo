-- Un empleado no puede tener mas de una cuenta de usuario vinculada.
-- No se agrega en V1 porque esa migración ya se aplico contra la base
-- real;

ALTER TABLE users
    ADD CONSTRAINT uq_users_employee_id UNIQUE (employee_id);