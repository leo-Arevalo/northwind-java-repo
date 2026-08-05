# Northwind Java API

API REST desarrollada con **Spring Boot 3.2** y **Java 21**, basada en el clásico esquema de datos **Northwind**. Incluye gestión de clientes, productos, proveedores, pedidos de compra y venta, un módulo de analítica con KPIs, y un frontend propio en JavaScript vanilla que consume la API.

Proyecto desarrollado como práctica personal para profundizar en arquitectura por capas, seguridad con JWT (incluyendo refresh tokens y roles), mapeo de DTOs y testing en un stack Java moderno.

> 🚧 **Deploy**: en progreso — próximamente el link a la demo en vivo.

## ✨ Características

- 🔐 Autenticación con **JWT** + **refresh tokens** y control de acceso por **roles/privilegios**
- 🗄️ Persistencia con **Spring Data JPA** / **Hibernate**
- 🔍 Filtros y búsquedas avanzadas con **JPA Specifications** (proveedores, órdenes de compra)
- 📄 Paginación y filtrado en endpoints de listado
- 🔄 Mapeo Entity ↔ DTO con **MapStruct**
- 📉 Reducción de boilerplate con **Lombok**
- ⚠️ Manejo centralizado de errores por capas (`GlobalExceptionHandler` + excepciones custom)
- 📊 Módulo de **analítica** con KPIs y datos para gráficos
- 🖥️ **Frontend propio** en HTML/CSS/JS vanilla, consumiendo la API
- ✅ Tests de controllers y services con **JUnit** y **Mockito**

## 🛠️ Stack tecnológico

**Backend**

| Categoría          | Tecnología                          |
|--------------------|--------------------------------------|
| Lenguaje           | Java 21                              |
| Framework          | Spring Boot 3.2.2                    |
| Seguridad          | Spring Security, JWT (JJWT 0.12.6)   |
| Persistencia       | Spring Data JPA, Hibernate 6         |
| Base de datos      | MySQL (Docker)                       |
| Mapeo DTO          | MapStruct 1.5.5                      |
| Boilerplate        | Lombok                               |
| Testing            | JUnit 5, Mockito                     |
| Build              | Maven                                |

**Frontend**

| Categoría          | Tecnología                          |
|--------------------|--------------------------------------|
| Estructura         | HTML5                                |
| Estilos            | CSS3                                 |
| Lógica             | JavaScript (vanilla, sin frameworks) |

## 📋 Requisitos previos

- Java 21 (JDK)
- Maven 3.9+ (o usar el wrapper `./mvnw` incluido)
- Docker (para levantar MySQL)
- Un navegador para el frontend

## ⚙️ Configuración

1. Cloná el repositorio:

   ```bash
   git clone https://github.com/tu-usuario/northwind-java-repo.git
   cd northwind-java
   ```

2. Levantá MySQL con Docker. Usá el esquema incluido en `docker-northwind.sql` para crear la estructura de base de datos.

3. Creá tu archivo de configuración local a partir del ejemplo (próximamente `application.properties.example`) y completá:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/northwind
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_password

   jwt.secret=tu_secreto_jwt
   jwt.expiration=86400000
   ```

   > ⚠️ El archivo real (`application.properties` / `application.yml`) está en `.gitignore` y no se sube al repositorio por seguridad.

## ▶️ Cómo correr el backend

```bash
./mvnw clean install
./mvnw spring-boot:run
```

La API levanta por defecto en `http://localhost:8080`.

## 🖥️ Cómo correr el frontend

El frontend está en la carpeta `Frontend/` y es HTML/CSS/JS puro. Podés abrir `Frontend/index.html` directamente en el navegador, o servirlo con una extensión tipo Live Server para evitar problemas de CORS/rutas relativas. Asegurate de que el backend esté corriendo en `http://localhost:8080` para que las llamadas a la API funcionen.

## 📂 Estructura del proyecto

```
northwind-java/
├── Frontend/                  # Cliente HTML/CSS/JS vanilla
│   ├── css/
│   ├── js/
│   │   ├── features/           # Componentes por feature (ej. suppliers)
│   │   ├── services/            # Clientes de la API (apiClient, authService, etc.)
│   │   └── views/
│   └── views/                   # Páginas HTML (login, customers, suppliers, etc.)
│
└── src/main/java/com/la/northwind_java
    ├── config/exceptions/       # Manejo global de errores
    ├── controllers/              # Endpoints REST (Customer, Order, Product, Supplier, PurchaseOrder, Analytics)
    ├── dtos/                      # DTOs de entrada/salida, organizados por dominio
    ├── mappers/                    # Interfaces MapStruct
    ├── models/                      # Entidades JPA
    ├── repositories/                 # Interfaces Spring Data JPA
    ├── security/                      # Configuración de seguridad, JWT, roles, refresh tokens
    ├── services/                       # Lógica de negocio + implementaciones
    └── specification/                   # JPA Specifications para filtros dinámicos
```

## 🔑 Autenticación y roles

La API usa JWT para proteger los endpoints, con soporte de **refresh tokens** para renovar la sesión sin volver a loguearse, y control de acceso basado en **roles/privilegios**.

Flujo típico:

1. `POST /auth/login` con credenciales → devuelve un access token y un refresh token.
2. Incluir el access token en las peticiones protegidas:

   ```
   Authorization: Bearer <token>
   ```

3. Cuando el access token expira, usar el refresh token para obtener uno nuevo sin volver a pedir usuario/contraseña.

## 📚 Módulos principales de la API

| Módulo             | Descripción                                                        |
|---------------------|---------------------------------------------------------------------|
| **Customers**        | CRUD de clientes                                                    |
| **Products**          | CRUD de productos                                                   |
| **Suppliers**          | CRUD de proveedores, con filtros/búsqueda avanzada (Specifications) |
| **Orders**              | Pedidos de venta, con filtros, cambios de estado y estado impositivo |
| **Purchase Orders**      | Órdenes de compra a proveedores, con búsqueda y actualización       |
| **Analytics**             | KPIs y datos agregados para dashboards/gráficos                     |
| **Auth**                   | Login, registro, refresh de tokens, roles                           |

## 🧪 Tests

```bash
./mvnw test
```

Incluye tests de **controllers** (con MockMvc/Mockito) y de **services**, cubriendo lógica de negocio, filtros y el flujo de autenticación JWT.

## 🗺️ Próximos pasos

- [ ] Deploy en la nube (AWS)
- [ ] Dockerizar el backend completo (app + MySQL en `docker-compose`)
- [ ] CI con GitHub Actions
- [ ] Archivo `application.properties.example` para facilitar el setup

## 📄 Licencia

Este proyecto es de uso educativo / portfolio.