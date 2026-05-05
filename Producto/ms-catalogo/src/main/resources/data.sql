INSERT INTO medicamento (id_medicamento, nombre_canonico, principio_activo, origen_catalogo, activo, es_bioequivalente, categoria) VALUES
-- Categoría: Analgésicos y antiinflamatorios
(1, 'Paracetamol', 'Paracetamol', 'Nacional', true, true, 'Analgésicos y antiinflamatorios'),
(2, 'Ibuprofeno', 'Ibuprofeno', 'Nacional', true, true, 'Analgésicos y antiinflamatorios'),
(11, 'Ácido Mefenámico', 'Ácido Mefenámico', 'Nacional', true, true, 'Analgésicos y antiinflamatorios'),

-- Categoría: Antialérgicos
(12, 'Loratadina', 'Loratadina', 'Nacional', true, true, 'Antialérgicos'),
(13, 'Desloratadina', 'Desloratadina', 'Nacional', true, true, 'Antialérgicos'),

-- Categoría: Antibióticos y antibacterianos
(31, 'Amoxicilina', 'Amoxicilina', 'Nacional', true, true, 'Antibióticos y antibacterianos'),
(32, 'Azitromicina', 'Azitromicina', 'Nacional', true, true, 'Antibióticos y antibacterianos'),

-- Categoría: Cardiovascular y circulación
(20, 'Losartan', 'Losartán', 'Nacional', true, true, 'Cardiovascular y circulación'),
(24, 'Atorvastatina', 'Atorvastatina', 'Nacional', true, true, 'Cardiovascular y circulación'),

-- Categoría: Salud mental y neurológico
(44, 'Sertralina', 'Sertralina', 'Nacional', true, true, 'Salud mental y neurológico'),
(45, 'Clonazepam', 'Clonazepam', 'Nacional', true, true, 'Salud mental y neurológico'),

-- Categoría: Digestivo y gastrointestinal
(38, 'Omeprazol', 'Omeprazol', 'Nacional', true, true, 'Digestivo y gastrointestinal'),
(41, 'Viadil', 'Pargeverina', 'Nacional', true, true, 'Digestivo y gastrointestinal');

-- Insertar Región
INSERT INTO region (id_region, nombre) VALUES (1, 'Región Metropolitana') ON CONFLICT DO NOTHING;

-- Insertar Comuna (asumiendo id_region = 1)
INSERT INTO comuna (id, nombre_com, id_region) VALUES (1, 'La Florida', 1) ON CONFLICT DO NOTHING;

-- Insertar farmacias normalizadas
INSERT INTO farmacias (id, nombre) VALUES (1, 'Farmacias Ahumada') ON CONFLICT DO NOTHING;
INSERT INTO farmacias (id, nombre) VALUES (2, 'Farmacias Dr. Simi') ON CONFLICT DO NOTHING;
INSERT INTO farmacias (id, nombre) VALUES (3, 'Salcobrand') ON CONFLICT DO NOTHING;

-- Insertar sucursales con PostGIS y referencia a comuna
-- Nota: En ST_GeomFromText, el orden es (LONGITUD LATITUD)

