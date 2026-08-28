-- Vincula cada usuario del sistema (login) con un empleado (datos de
-- negocio + privilegios). Nullable a proposito: puede haber usuarios
-- (ej. una cuenta de administracion pura) sin un Employee asociado.

ALTER TABLE users
    ADD COLUMN employee_id INT NULL,
    ADD CONSTRAINT fk_users_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON DELETE SET NULL;

-- Opcional:
-- UPDATE users SET employee_id = 2 WHERE id = 1;



