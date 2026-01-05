# Plan de Pruebas
**Asignatura:** Diseño y Pruebas 1 (Grado en Ingeniería del Software, Universidad de Sevilla)   
**Curso académico:** 2025/2026  
**Grupo/Equipo:** L6-03     
**Nombre del proyecto:** Petris  
**Repositorio:** https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25  
**Integrantes (máx. 6):** 
- David Lozano Acosta
- Diego Vicente Cámara
- José Ismael Barroso Delgado
- Jose Antonio Aguadero García
- Lu Dao Guerricabeitia Garzón
- Pablo Pérez Sorni

## 1. Introducción

Este documento describe el plan de pruebas para el proyecto **Petris** desarrollado en el marco de la asignatura **Diseño y Pruebas 1** por el grupo **L6-03**. El objetivo del plan de pruebas es garantizar que el software desarrollado cumple con los requisitos especificados en las historias de usuario y que se han realizado las pruebas necesarias para validar su funcionamiento.

## 2. Alcance

El alcance de este plan de pruebas incluye:

- Pruebas unitarias.
  - Pruebas unitarias de backend incluyendo pruebas servicios o repositorios
  - Pruebas unitarias de frontend: pruebas de las funciones javascript creadas en frontend.
  - Pruebas unitarias de interfaz de usuario. Usan la interfaz de usuario de nuestros componentes frontend.
- Pruebas de integración.  En nuestro caso principalmente son pruebas de controladores que también se ejecutarán mediante JUnit.

## 3. Estrategia de Pruebas

### 3.1 Tipos de Pruebas

#### 3.1.1 Pruebas Unitarias
Las pruebas unitarias se realizarán para verificar el correcto funcionamiento de los componentes individuales del software. Se utilizarán herramientas de automatización de pruebas como **JUnit** en backend y jest en frontend.

#### 3.1.2 Pruebas de Integración
Las pruebas de integración se enfocarán en evaluar la interacción entre los distintos módulos o componentes del sistema, nosotros las realizaremos a nivel de API, probando nuestros controladores Spring.

## 4. Herramientas y Entorno de Pruebas

### 4.1 Herramientas
- **Maven**: Gestión de dependencias y ejecución de las pruebas.
- **JUnit**: Framework de pruebas unitarias.
- **Jacoco**: Generación de informes de cobertura de código. Si se ejecuta el comando de maven install, se copiará el informe de cobertura a la subcarpeta del repositorio /docs/deliverables/D3/coverage (puede visualizarse pulsando en el fichero index.html de dicho directorio).
- **Allure**: Generación de informes de estado de las últimas ejecuciones de las pruebas. Permite agrupar las pruebas por módulo/épica y feature. Si se ejecuta el comando de maven install, se copiará el informe de estado a la subcarpeta del repositorio /docs/deliverables/D3/status (puede visualizarse pulsando en el fichero index.html de dicho directorio).
- **Jest**: Framework para pruebas unitarias en javascript.
- **React-test**: Librería para la creación de pruebas unitarias de componentes React.

### 4.2 Entorno de Pruebas
Las pruebas se ejecutarán en el entorno de desarrollo y, eventualmente, en el entorno de pruebas del servidor de integración continua.

## 5. Planificación de Pruebas
### 5.1 Estado y trazadibilidad de Pruebas por Módulo y Épica