-- ==========================================
-- SUCURSALES DR. SIMI (id_farmacia = 2)
-- ==========================================
INSERT INTO sucursal_farmacia (id_farmacia, id_comuna, nombre_sucursal, direccion, ubicacion, activo) VALUES 
(2, 1, 'Dr. Simi - Vicuña Mackenna 7287', 'Avda. Vicuña Mackenna 7287', ST_GeomFromText('POINT(-70.5987675 -33.5202092)', 4326), true),
(2, 1, 'Dr. Simi - La Florida 9660', 'Avda. La Florida 9660', ST_GeomFromText('POINT(-70.5697048 -33.5428264)', 4326), true),
(2, 1, 'Dr. Simi - Walker Martínez 1786', 'Walker Martínez N°1786', ST_GeomFromText('POINT(-70.5795070 -33.5221014)', 4326), true),
(2, 1, 'Dr. Simi - La Florida 8220', 'Avda. La Florida N°8220', ST_GeomFromText('POINT(-70.5755567 -33.5281624)', 4326), true),
(2, 1, 'Dr. Simi - Vicuña Mackenna 7110 Local 15 Blvd', 'Avda. Vicuña Mackenna N°7110 Local 15 Boulevard', ST_GeomFromText('POINT(-70.5999735 -33.5165652)', 4326), true),
(2, 1, 'Dr. Simi - La Florida 9073 Local 3', 'Avenida La Florida 9073 local 3', ST_GeomFromText('POINT(-70.5745221 -33.5344348)', 4326), true),
(2, 1, 'Dr. Simi - Serafín Zamora 35', 'Serafín Zamora 35', ST_GeomFromText('POINT(-70.6005452 -33.5211529)', 4326), true),
(2, 1, 'Dr. Simi - Vicuña Mackenna 7110 Local D-104', 'Avda Vicuña Mackenna N°7110 Local D-104', ST_GeomFromText('POINT(-70.5981728 -33.5178218)', 4326), true),
(2, 1, 'Dr. Simi - Vicuña Mackenna 7110 Local D-105', 'Avda Vicuña Mackenna N°7110 Local D-105', ST_GeomFromText('POINT(-70.5954112 -33.5175921)', 4326), true),
(2, 1, 'Dr. Simi - Vicuña Mackenna 7110 Local M-1', 'Avda. Vicuña Mackenna N°7110 local M-1', ST_GeomFromText('POINT(-70.5997773 -33.5193287)', 4326), true),
(2, 1, 'Dr. Simi - La Florida 8988', 'Avda La Florida 8988', ST_GeomFromText('POINT(-70.5723002 -33.5352988)', 4326), true);

-- ==========================================
-- SUCURSALES SALCOBRAND (id_farmacia = 3)
-- ==========================================
INSERT INTO sucursal_farmacia (id_farmacia, id_comuna, nombre_sucursal, direccion, ubicacion, activo) VALUES
(3, 1, 'Salcobrand - Mall Plaza Vespucio', 'Av. Vicuña Mackenna 7110, Local 120', ST_GeomFromText('POINT(-70.5970 -33.5215)', 4326), true),
(3, 1, 'Salcobrand - Florida Center', 'Av. Vicuña Mackenna 6100, Local 1050', ST_GeomFromText('POINT(-70.5990 -33.5210)', 4326), true),
(3, 1, 'Salcobrand - Paseo La Florida', 'Av. La Florida 9301', ST_GeomFromText('POINT(-70.5535 -33.5445)', 4326), true),
(3, 1, 'Salcobrand - Servicentro La Florida', 'Avda. La Florida N° 9871, Servicentro', ST_GeomFromText('POINT(-70.5685689 -33.5481970)', 4326), true),
(3, 1, 'Salcobrand - Mall / Strip Center 6100', 'Avda. Vicuña Mackenna N° 6100 Mall / Strip Center', ST_GeomFromText('POINT(-70.6083241 -33.5105496)', 4326), true),
(3, 1, 'Salcobrand - Mall / Strip Center Rojas Magallanes 1280', 'Avda. Rojas Magallanes N° 1280 Mall / Strip Center', ST_GeomFromText('POINT(-70.5739931 -33.5354549)', 4326), true),
(3, 1, 'Salcobrand - Mall / Strip Center Vicuña Mackenna 11091', 'Avda. Vicuña Mackenna N° 11091 Mall / Strip Center', ST_GeomFromText('POINT(-70.5860475 -33.5609251)', 4326), true),
(3, 1, 'Salcobrand - Supermercado Vicuña Mackenna 6331', 'Avda. Vicuña Mackenna N° 6331 L-5, Supermercado', ST_GeomFromText('POINT(-70.5998213 -33.5192220)', 4326), true),
(3, 1, 'Salcobrand - Local 24', 'Avda.Vicuña Mackenna N° 7304 - 7308 Local 24', ST_GeomFromText('POINT(-70.5977248 -33.5254997)', 4326), true),
(3, 1, 'Salcobrand - Vicuña Mackenna 9101', 'Avda. Vicuña Mackenna N° 9101', ST_GeomFromText('POINT(-70.5916431 -33.5398648)', 4326), true),
(3, 1, 'Salcobrand - Local 110 Mall / Strip Center', 'Avda. Vicuña Mackenna Oriente N° 7110 Local 110 Mall / Strip Center', ST_GeomFromText('POINT(-70.5980852 -33.51760177)', 4326), true),
(3, 1, 'Salcobrand - Local A', 'Avda. Departamental N° 1455 Local A Hospital / Clínica / Centro Medico La Florida', ST_GeomFromText('POINT(-70.5965847 -33.5101675)', 4326), true),
(3, 1, 'Salcobrand - TM 129 Vicuña Mackenna 7110', 'Avda. Vicuña Mackenna N° 7110 TM 129', ST_GeomFromText('POINT(-70.5997566 -33.5193173)', 4326), true),
(3, 1, 'Salcobrand - Local 1', 'Avda. Vicuña Mackenna 8733 Local 1', ST_GeomFromText('POINT(-70.5934514 -33.5354859)', 4326), true),
(3, 1, 'Salcobrand - Local E-9140', 'Avda. Vicuña Mackenna N° 7110 Local E-9140', ST_GeomFromText('POINT(-70.5997566 -33.5193173)', 4326), true);

