-- ==========================================
-- 1. USUARIOS INICIALES (ADMIN Y FARMACIAS)
-- ==========================================
INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('eugenio@medimapa.cl', '$2a$10$51smH0aSZ/6tcZusb8mrX.QCt.eEd5LvLh6e1rLaKC7JI9X3ojQ7e', 'ADMIN')
ON CONFLICT (correo) DO NOTHING;

INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('contacto@farmacialaflorida.cl', '$2a$10$bYiM0BijMIsbVi4DkNff7eGhGlFPRHJXYE/rH.wNuZR6B56RAwl/y', 'FARMACEUTICO')
ON CONFLICT (correo) DO NOTHING;