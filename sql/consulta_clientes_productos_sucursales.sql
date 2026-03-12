-- ============================================================
-- BTG Pactual - Prueba Técnica Parte 2 (SQL 20%)
-- ============================================================
-- Base de datos: BTG
--
-- Consulta solicitada:
-- Obtener los nombres de los clientes que tienen inscrito
-- algún producto disponible solo en las sucursales que visitan.
-- ============================================================

-- Tablas del modelo (según diagrama ER):
--   cliente(id, nombre, apellidos, ciudad)
--   producto(id, nombre, tipoProducto)
--   sucursal(id, nombre, ciudad)
--   inscripcion(idProducto PK/FK, idCliente PK/FK)
--   disponibilidad(idSucursal PK/FK, idProducto PK/FK)
--   visitan(idSucursal PK/FK, idCliente PK/FK, fechaVisita)

SELECT DISTINCT c.nombre
FROM cliente c
-- Productos inscritos por el cliente
INNER JOIN inscripcion i ON c.id = i.idCliente
-- Verificamos que el producto esté disponible SOLO en sucursales que el cliente visita.
-- Esto significa: no existe ninguna sucursal que ofrezca ese producto y que el cliente NO visite.
WHERE NOT EXISTS (
    -- Sucursales donde está disponible el producto inscrito
    SELECT 1
    FROM disponibilidad d
    WHERE d.idProducto = i.idProducto
      AND d.idSucursal NOT IN (
          -- Sucursales que visita el cliente
          SELECT v.idSucursal
          FROM visitan v
          WHERE v.idCliente = c.id
      )
)
-- Además, el producto debe estar disponible en al menos una sucursal
AND EXISTS (
    SELECT 1
    FROM disponibilidad d2
    WHERE d2.idProducto = i.idProducto
);
