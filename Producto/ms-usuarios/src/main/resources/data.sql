-- ==========================================
-- 1. USUARIOS DEL SISTEMA
-- ==========================================
INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('eugenio@medimapa.cl', '$2a$10$ToDCdFtfMzBTu31noDemG.9j4EdsMr8t/4uOq2e2dxCICJuYMH6dq', 'ADMIN') 
ON CONFLICT (correo) DO UPDATE SET password_hash = EXCLUDED.password_hash;

INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('contacto@farmacialaflorida.cl', '$2a$10$HcU8pAFSr1VqlZLe5Q3Eye/rAFz6XBqmT/v.PwnciCpZK9R4ktMUG', 'FARMACEUTICO')
ON CONFLICT (correo) DO UPDATE SET password_hash = EXCLUDED.password_hash;

-- ==========================================
-- 2. GEOGRAFÍA (PostGIS)
-- ==========================================
INSERT INTO region (id_region, nombre) VALUES (1, 'Región Metropolitana') ON CONFLICT DO NOTHING;

-- En ms-usuarios la tabla comuna usa 'id' como PK según tus logs
INSERT INTO comuna (id, nombre_com, id_region) VALUES (1, 'La Florida', 1) ON CONFLICT DO NOTHING;

-- ==========================================
-- 3. CADENAS DE FARMACIA
-- ==========================================
INSERT INTO cadena_farmacia (id, nombre) VALUES (1, 'Farmacias Ahumada') ON CONFLICT DO NOTHING;
INSERT INTO cadena_farmacia (id, nombre) VALUES (2, 'Farmacias Dr. Simi') ON CONFLICT DO NOTHING;
INSERT INTO cadena_farmacia (id, nombre) VALUES (3, 'Salcobrand') ON CONFLICT DO NOTHING;
INSERT INTO cadena_farmacia (id, nombre) VALUES (4, 'Farmacia La Florida') ON CONFLICT DO NOTHING;

-- ==========================================
-- 4. SUCURSALES (Data Real de La Florida)
-- ==========================================

-- SUCURSAL DE PRUEBAS PARA EXCEL (ID 99 - Obligatorio para tu React)
INSERT INTO sucursal_farmacia (id_sucursal, id_farmacia, id_comuna, nombre_sucursal, direccion, ubicacion, activo, creado_en)
VALUES (99, 4, 1, 'Farmacia La Florida (Sede Principal)', 'Av. Vicuña Mackenna 7110, La Florida', ST_GeomFromText('POINT(-70.5980 -33.5188)', 4326), true, CURRENT_TIMESTAMP)
ON CONFLICT (id_sucursal) DO NOTHING;

-- SUCURSALES DR. SIMI
INSERT INTO sucursal_farmacia (id_farmacia, id_comuna, nombre_sucursal, direccion, ubicacion, activo) VALUES 
(2, 1, 'Dr. Simi - Vicuña Mackenna 7287', 'Avda. Vicuña Mackenna 7287', ST_GeomFromText('POINT(-70.5987675 -33.5202092)', 4326), true),
(2, 1, 'Dr. Simi - La Florida 9660', 'Avda. La Florida 9660', ST_GeomFromText('POINT(-70.5697048 -33.5428264)', 4326), true),
(2, 1, 'Dr. Simi - Walker Martínez 1786', 'Walker Martínez N°1786', ST_GeomFromText('POINT(-70.5795070 -33.5221014)', 4326), true),
(2, 1, 'Dr. Simi - La Florida 8220', 'Avda. La Florida N°8220', ST_GeomFromText('POINT(-70.5755567 -33.5281624)', 4326), true),
(2, 1, 'Dr. Simi - Vicuña Mackenna 7110 Local 15 Blvd', 'Avda. Vicuña Mackenna N°7110 Local 15 Boulevard', ST_GeomFromText('POINT(-70.5999735 -33.5165652)', 4326), true),
(2, 1, 'Dr. Simi - La Florida 9073 Local 3', 'Avenida La Florida 9073 local 3', ST_GeomFromText('POINT(-70.5745221 -33.5344348)', 4326), true),
(2, 1, 'Dr. Simi - Serafín Zamora 35', 'Serafín Zamora 35', ST_GeomFromText('POINT(-70.6005452 -33.5211529)', 4326), true),
(2, 1, 'Dr. Simi - Vicuña Mackenna 7110 Local D-104', 'Avda Vicuña Mackenna N°7110 Local D-104', ST_GeomFromText('POINT(-70.5981728 -33.5178218)', 4326), true);

-- SUCURSALES SALCOBRAND
INSERT INTO sucursal_farmacia (id_farmacia, id_comuna, nombre_sucursal, direccion, ubicacion, activo) VALUES
(3, 1, 'Salcobrand - Mall Plaza Vespucio', 'Av. Vicuña Mackenna 7110, Local 120', ST_GeomFromText('POINT(-70.5970 -33.5215)', 4326), true),
(3, 1, 'Salcobrand - Florida Center', 'Av. Vicuña Mackenna 6100, Local 1050', ST_GeomFromText('POINT(-70.5990 -33.5210)', 4326), true),
(3, 1, 'Salcobrand - Paseo La Florida', 'Av. La Florida 9301', ST_GeomFromText('POINT(-70.5535 -33.5445)', 4326), true),
(3, 1, 'Salcobrand - Servicentro La Florida', 'Avda. La Florida N° 9871, Servicentro', ST_GeomFromText('POINT(-70.5685689 -33.5481970)', 4326), true),
(3, 1, 'Salcobrand - Mall / Strip Center 6100', 'Avda. Vicuña Mackenna N° 6100 Mall / Strip Center', ST_GeomFromText('POINT(-70.6083241 -33.5105496)', 4326), true);

-- SUCURSALES AHUMADA
INSERT INTO sucursal_farmacia (id_farmacia, id_comuna, nombre_sucursal, direccion, ubicacion, activo) VALUES
(1, 1, 'Ahumada - Vicuña Mackenna 7110 Local 12', 'Vicuña Mackenna N° 7110 local Nº12', ST_GeomFromText('POINT(-70.5977102 -33.5180275)', 4326), true),
(1, 1, 'Ahumada - Froilán Roa 7107', 'Froilán Roa N° 7107', ST_GeomFromText('POINT(-70.59781877 -33.5162832)', 4326), true),
(1, 1, 'Ahumada - Vicuña Mackenna 7196', 'Avda. Vicuña Mackenna N° 7196', ST_GeomFromText('POINT(-70.6021772 -33.5193445)', 4326), true),
(1, 1, 'Ahumada - Américo Vespucio 7310', 'Avda. Américo Vespucio N° 7310', ST_GeomFromText('POINT(-70.5954295 -33.5196461)', 4326), true);