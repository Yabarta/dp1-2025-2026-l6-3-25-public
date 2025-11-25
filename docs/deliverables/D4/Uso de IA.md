# Documentación del Uso de IA en el Proyecto
**Asignatura:** Diseño y Pruebas 1 (Grado en Ingeniería del Software, Universidad de Sevilla)       
**Curso académico:** 2025/2026      
**Grupo/Equipo:** L6-03     
**Nombre del proyecto:** Petris     
**Repositorio:** https://github.com/gii-is-DP1/dp1-2025-2026-l6-3-25        
**Integrantes (máx. 6):** 
- David Lozano Acosta
- Diego Vicente Cámara
- Ismael Barroso Delgado
- Jose Antonio Aguadero García
- Lu Dao Guerricabeitia Garzón
- Pablo Pérez Sorni

## 1. Introducción

Este documento describe el uso que se ha echo de la IA en el proyecto. El objetivo es ser transparentes sobre el uso de IA realizado. Como recordatorio, al alumnado incluimos un resumen de lo indicado en el Syllabus de la asignatura:

### Declaración de Política y Compromiso

> **Principios guía generales (resumen):**  
> - **Dimensión Cognitiva:** El trabajo con IA **no** debe reducir su capacidad de pensar con claridad; úsela para **facilitar**—no obstaculizar—el aprendizaje.  
> - **Dimensión Ética :** La utilización de IA debe ser **transparente** y **alineada** con la integridad académica.

**Normas específicas de la asignatura:**
- ✅ **IA para código:** Se permite usar tecnología generativa para **completar o generar ejemplos de código** en las tareas, pero **debe citarse explícitamente** la procedencia del mismo. Así mismo el alumno debe **entender** y poder **modificar bajo demanda** cualquier código entregado, siendo el responsable de cualquier comportamiento del código que ha conmitado, ante el profesor y sus compañeros. Recuerde: **Usted es responsable** de dicho código.
- ❌ **IA para narrativa:** Salvo indicación en contrario, **no** se permite usar IA generativa para **redactar narrativa** de las entregas. Se puede usar como **recurso** durante el proceso, **no** para **responder por usted** a los ejercicios.

**Marco ético US:** Consulte y cumpla lo indicado en **Guías de Ética e IA** de la US: https://guiasbus.us.es/ia/etica

**Rellenar este documento es Obligatorio:** La **documentación del uso de IA** es un **entregable** del proyecto.

## Resumen por Sprint (1–4)
### Sprint 1 — Resumen de uso de IA

Usos registrados: <!-- nº -->

Ámbitos principales: <!-- p.ej., generación de pruebas, esqueletos de código, análisis y resolución de errores --> generación de estilos

Valor aportado: <!-- síntesis -->

Riesgos relevantes y mitigaciones: <!-- síntesis -->

Lecciones aprendidas: <!-- síntesis -->

Checklist de cumplimiento de uso ético de la IA del sprint X:

- [ ] Toda interacción significativa está en el Registro Detallado con enlace a conversación.

- [ ] No se usó IA para narrativa (o hay autorización documentada).

- [ ] Toda pieza aceptada fue comprendida y verificada por humanos (tests/revisión).

- [ ] Citas/Atribuciones incluidas cuando corresponde.

- [ ] Se usó la IA sin dar datos personales/sensibles que puedieran quedar expuestos a herramientas externas.

Repita esta subsección para Sprints 2, 3 y 4.

## Registro detallado de uso de AI por Sprint

**Use una fila por “uso realmente significativo”** (idea sugerida por la IA, trozo de código importante modificado, depuración de error que no eras capaz de resolver por tu cuenta, generación de pruebas para el código de producción, etc.). No incluya filas para detalles nímios como el autocompletado de variables o signaturas de métodos, o la generación de código simple (recorridos y procesamiento de estructuras de datos, formateo  y/o creación de estilos CSS, etc.).

### Sprint 1 registro detallado de uso de IA por sprint

| # | Fecha y hora | Sprint | Integrante(s) | **Herramienta & versión** | **Acceso** | **Enlace a conversación / Prompt** | **Finalidad** | **Artefactos afectados** | **Verificación humana** | **Riesgos & mitigaciones** | **Resultado** |
|---:|--------------|:-----:|---------------|----------------------------|------------|------------------------------------|---------------|---------------------------|--------------------------|-----------------------------|---------------|
| 1.1 | <!-- 04/09/2025 18:40 --> | 1 | <!-- Nombre --> | <!-- p.ej., ChatGPT (GPT-5, OpenAI, 2025) --> | <!-- web/plugin/integración --> | <!-- URL al chat o prompt resumido --> | <!-- idea / código / depuración / pruebas / documentación técnica* --> | <!-- ficheros, issue, PR, commit --> | <!-- pruebas, revisión por pares, reasoning propio --> | <!-- plagio, licencias, datos personales; mitigación --> | <!-- aceptado / rechazado / aceptado con cambios parciales --> |


