# ADR en Bases de Datos

## ¿Qué es ADR?

**ADR** en el contexto de bases de datos puede referirse a varios conceptos, siendo los más comunes:

### 1. **Address (Dirección)**
Es la ubicación física o lógica donde se almacenan los datos en la base de datos. Cada registro o dato tiene una dirección única que permite el acceso rápido a la información.

### 2. **Atomic, Durable, Remote** (en sistemas distribuidos)
- **Atomic**: Las operaciones se ejecutan completamente o no se ejecutan.
- **Durable**: Los datos persisten incluso después de fallos.
- **Remote**: Capacidad de trabajar con datos en ubicaciones remotas.

### 3. **Principios ACID Relacionados**
ADR puede estar relacionado con los principios **ACID** que garantizan la integridad de las bases de datos:
- **Atomicidad**: Transacciones completas o nulas
- **Consistencia**: Integridad de datos
- **Aislamiento**: Independencia entre transacciones
- **Durabilidad**: Persistencia de datos

## Importancia en Bases de Datos

Los conceptos de ADR son fundamentales para:
- ✓ Garantizar la integridad de los datos
- ✓ Mejorar el rendimiento de acceso a registros
- ✓ Asegurar la confiabilidad del sistema
- ✓ Facilitar la recuperación ante fallos

## Conclusión

ADR es un concepto clave en el diseño y funcionamiento de bases de datos, asegurando que los datos sean accesibles, seguros y confiables.
