# Product Requirements Document (PRD)
# Sistema de Gestión de Turnos Bancarios

### 1. Visión del Producto
Sistema modular para gestionar turnos de clientes en sucursales bancarias, utilizando arquitectura basada en eventos con Spring Boot + Spring Modulith.

### 2. Objetivos
* Generar turnos para clientes de forma ordenada
* Controlar cola de espera en tiempo real
* Asignar turnos a cajeros disponibles
* Mantener registros de auditoría completos
* Comunicar eventos entre módulos de forma desacoplada

### 3. Módulos Principales (3 módulos + listeners)

#### 3.1 Módulo `customers`
* **Responsabilidad:** Registro y gestión básica de clientes
* **Funcionalidades:**
  * Registrar cliente (documento, nombre completo)
  * Buscar cliente existente por documento
  * Crear o recuperar cliente automáticamente

#### 3.2 Módulo `turns`
* **Responsabilidad:** Gestión de turnos y cola de espera
* **Funcionalidades:**
  * Generar turnos con numeración automática
  * Mantener cola de espera por servicio
  * Llamar siguiente turno disponible
  * Completar atención de turno
  * Estados: WAITING, CALLED, IN_PROGRESS, COMPLETED, CANCELLED

#### 3.3 Módulo `services`
* **Responsabilidad:** Gestión de cajeros y servicios bancarios
* **Funcionalidades:**
  * CRUD de cajeros (nombre, tipo de servicio)
  * Control de estado de cajeros (AVAILABLE, BUSY, ON_BREAK)
  * Servicios disponibles (CAJA, EJECUTIVO)
  * Consulta de cajeros por disponibilidad

#### 3.4 Módulo `listeners`
* **Responsabilidad:** Procesamiento de eventos del sistema
* **Componentes:**
  * **NotificationEventListener:** Notificaciones SMS/Email simuladas
  * **QueueDisplayEventListener:** Actualización de pantallas de sucursal
  * **AuditEventListener:** Registros de auditoría y métricas

### 3.5 Módulo `batch`
* **Responsabilidad:** Procesamiento por lotes y tareas programadas
* **Componentes:**
  * **CleanupTasklet:** Limpieza automática de turnos antiguos
  * **BatchScheduler:** Programación de trabajos batch
  * **CleanupJobConfig:** Configuración del job de limpieza
  * **BatchController:** API REST para ejecución manual de jobs

### 4. Eventos del Sistema
* `TurnCreated` - Turno generado para cliente
* `TurnCalled` - Turno llamado por cajero
* `TurnCompleted` - Atención finalizada
* `CashierAvailable` - Cajero disponible para atender

### 5. APIs REST Implementadas

```
/api/customers
  POST   - Crear/registrar cliente
  GET    /{document} - Buscar cliente por documento

/api/turns
  POST   - Generar nuevo turno
  GET    /queue - Ver cola actual de espera
  PUT    /{id}/call - Llamar turno específico
  PUT    /{id}/complete - Completar turno

/api/services
  POST   /cashiers - Crear nuevo cajero
  GET    /cashiers - Listar todos los cajeros
  GET    /cashiers/available - Cajeros disponibles
  PUT    /cashiers/{id}/status - Cambiar estado cajero
  GET    /cashiers/{id} - Obtener cajero específico

/api/batch
  POST   /run-cleanup - Ejecutar limpieza manual de datos antiguos
```

### 6. Modelo de Datos Final

**Customer:** 
- id (Long), document (String), fullName (String)

**Turn:** 
- id (Long), customerId (Long), serviceType (CAJA/EJECUTIVO), turnNumber (String), 
- status (WAITING/CALLED/IN_PROGRESS/COMPLETED/CANCELLED), createdAt (DateTime), calledAt (DateTime)

**Cashier:** 
- id (Long), name (String), serviceType (CAJA/EJECUTIVO), status (AVAILABLE/BUSY/ON_BREAK)

### 7. Stack Tecnológico Implementado
* **Backend:** Spring Boot 3.4+, Spring Modulith 1.2+, Java 21
* **Batch Processing:** Spring Batch 5.0+ con scheduling
* **Base de Datos:** H2 (en memoria para desarrollo)
* **ORM:** JPA/Hibernate con generación automática de esquema
* **Logging:** SLF4J con formato estructurado
* **Eventos:** Spring Application Events
* **Testing:** JUnit 5, Mockito, MockMvc
* **Build:** Maven

