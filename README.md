# Petris

Petris es un juego de mesa digital para 2 jugadores basado en el control y la expansión de bacterias sobre placas de Petri. El objetivo es gestionar inteligentemente tus bacterias y sarcinas para minimizar los puntos de contaminación y derrotar al oponente.

Este repositorio contiene el backend (Spring Boot) y el frontend (React) de la aplicación Petris. La descripción de las reglas y los requisitos funcionales se encuentra en el documento de análisis del proyecto: [docs/deliverables/D1/Análisis de requisitos del sistema.md](docs/deliverables/D1/An%C3%A1lisis%20de%20requisitos%20del%20sistema.md).

## Requisitos previos

- Java 17 o superior
- Node.js 18 o superior
- Git

## Ejecutar el backend

Hay varias maneras de ejecutar el backend Spring Boot:

- Compilar y ejecutar el jar generado:

```bash
./mvnw package
java -jar target/*.jar
```

- Ejecutar directamente con el wrapper de Maven (modo desarrollo):

```bash
./mvnw spring-boot:run
```

- En Windows usa las variantes con `.cmd` si es necesario:

```powershell
mvnw.cmd spring-boot:run
# o
mvnw.cmd package
java -jar target\*.jar
```

- Usar Docker / Docker Compose (si quieres levantarlo en contenedores):

```bash
docker-compose up --build
```

Una vez el backend esté corriendo, la API y la documentación Swagger quedan disponibles normalmente en:

`http://localhost:8080/swagger-ui/index.html`

## Ejecutar el frontend

Desde la carpeta del proyecto, entra en el frontend y arranca la app React:

```bash
cd frontend
npm install    # solo la primera vez
npm start
```

El frontend quedará disponible en `http://localhost:3000`.

## Estructura básica

- `src/main/java` - código Java del backend
- `src/main/resources` - configuración y recursos del backend
- `frontend/` - aplicación React (UI)

## Desarrollo y contribución

- Para cambios en Java, recompila con Maven o ejecuta desde tu IDE favorito (IntelliJ, Eclipse, STS).
- Para cambios en la UI, usa `npm start` dentro de `frontend/` y el servidor de desarrollo recargará automáticamente.

## Calidad y análisis estático

El proyecto dispone de un repositorio público y un análisis en SonarCloud:

- Repositorio público (fork): https://github.com/Yabarta/dp1-2025-2026-l6-3-25-public
- SonarCloud (análisis del proyecto): https://sonarcloud.io/project/overview?id=Yabarta_dp1-2025-2026-l6-3-25-public