El informe de estado de las pruebas (con trazabilidad de éstas hacia los módulos y las épicas/historias de usaurio) se encuentra [aquí](
https://gii-is-dp1.github.io/group-project-seed/deliverables/D3/status/#behaviors).

### 5.2 Cobertura de Pruebas

El informe de cobertura de pruebas se puede consultar [aquí](
https://gii-is-dp1.github.io/group-project-seed/deliverables/D3/coverage/).



*Nota importante para el alumno*: A la hora de entregar el proyecto, debes modificar la url para que esté asociada al respositorio concreto de tu proyecto. Date cuenta de que ahora mismo apunta al repositorio _gii-is-DP1/group-project-seed_.


| Historia de Usuario | Prueba | Descripción | Estado |Tipo |
|---------------------|--------|-------------|--------|--------|
| HU-01: Iniciar sesión | [UTB-1:TestLogin](https://github.com//gii-is-DP1/group-project-seed/blob/main/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/auth/AuthControllerTest.java) | Verifica que un usuario puede iniciar sesión con credenciales válidas. | Implementada | Unitaria en backend de controlador aislaada |
| HU-02: Registrar usuario | [UTB-2:TestRegister](https://github.com//gii-is-DP1/group-project-seed/blob/main/src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/auth/AuthServiceTest.java) | Verifica que un nuevo usuario puede registrarse en el sistema. | Implementada |Unitaria en backend a nivel de Servicio, prueba social incluyendo a la BD y los repositorios. |
| **HU-17: Registro de usuario (usuario)** | [UTB17-shouldSave](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica que se **guarda** correctamente un nuevo jugador. | Implementada | Unitaria en backend a nivel de Servicio, transaccional |
| **HU-17: Registro de usuario (usuario)** | [UTW31 - testCreatePlayer](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **POST /players** con datos válidos (status 201 Created). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-20: Editar perfil (jugador)** | [UTW31 - testUpdatePlayer](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **PUT /players/{id}** con datos válidos (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-20: Editar perfil (jugador)** | [UTW31 - testUpdatePlayerWithImage](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **PUT /players/{id}** con datos válidos (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-20: Editar perfil (jugador)** | [UTW31 - testUpdatePlayer_NotFound](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el manejo del endpoint **PUT /players/{id}** cuando el ID es incorrecto (status 404 Not Found). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-23: Listado de usuarios (administrador)** | [UTB23-shouldGetAllPlayers](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica la obtención de **todos** los jugadores. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-23: Listado de usuarios (administrador)** | [UTW23-shouldGetAllPlayers](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el endpoint **GET /players** para obtener la lista completa (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-25: Eliminar usuario (administrador)** | [UTB-31:shouldDelete](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/AchievementServiceTest.java) | Verifica que se **elimina** un jugador existente correctamente. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-25: Eliminar usuario (administrador)** | [UTW-31:testDeletePlayer](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **DELETE /players/{id}** para un ID válido (status 204 No Content). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-27: Ver estadísticas personales (jugador)** | [UTW27-testGetPlayerStatsById](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el endpoint **GET /players/{id}/statistics** para un ID existente (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-27: Ver estadísticas personales (jugador)** | [UTW27-testGetPlayerSpecificStatById](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el endpoint **GET /players/{id}/statistics/{statId}** para unos IDs existente (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-27: Ver estadísticas personales (jugador)** | [UTW27-testGetPlayerSpecificStatById_NotFound](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica que se lance `ResourceNotFoundException` al buscar un **ID de estadística inexistente**. | Integración en backend (Controller/MockMvc) |
| **HU-27: Ver estadísticas personales (jugador)** | [UTW31 - testUpdatePlayerStat](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **PUT /players/{id}/statistics/{statId}** con datos válidos (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-27: Ver estadísticas personales (jugador)** | [UTW31 - testUpdatePlayerStat_NotFound](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el manejo del endpoint **PUT /players/{id}/statistics/{statId}** cuando el ID de la estadística es incorrecto (status 404 Not Found). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-28: Ver logros (jugador)** | [UTB-28:testGetAllAchievements](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/AchievementServiceTest.java) | Verifica la obtención de **todos** los logros existentes. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-28: Ver logros (jugador)** | [UTB-28:testGetAchievementByCorrectId](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/AchievementServiceTest.java) | Verifica la obtención de un logro por **ID válido**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-28: Ver logros (jugador)** | [UTB-28:testGetAchievementByWrongId](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/AchievementServiceTest.java) | Verifica que se lance `ResourceNotFoundException` al buscar un **ID inexistente**. | Implementada | Unitaria en backend a nivel de Servicio (Caso negativo) |
| **HU-28: Ver logros (jugador)** | [UTB-28:testGetAchievementByName](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/AchievementServiceTest.java) | Verifica la obtención de un logro por **nombre válido**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-28: Ver logros (jugador)** | [UTB-28:testGetAchievementWrongByName](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/AchievementServiceTest.java) | Verifica que se lance `ResourceNotFoundException` al buscar un **nombre inexistente**. | Implementada | Unitaria en backend a nivel de Servicio (Caso negativo) |
| **HU-28: Ver logros (jugador)** | [UTW-28:getAllAchievements](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **GET /achievements** para obtener la lista completa (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-28: Ver logros (jugador)** | [UTW-28:getAchievementById\_ExistingId](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **GET /achievements/{id}** para un ID existente (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-28: Ver logros (jugador)** | [UTW-28:getAchievementById\_NotExistingId](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el manejo del endpoint **GET /achievements/{id}** para un ID inexistente (status 404 Not Found). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-28: Ver logros (jugador)** | [UTW28-testGetPlayerAchievementById](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el endpoint **GET /players/{id}/achievements** para un ID existente (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-28: Ver logros (jugador)** | [UTW27-testGetPlayerAchievementById_NoAchievements](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica que se devuelva una lista vacía al buscar un jugador **sin logros**. | Integración en backend (Controller/MockMvc) |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTB29-shouldGetPlayerById](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica la obtención de un jugador por **ID válido**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTW29-testGetPlayerById](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el endpoint **GET /players/{id}** para un ID existente (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTW29-testGetPlayerById_NotFound](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el manejo del endpoint **GET /players/{id}** para un ID inexistente (status 404 Not Found). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTB29-shouldNotGetPlayerByIncorrectId](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica que se lance `ResourceNotFoundException` al buscar un **ID inexistente**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTB29-shouldGetPlayerByNickname](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica la obtención de un jugador por **nickname válido**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTB29-shouldNotGetPlayerByIncorrectNickname](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica que se lance `ResourceNotFoundException` al buscar un **nickname inexistente**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTW28-testGetPlayerByNickname](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el endpoint **GET /players/nickname/{nickname}** para un nickname existente (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTW29-testGetPlayerByNickname_NotFound](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el manejo del endpoint **GET /players/nickname/{nickname}** para un nickname inexistente (status 404 Not Found). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTB29-shouldGetPlayerByUsername](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica la obtención de un jugador por **username válido**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTB29-shouldNotGetPlayerByIncorrectUsername](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica que se lance `ResourceNotFoundException` al buscar un **username inexistente**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTW28-testGetPlayerByUsername](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el endpoint **GET /players/user/{user}** para un username existente (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTW29-testGetPlayerByUsername_NotFound](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/PlayerControllerTests.java) | Verifica el manejo del endpoint **GET /players/user/{user}** para un username inexistente (status 404 Not Found). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTB-shouldGetPlayerByUser](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica la obtención de un jugador por **user válido**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-29: Ver perfil de otro jugador (jugador)** | [UTB-shouldNotGetPlayerWithIncorrectUser](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/PlayerServiceTests.java) | Verifica que se lance `ResourceNotFoundException` al buscar un **user inexistente**. | Implementada | Unitaria en backend a nivel de Servicio |
| **HU-31: Definir nuevos logros (admin)** | [UTB-31:testSaveAchievement](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/AchievementServiceTest.java) | Verifica que se **guarda** correctamente un nuevo logro. | Implementada | Unitaria en backend a nivel de Servicio, transaccional |
| **HU-31: Definir nuevos logros (admin)** | [UTB-31:testDeleteAchievement](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/AchievementServiceTest.java) | Verifica que se **elimina** un logro existente correctamente. | Implementada | Unitaria en backend a nivel de Servicio, transaccional |
| **HU-31: Definir nuevos logros (admin)** | [UTB-31:testDeleteAchievementNotFound](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/service/AchievementServiceTest.java) | Verifica el manejo de errores al intentar **eliminar** un logro inexistente. | Implementada | Unitaria en backend a nivel de Servicio (Caso negativo) |
| **HU-31: Definir nuevos logros (admin)** | [UTW-31:saveAchievement\_ValidDataSubmitted](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **POST /achievements** con datos válidos (status 201 Created). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-31: Definir nuevos logros (admin)** | [UTW-31:updateAchievement\_ValidDataSubmitted](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **PUT /achievements/{id}** con datos válidos (status 200 OK). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-31: Definir nuevos logros (admin)** | [UTW-31:updateAchievement\_WrongId](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el manejo del endpoint **PUT /achievements/{id}** cuando el ID es incorrecto (status 404 Not Found). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-31: Definir nuevos logros (admin)** | [UTW-31:deleteAchievement\_ValidId](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el endpoint **DELETE /achievements/{id}** para un ID válido (status 204 No Content). | Implementada | Integración en backend (Controller/MockMvc) |
| **HU-31: Definir nuevos logros (admin)** | [UTW-31:deleteAchievement\_WrongId](https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25/blob/main/src/test/java/es/us/dp1/l6_3_24_25/Petris/player/controller/AchievementControllerTest.java) | Verifica el manejo del endpoint **DELETE /achievements/{id}** cuando el ID es incorrecto (status 404 Not Found). | Implementada | Integración en backend (Controller/MockMvc) |


## 6. Criterios de Aceptación

- Todas las pruebas unitarias deben pasar con éxito antes de la entrega final del proyecto.
- La cobertura de código debe ser al menos del 70%.
- No debe haber fallos críticos en las pruebas de integración y en la funcionalidad.

## 7. Conclusión

Este plan de pruebas establece la estructura y los criterios para asegurar la calidad del software desarrollado. Es responsabilidad del equipo de desarrollo y pruebas seguir este plan para garantizar la entrega de un producto funcional y libre de errores.
