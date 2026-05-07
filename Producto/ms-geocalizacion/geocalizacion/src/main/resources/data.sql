-- ==========================================
-- 1. TERRITORIO POLÍTICO
-- ==========================================
-- Insertar Región
INSERT INTO region (id_region, nombre) VALUES (1, 'Región Metropolitana') ON CONFLICT DO NOTHING;

-- Insertar Comuna
INSERT INTO comuna (id, nombre_com, id_region) VALUES (1, 'La Florida', 1) ON CONFLICT DO NOTHING;

-- ==========================================
-- 2. VINCULACIÓN GEOGRÁFICA
-- ==========================================
-- Como el ms-catalogo insertó las sucursales sin comuna, 
-- aquí las "abrazamos" y las vinculamos a La Florida (id=1)
UPDATE sucursal_farmacia SET id_comuna = 1 WHERE id_comuna IS NULL;