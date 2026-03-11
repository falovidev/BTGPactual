-- ============================================================
-- Prueba Técnica BTG Pactual - Parte 2: SQL
-- Base de datos: BTG
-- ============================================================

-- Consulta: Obtener los nombres de los clientes que tienen inscrito
-- algún producto disponible SOLO en las sucursales que visitan.
--
-- Lógica: Un producto está "disponible solo en las sucursales que visita"
-- cuando TODAS las sucursales que ofrecen ese producto son sucursales
-- que el cliente visita. Es decir, no existe ninguna sucursal que
-- ofrezca el producto y que el cliente NO visite.

SELECT DISTINCT c.nombre
FROM clientes c
INNER JOIN inscripciones i ON c.id_cliente = i.id_cliente
WHERE NOT EXISTS (
    SELECT 1
    FROM productos_sucursal ps
    WHERE ps.id_producto = i.id_producto
      AND ps.id_sucursal NOT IN (
          SELECT v.id_sucursal
          FROM visitas v
          WHERE v.id_cliente = c.id_cliente
      )
);

-- Explicación paso a paso:
-- 1. Partimos de los clientes y sus inscripciones (productos inscritos).
-- 2. Para cada combinación cliente-producto, verificamos con NOT EXISTS
--    que NO exista ninguna sucursal que ofrezca ese producto y que el
--    cliente NO visite.
-- 3. Si no existe tal sucursal, significa que el producto está disponible
--    únicamente en sucursales que el cliente sí visita.
-- 4. DISTINCT evita duplicados cuando un cliente tiene múltiples productos
--    que cumplen la condición.
