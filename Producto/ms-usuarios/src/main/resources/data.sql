-- ==========================================
-- 4. USUARIOS DEL SISTEMA
-- ==========================================
INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('eugenio@medimapa.cl', '$2a$10$ToDCdFtfMzBTu31noDemG.9j4EdsMr8t/4uOq2e2dxCICJuYMH6dq', 'ADMIN') 
ON CONFLICT (correo) DO UPDATE SET password_hash = EXCLUDED.password_hash;

INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('contacto@farmacialaflorida.cl', '$2a$10$HcU8pAFSr1VqlZLe5Q3Eye/rAFz6XBqmT/v.PwnciCpZK9R4ktMUG', 'FARMACEUTICO')
ON CONFLICT (correo) DO UPDATE SET password_hash = EXCLUDED.password_hash;