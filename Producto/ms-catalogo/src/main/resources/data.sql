-- ==========================================
-- 1. CATÁLOGO MAESTRO DE MEDICAMENTOS
-- ==========================================
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
(41, 'Viadil', 'Pargeverina', 'Nacional', true, true, 'Digestivo y gastrointestinal')
ON CONFLICT DO NOTHING;

-- ==========================================
-- SINCRONIZAR CONTADOR DE MEDICAMENTOS
-- ==========================================
SELECT setval(pg_get_serial_sequence('medicamento', 'id_medicamento'), coalesce(max(id_medicamento), 1), max(id_medicamento) IS NOT null) FROM medicamento;