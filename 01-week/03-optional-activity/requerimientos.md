 
 ### Requerimientos funcionales (RF) H

markdown


### Requerimientos no funcionales (RNF) C

RNF-01 (Usabilidad):
El sistema debe ser fácil de usar, con una interfaz clara e intuitiva para clientes y administrador.

RNF-02 (Tiempo de respuesta):
El sistema debe responder a las acciones del usuario en un tiempo máximo de 3 segundos.

RNF-03 (Disponibilidad):
El sistema debe estar disponible durante el horario de atención de la cafetería.

RNF-04 (Seguridad):
El sistema debe permitir el acceso restringido al administrador mediante autenticación.

RNF-05 (Compatibilidad):
El sistema debe ser compatible con navegadores web modernos.

RNF-06 (Fiabilidad):
El sistema debe garantizar que los pedidos no se pierdan una vez registrados.


# Reglas de negocio (RN) N

RN-01: No se puede confirmar un pedido si el carrito está vacío.

RN-02: Todo pedido debe tener al menos un producto.

RN-03: El estado inicial de un pedido debe ser “Recibido”.

RN-04: Un pedido solo puede cambiar de estado en el siguiente orden:
Recibido → En preparación → Entregado.

RN-05: No se permite modificar un pedido una vez su estado sea Entregado.

RN-06: Solo el administrador puede cambiar el estado del pedido.
