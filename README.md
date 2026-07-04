# 🎓 TutorMatch - Backend Ecosystem

**TutorMatch** es una plataforma web estratégica diseñada para facilitar la conexión entre alumnos universitarios que requieren apoyo académico y compañeros capacitados dispuestos a impartir tutorías. 

Este repositorio contiene el código fuente del **Backend**, estructurado como un **Monorepo de Microservicios** utilizando el ecosistema de Spring Boot y desplegado sobre infraestructura en la nube.

---

## Arquitectura del Sistema

El proyecto implementa un patrón de microservicios con seguridad perimetral delegada, asegurando alta disponibilidad, tolerancia a fallos y despliegues independientes.

* **Service Discovery:** Netflix Eureka (Enrutamiento interno).
* **API Gateway:** Spring Cloud Gateway (Punto de entrada único).
* **Identity Provider (Auth Server):** Spring Authorization Server (OAuth2 + PKCE + JWT).
* **Microservicios de Negocio:** Spring Boot (MVC Desacoplado).
* **Persistencia:** Supabase (PostgreSQL) aplicando el patrón *Database per Service* mediante esquemas lógicos.

---

## Estructura del Monorepo (Multi-módulo)

El proyecto está diseñado como un proyecto Maven Multi-módulo. La estructura de directorios refleja la separación de responsabilidades de la arquitectura:

```text
tutormatch-backend/
 │
 ├── pom.xml                # POM Padre: Gestión centralizada de versiones y dependencias.
 │
 ├── eureka-server/         # Servidor de descubrimiento de microservicios.
 │
 ├── api-gateway/           # Enrutador y Resource Server.
 │
 ├── auth/                  # Servidor de Identidad (Login, emisión y validación de JWT).
 │
 ├── ms-usuarios/           # Gestión de perfiles y registro.
 │
 ├── ms-core/               # Lógica de sesiones de tutoría e inscripciones.
 │
 ├── ms-evaluaciones/       # Sistema de calificaciones por estrellas.
 │
 └── ms-notificaciones/     # Motor asíncrono de correos electrónicos.
```

---

## Guía de Inicio Rápido (Desarrollo Local)

Para ejecutar este ecosistema en un entorno local, el equipo debe asegurar el siguiente orden de arranque:

- Clonar el repositorio con git clone

- Configurar las variables de entorno locales (Credenciales de Supabase y llaves).

- Levantar eureka-server (Puerto 8761).

- Levantar auth (Puerto 8081).

- Levantar los microservicios de negocio (ms-usuarios, ms-core, etc.).

- Levantar api-gateway (Puerto 8080).

_Nota: La aplicación cliente (SPA en Angular) correrá de manera independiente en el puerto 4200 y solo se comunicará con el puerto 8080 del Gateway._