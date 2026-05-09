-- ==========================================
-- 4. USUARIOS DEL SISTEMA
-- ==========================================
INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('eugenio@medimapa.cl', '$2a$10$8.UnVuG9HLdaV9p0aM4.0.1M3gCqQkX2y6D.1j0m9.Y7.Q91.2i/m', 'ADMIN') 
ON CONFLICT DO NOTHING;

INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('contacto@farmacialaflorida.cl', '$2a$10$bYiM0BijMIsbVi4DkNff7eGhGlFPRHJXYE/rH.wNuZR6B56RAwl/y', 'FARMACEUTICO')
ON CONFLICT DO NOTHING;