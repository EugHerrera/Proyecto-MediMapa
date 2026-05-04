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
INSERT INTO region (nombre) VALUES ('Región Metropolitana');

-- Insertar Comuna (asumiendo id_region = 1)
INSERT INTO comuna (nombre_com, id_region) VALUES ('La Florida', 1);

-- Insertar Sucursales (asumiendo id_comuna = 1)
INSERT INTO sucursal_farmacia (nombre_sucursal, direccion, latitud, longitud, id_comuna, activo) VALUES 
('Dr. Simi - Vicuña Mackenna 7287', 'Avda. Vicuña Mackenna 7287', -33.5202092, -70.5987675, 1, true),
('Dr. Simi - La Florida 9660', 'Avda. La Florida 9660', -33.5428264, -70.5697048, 1, true),
('Dr. Simi - Walker Martínez 1786', 'Walker Martínez N°1786', -33.5221014, -70.5795070, 1, true),
('Dr. Simi - La Florida 8220', 'Avda. La Florida N°8220', -33.5281624, -70.5755567, 1, true),
('Dr. Simi - Vicuña Mackenna 7110 Local 15 Boulevard', 'Avda. Vicuña Mackenna N°7110 Local 15 Boulevard', -33.5165652, -70.5999735, 1, true),
('Dr. Simi - La Florida 9073 Local 3', 'Avenida La Florida 9073 local 3', -33.5344348, -70.5745221, 1, true),
('Dr. Simi - Serafín Zamora 35', 'Serafín Zamora 35', -33.5211529, -70.6005452, 1, true),
('Dr. Simi - Vicuña Mackenna 7110 Local D-104', 'Avda Vicuña Mackenna N°7110 Local D-104', -33.5178218, -70.5981728, 1, true),
('Dr. Simi - Vicuña Mackenna 7110 Local D-105', 'Avda Vicuña Mackenna N°7110 Local D-105', -33.5175921, -70.5954112, 1, true),
('Dr. Simi - Vicuña Mackenna 7110 Local M-1', 'Avda Vicuña Mackenna N°7110 local M-1', -33.5193287, -70.5997773, 1, true),
('Dr. Simi - La Florida 8988', 'Avda La Florida 8988', -33.5352988, -70.5723002, 1, true),
('Salcobrand - Mall Plaza Vespucio', 'Av. Vicuña Mackenna 7110, Local 120', -33.5215, -70.5970, 1, true),
('Salcobrand - Florida Center', 'Av. Vicuña Mackenna 6100, Local 1050', -33.5210, -70.5990, 1, true),
('Salcobrand - Paseo La Florida', 'Av. La Florida 9301', -33.5445, -70.5535, 1, true),
('Salcobrand - Servicentro La Florida', 'Avda. La Florida N° 9871, Servicentro', -33.5481970, -70.5685689, 1, true),
('Salcobrand - Mall / Strip Center 6100', 'Avda. Vicuña Mackenna N° 6100 Mall / Strip Center', -33.5105496, -70.6083241, 1, true),
('Salcobrand - Mall / Strip Center Rojas Magallanes 1280', 'Avda. Rojas Magallanes N° 1280 Mall / Strip Center', -33.5354549, -70.5739931, 1, true),
('Salcobrand - Mall / Strip Center Vicuña Mackenna 11091', 'Avda. Vicuña Mackenna N° 11091 Mall / Strip Center', -33.5609251, -70.5860475, 1, true),
('Salcobrand - Supermercado Vicuña Mackenna 6331', 'Avda. Vicuña Mackenna N° 6331 L-5, Supermercado', -33.5192220, -70.5998213, 1, true),
('Salcobrand - Local 24', 'Avda.Vicuña Mackenna N° 7304 - 7308 Local 24', -33.5254997, -70.5977248, 1, true),
('Salcobrand - Vicuña Mackenna 9101', 'Avda. Vicuña Mackenna N° 9101', -33.5398648, -70.5916431, 1, true),
('Salcobrand - Local 110 Mall / Strip Center', 'Avda. Vicuña Mackenna Oriente N° 7110 Local 110 Mall / Strip Center', -33.51760177, -70.5980852, 1, true),
('Salcobrand - Local A', 'Avda. Departamental N° 1455 Local A Hospital / Clínica / Centro Medico La Florida', -33.5101675, -70.5965847, 1, true),
('Salcobrand - TM 129 Vicuña Mackenna 7110', 'Avda. Vicuña Mackenna N° 7110 TM 129', -33.5193173, -70.5997566, 1, true),
('Salcobrand - Local 1', 'Avda. Vicuña Mackenna 8733 Local 1', -33.5354859, -70.5934514, 1, true),
('Salcobrand - Local E-9140', 'Avda. Vicuña Mackenna N° 7110 Local E-9140', -33.5193173, -70.5997566, 1, true),
('Ahumada - Vicuña Mackenna 7110 Local 12', 'Vicuña Mackenna N° 7110 local Nº12', -33.5180275, -70.5977102, 1, true),
('Ahumada - Vicuña Mackenna 7110 Local E-9109', 'Avda. Vicuña Mackenna N° 7110 Local E-9109', -33.5178889, -70.5978765, 1, true),
('Ahumada - Froilán Roa 7107', 'Froilán Roa N° 7107', -33.5162832, -70.59781877, 1, true),
('Ahumada - Vicuña Mackenna 7196', 'Avda. Vicuña Mackenna N° 7196', -33.5193445, -70.6021772, 1, true),
('Ahumada - Américo Vespucio 7310', 'Avda. Américo Vespucio N° 7310', -33.5196461, -70.5954295, 1, true),
('Ahumada - Vicuña Mackenna 6100 Local 102', 'Avda. Vicuña Mackenna N° 6100 L. 102', -33.5106191, -70.6061243, 1, true),
('Ahumada - Américo Vespucio 6325', 'Avda. Américo Vespucion N° 6325', -33.5118741, -70.5917070, 1, true),
('Ahumada - Vicuña Mackenna 9521', 'Avda. Vicuña Mackenna N° 9521', -33.5435570, -70.5896604, 1, true),
('Ahumada - La Florida 9497', 'Avda. La Florida N° 9497', -33.5432607, -70.5705468, 1, true),
('Ahumada - Santa Amalia Supermercado Líder', 'Avda. Santa Amalia N° 1763 / Supermercado Líder', -33.5449876, -70.5708940, 1, true),
('Ahumada - Rojas Magallanes 3638', 'Rojas Magallanes N° 3638', -33.5354752, -70.5558007, 1, true),
('Ahumada - Walker Martínez 3600', 'Avda. Walker Martínez Nº 3600', -33.5218443, -70.5587105, 1, true),
('Ahumada - La Florida 8988', 'Avda. La Florida 8988', -33.5349448, -70.5720425, 1, true);

INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('eugenio@medimapa.cl', '$2a$10$51smH0aSZ/6tcZusb8mrX.QCt.eEd5LvLh6e1rLaKC7JI9X3ojQ7e', 'ADMIN');

INSERT INTO usuario (correo, password_hash, rol) 
VALUES ('contacto@farmacialaflorida.cl', '$2a$10$bYiM0BijMIsbVi4DkNff7eGhGlFPRHJXYE/rH.wNuZR6B56RAwl/y', 'FARMACEUTICO');