-- ==========================================
-- SUCURSALES AHUMADA (id_farmacia = 1)
-- ==========================================
INSERT INTO sucursal_farmacia (id_farmacia, id_comuna, nombre_sucursal, direccion, ubicacion, activo) VALUES
(1, 1, 'Ahumada - Vicuña Mackenna 7110 Local 12', 'Vicuña Mackenna N° 7110 local Nº12', ST_GeomFromText('POINT(-70.5977102 -33.5180275)', 4326), true),
(1, 1, 'Ahumada - Vicuña Mackenna 7110 Local E-9109', 'Avda. Vicuña Mackenna N° 7110 Local E-9109', ST_GeomFromText('POINT(-70.5978765 -33.5178889)', 4326), true),
(1, 1, 'Ahumada - Froilán Roa 7107', 'Froilán Roa N° 7107', ST_GeomFromText('POINT(-70.59781877 -33.5162832)', 4326), true),
(1, 1, 'Ahumada - Vicuña Mackenna 7196', 'Avda. Vicuña Mackenna N° 7196', ST_GeomFromText('POINT(-70.6021772 -33.5193445)', 4326), true),
(1, 1, 'Ahumada - Américo Vespucio 7310', 'Avda. Américo Vespucio N° 7310', ST_GeomFromText('POINT(-70.5954295 -33.5196461)', 4326), true),
(1, 1, 'Ahumada - Vicuña Mackenna 6100 Local 102', 'Avda. Vicuña Mackenna N° 6100 L. 102', ST_GeomFromText('POINT(-70.6061243 -33.5106191)', 4326), true),
(1, 1, 'Ahumada - Américo Vespucio 6325', 'Avda. Américo Vespucion N° 6325', ST_GeomFromText('POINT(-70.5917070 -33.5118741)', 4326), true),
(1, 1, 'Ahumada - Vicuña Mackenna 9521', 'Avda. Vicuña Mackenna N° 9521', ST_GeomFromText('POINT(-70.5896604 -33.5435570)', 4326), true),
(1, 1, 'Ahumada - La Florida 9497', 'Avda. La Florida N° 9497', ST_GeomFromText('POINT(-70.5705468 -33.5432607)', 4326), true),
(1, 1, 'Ahumada - Santa Amalia Supermercado Líder', 'Avda. Santa Amalia N° 1763 / Supermercado Líder', ST_GeomFromText('POINT(-70.5708940 -33.5449876)', 4326), true),
(1, 1, 'Ahumada - Rojas Magallanes 3638', 'Rojas Magallanes N° 3638', ST_GeomFromText('POINT(-70.5558007 -33.5354752)', 4326), true),
(1, 1, 'Ahumada - Walker Martínez 3600', 'Avda. Walker Martínez Nº 3600', ST_GeomFromText('POINT(-70.5587105 -33.5218443)', 4326), true),
(1, 1, 'Ahumada - La Florida 8988', 'Avda. La Florida 8988', ST_GeomFromText('POINT(-70.5720425 -33.5349448)', 4326), true);
select * from sucursal_farmacia;

INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('eugenio@medimapa.cl', '$2a$10$51smH0aSZ/6tcZusb8mrX.QCt.eEd5LvLh6e1rLaKC7JI9X3ojQ7e', 'ADMIN');

INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('contacto@farmacialaflorida.cl', '$2a$10$bYiM0BijMIsbVi4DkNff7eGhGlFPRHJXYE/rH.wNuZR6B56RAwl/y', 'FARMACEUTICO');