-- ============================================================
-- BTG Pactual - Prueba Técnica Parte 2 (SQL 20%)
-- ============================================================
-- Base de datos: BTG
--
-- Consulta solicitada:
-- Obtener los nombres de los clientes que tienen inscrito
-- algún producto disponible solo en las sucursales que visitan.
-- ============================================================

-- Se asumen las siguientes tablas:
--   CLIENTES(id_cliente, nombre, ...)
--   SUCURSALES(id_sucursal, nombre, ...)
--   PRODUCTOS(id_producto, nombre, ...)
--   VISITAS(id_cliente, id_sucursal)           -- sucursales que visita cada cliente
--   PRODUCTOS_SUCURSAL(id_sucursal, id_producto)  -- productos disponibles por sucursal
--   INSCRIPCIONES(id_cliente, id_producto)      -- productos inscritos por el cliente

SELECT DISTINCT c.nombre
FROM CLIENTES c
-- Productos inscritos por el cliente
INNER JOIN INSCRIPCIONES i ON c.id_cliente = i.id_cliente
-- Verificamos que el producto esté disponible SOLO en sucursales que el cliente visita.
-- Esto significa: no existe ninguna sucursal que ofrezca ese producto y que el cliente NO visite.
WHERE NOT EXISTS (
    -- Sucursales donde está disponible el producto inscrito
    SELECT 1
    FROM PRODUCTOS_SUCURSAL ps
    WHERE ps.id_producto = i.id_producto
      AND ps.id_sucursal NOT IN (
          -- Sucursales que visita el cliente
          SELECT v.id_sucursal
          FROM VISITAS v
          WHERE v.id_cliente = c.id_cliente
      )
)
-- Además, el producto debe estar disponible en al menos una sucursal
AND EXISTS (
    SELECT 1
    FROM PRODUCTOS_SUCURSAL ps2
    WHERE ps2.id_producto = i.id_producto
);