### Sprint 2

| # | Fecha y hora | Sprint | Integrante(s) | **Herramienta & versión** | **Acceso** | **Enlace a conversación / Prompt** | **Finalidad** | **Artefactos afectados** | **Verificación humana** | **Riesgos & modificaciones** | **Resultado** |
|---:|--------------|:-----:|---------------|----------------------------|------------|------------------------------------|---------------|---------------------------|--------------------------|-----------------------------|---------------|
| 2.1 | <!-- 04/09/2025 18:40 --> | 2 | <!-- Nombre --> | <!-- p.ej., ChatGPT (GPT-5, OpenAI, 2025) --> | <!-- web/plugin/integración --> | <!-- URL al chat o prompt resumido --> | <!-- idea / código / depuración / pruebas / documentación técnica* --> | <!-- ficheros, issue, PR, commit --> | <!-- pruebas, revisión por pares, reasoning propio --> | <!-- plagio, licencias, datos personales; mitigación --> | <!-- aceptado / rechazado / aceptado con cambios parciales --> |

### Sprint 3

| # | Fecha y hora | Sprint | Integrante(s) | **Herramienta & versión** | **Acceso** | **Enlace a conversación / Prompt** | **Finalidad** | **Artefactos afectados** | **Verificación humana** | **Riesgos & mitigaciones** | **Resultado** |
|---:|--------------|:-----:|---------------|----------------------------|------------|------------------------------------|---------------|---------------------------|--------------------------|-----------------------------|---------------|
| 3.1 | <!--20/11/2025 17:10 --> | 3 | <!-- Diego Vicente Cámara --> | <!-- GitHub Copilot Chat (GPT-5.1-Codex Preview) --> | <!-- VS Code --> | <!-- Prompt “Me gustaría que implementaras todas las funcionalidades para que puedan jugarse los turnos sin problemas” --> | <!-- Generación/ajuste de codigo backend + UI para, en base a las reglas del juego definidas, poder completar una partida con sus 40 turnos máximos  --> | <!-- src/main/java/.../MatchService.java, MatchServiceHelper.java, frontend/src/Game/gameScreen.js --> | <!-- comprobé el codigo generado dandome cuenta de el problema en lo que yo había hecho era la funcion de obtener al ganador para el caso de que no existieran movimientos legales, lo cual solucionó además de añadir las condiciones descritas en las reglas del juego--> | <!--  Posible desviación respecto a reglas del juego → contrastado con “Análisis de requisitos del sistema.md” y edición de ciertos condicionales creados --> | <!--aceptado con cambios parciales --> |

### Sprint 4

| # | Fecha y hora | Sprint | Integrante(s) | **Herramienta & versión** | **Acceso** | **Enlace a conversación / Prompt** | **Finalidad** | **Artefactos afectados** | **Verificación humana** | **Riesgos & mitigaciones** | **Resultado** |
|---:|--------------|:-----:|---------------|----------------------------|------------|------------------------------------|---------------|---------------------------|--------------------------|-----------------------------|---------------|
| 4.1 | <!-- 04/09/2025 18:40 --> | 4 | <!-- Nombre --> | <!-- p.ej., ChatGPT (GPT-5, OpenAI, 2025) --> | <!-- web/plugin/integración --> | <!-- URL al chat o prompt resumido --> | <!-- idea / código / depuración / pruebas / documentación técnica* --> | <!-- ficheros, issue, PR, commit --> | <!-- pruebas, revisión por pares, reasoning propio --> | <!-- plagio, licencias, datos personales; mitigación --> | <!-- aceptado / rechazado / aceptado con cambios parciales --> |

## Conclusiones finales sobre el uso de la IA en el proyecto
Aqui debéis reflexionar sobre el papel que ha tenido la IA en la realización de vuestro proyecto y las maneras que consideráis que son más adecuadas para su uso en este contexto. Si tenéis alguna curiosida o caso que sea reseñable y pueda ser útil que los profesor conozcamos de cara a orientar a otros compañeros a este respecto podéis incluirlas aquí también.

## Anexo A) Inventario de Herramientas de IA
|Herramienta|Versión/Modelo|Proveedor|Acceso (web/plugin/API)| Licencia/Plan | Observaciones|
|-----------|--------------|---------|-----------------------|---------------|--------------|
|<!-- p.ej., ChatGPT (GPT-5) -->|<!-- 5 (2025)-->|<!-- OpenAI -->|<!-- web -->|<!-- pro -->|<!-- ... -->|

## Anexo B) Glosario de Finalidades

Idea/Exploración · Generación de Código funcional · Depuración / Diagnóstico · Generación de pruebas (unitarias/integración/e2e) · Diseño técnico · Documentación técnica (no narrativa) ·  Refactorización.
