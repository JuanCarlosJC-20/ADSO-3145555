# Módulo 10 - Coordinación y Eventos

## Dependencias del módulo

Este módulo consume información de los siguientes módulos:

- 3. Infraestructura
- 4. Parametrización
- 6. Oferta y Programas
- 7. Actores
- 8. Horarios
- 9. Proyectos Formativos

---

# Entidades Propias del Módulo

## ActividadAcademica

Representa cualquier actividad académica especial gestionada por el módulo.

Ejemplos:

- Sustentación
- Evaluación
- Comité
- Evento
- Charla
- Revisión de proyecto

```text
ActividadAcademica
-------------------------
PK Id

FK TipoActividadId
FK ProyectoFormativoId
FK FichaId

Nombre
Descripcion

Fecha
HoraInicio
HoraFin

Estado

CreadoPor
FechaCreacion
FechaActualizacion
```

### Relaciones

```text
TipoActividadId -> Parametrización.TipoActividad

ProyectoFormativoId -> ProyectosFormativos.ProyectoFormativo

FichaId -> OfertaYProgramas.Ficha
```

---

## ReservaAmbiente

Permite reservar espacios físicos para actividades académicas.

```text
ReservaAmbiente
-------------------------
PK Id

FK ActividadAcademicaId
FK AmbienteId

Fecha
HoraInicio
HoraFin

Estado

FechaReserva
```

### Relaciones

```text
ActividadAcademicaId -> ActividadAcademica.Id

AmbienteId -> Infraestructura.Ambiente
```

---

## AsignacionEvaluador

Relaciona instructores evaluadores con actividades académicas.

```text
AsignacionEvaluador
-------------------------
PK Id

FK ActividadAcademicaId
FK InstructorId

Rol

Estado

FechaAsignacion
```

### Relaciones

```text
ActividadAcademicaId -> ActividadAcademica.Id

InstructorId -> Actores.Instructor
```

---

## SeguimientoActividad

Permite registrar observaciones y cambios realizados durante el ciclo de vida de una actividad.

```text
SeguimientoActividad
-------------------------
PK Id

FK ActividadAcademicaId
FK UsuarioRegistroId

Fecha

Observacion

EstadoAnterior
EstadoNuevo
```

### Relaciones

```text
ActividadAcademicaId -> ActividadAcademica.Id

UsuarioRegistroId -> Actores.Actor
```

---

## ParticipanteActividad

Permite registrar todos los participantes asociados a una actividad.

```text
ParticipanteActividad
-------------------------
PK Id

FK ActividadAcademicaId
FK ActorId
FK TipoParticipacionId

Asistencia

Observacion
```

### Relaciones

```text
ActividadAcademicaId -> ActividadAcademica.Id

ActorId -> Actores.Actor

TipoParticipacionId -> Parametrizacion.TipoParticipacion
```

### Ejemplos de participantes

```text
Aprendiz
Instructor
Jurado
Invitado
Directivo
```

---

## ReprogramacionActividad

Permite mantener historial de cambios de fechas y horarios.

```text
ReprogramacionActividad
-------------------------
PK Id

FK ActividadAcademicaId
FK UsuarioResponsableId

FechaAnterior

HoraInicioAnterior
HoraFinAnterior

FechaNueva

HoraInicioNueva
HoraFinNueva

Motivo

FechaRegistro
```

### Relaciones

```text
ActividadAcademicaId -> ActividadAcademica.Id

UsuarioResponsableId -> Actores.Actor
```

---

# Entidades Consumidas desde Otros Módulos

## Parametrización

```text
TipoActividad
--------------
PK Id

Nombre
Descripcion
```

Ejemplos:

```text
Sustentación
Evaluación
Comité
Evento
Charla
```

---

```text
TipoParticipacion
-----------------
PK Id

Nombre
Descripcion
```

Ejemplos:

```text
Aprendiz
Instructor
Jurado
Invitado
```

---

## Infraestructura

```text
Ambiente
--------------
PK Id

Nombre
Tipo

Capacidad

Estado
```

Ejemplos:

```text
Laboratorio
Auditorio
Sala de reuniones
Aula
```

---

## Actores

```text
Actor
--------------
PK Id

Nombre
Correo
Estado
```

Especializaciones:

```text
Instructor

Aprendiz

Coordinador

Directivo
```

---

## Oferta y Programas

```text
Ficha
--------------
PK Id

Codigo

Nombre

Estado
```

---

## Proyectos Formativos

```text
ProyectoFormativo
----------------------
PK Id

Nombre

Descripcion

Estado
```

---

## Horarios

```text
Horario
--------------
PK Id

Fecha

HoraInicio
HoraFin

Estado
```

Se utiliza únicamente para validación de conflictos.

---

# Modelo Relacional Simplificado

```text
                    PARAMETRIZACIÓN
                    ───────────────

        TipoActividad      TipoParticipacion
                │                 │
                │                 │
                ▼                 ▼

               ActividadAcademica
                        │
         ┌──────────────┼──────────────┐
         │              │              │
         ▼              ▼              ▼

ReservaAmbiente  AsignacionEvaluador  ParticipanteActividad
         │              │              │
         │              │              │
         ▼              ▼              ▼

      Ambiente      Instructor       Actor

         ▲
         │
 Infraestructura


                ActividadAcademica
                        │
         ┌──────────────┴──────────────┐
         │                             │
         ▼                             ▼

SeguimientoActividad     ReprogramacionActividad

                        ▲
                        │

                      Actor


ActividadAcademica
        │
        ├────────► Ficha
        │
        └────────► ProyectoFormativo
```

# MVP Recomendado

Para una primera versión funcional implementar:

```text
ActividadAcademica

ReservaAmbiente

AsignacionEvaluador

SeguimientoActividad

ParticipanteActividad

ReprogramacionActividad
```

Estas entidades cubren:

- Programación de actividades.
- Asignación de ambientes.
- Asignación de evaluadores.
- Participantes.
- Seguimiento.
- Historial de cambios.
- Validación de conflictos.