### 8. Arquitectura de Eventos
* **Publicación:** Servicios publican eventos usando `ApplicationEventPublisher`
* **Procesamiento:** Listeners procesan eventos de forma asíncrona
* **Desacoplamiento:** Módulos se comunican únicamente por eventos
* **Auditoría:** Todos los eventos se registran automáticamente

### 9. Casos de Uso Implementados

#### 9.1 Flujo Completo de Atención
1. **Cliente solicita turno:** Se registra cliente y genera turno automáticamente
2. **Sistema notifica:** Eventos disparan notificaciones, actualizaciones de pantalla y auditoría
3. **Cajero llama turno:** Se actualiza estado y notifica al cliente
4. **Atención completada:** Se libera cajero y registra métricas

#### 9.2 Gestión de Cajeros
1. **Registro de cajero:** Alta con especialidad (CAJA/EJECUTIVO)
2. **Control de estado:** Cambios automáticos según disponibilidad
3. **Asignación inteligente:** Por tipo de servicio solicitado

### 10. Características de Producción Implementadas
* **Logging estructurado:** Todos los eventos registrados con timestamps
* **Manejo de errores:** Validaciones y respuestas HTTP apropiadas
* **Concurrencia:** Estructuras thread-safe para colas y estados
* **Métricas:** Base para integración con sistemas de monitoreo
* **API RESTful:** Documentación implícita con naming conventions
* **Batch Processing:** Limpieza automática de datos antiguos
* **Scheduled Jobs:** Ejecución programada diaria a las 2:00 AM
* **Manual Triggers:** API REST para ejecutar jobs bajo demanda
* **Testing:** Cobertura de tests +70% en código principal

### 11. Funcionalidades de los Listeners

#### NotificationEventListener
* Simulación de SMS/Email al crear turnos
* Notificaciones cuando turno es llamado
* Encuestas post-atención
* Alertas al sistema de gestión

#### QueueDisplayEventListener
* Actualización de pantallas de sucursal en tiempo real
* Estado de colas por servicio
* Información de cajeros ocupados/disponibles
* Mensajes temporales de completación

#### AuditEventListener
* Registro completo de todas las operaciones
* Métricas de tiempo de espera y atención
* Trazabilidad por actor (sistema/cajero)
* Base para reportes gerenciales

### 12. Funcionalidades del Módulo Batch

#### CleanupTasklet
* **Propósito:** Eliminar turnos completados antiguos
* **Criterio:** Turnos con más de 30 días de antigüedad
* **Ejecución:** Automática diaria a las 2:00 AM
* **Manual:** Disponible vía API REST `/api/batch/run-cleanup`
* **Logging:** Registra cantidad de turnos eliminados
* **Métricas:** Incrementa contadores de items procesados

#### Configuración del Job
* **Job Name:** cleanupJob
* **Step:** cleanupOldTurnsStep
* **Transaction Manager:** Integrado con JPA
* **Rollback:** Automático en caso de error
* **Status Tracking:** Estado persistido en tablas Spring Batch

#### BatchScheduler
* **Frecuencia:** Diaria a las 2:00 AM (configurable vía cron)
* **Job Launcher:** Ejecución con parámetros únicos (timestamp)
* **Error Handling:** Logs estructurados de errores
* **Retry Logic:** Basado en configuración de Spring Batch

### 12. Validaciones y Reglas de Negocio
* **Clientes únicos:** Documento no duplicado
* **Turnos únicos:** Numeración automática con timestamp
* **Estados coherentes:** Transiciones válidas de estados
* **Servicios válidos:** Solo CAJA y EJECUTIVO permitidos

### 13. Próximos Pasos (Extensiones Futuras)
* Base de datos persistente (MySQL/PostgreSQL)
* Interfaz web para cajeros y supervisores
* API de reportes y estadísticas
* Notificaciones push reales
* Integración con sistemas bancarios existentes
* Priorización de clientes VIP
* Estimación de tiempos de espera
* Dashboard en tiempo real

### 14. Criterios de Aceptación Cumplidos
Arquitectura modular con Spring Modulith  
Comunicación por eventos entre módulos  
APIs REST funcionales y probadas  
Base de datos con esquema automático  
Manejo de errores consistente  
Logging estructurado para auditoría  
Separación clara de responsabilidades  
Eventos procesados por múltiples listeners  
Estados de turno y cajero bien definidos  
Numeración automática de turnos