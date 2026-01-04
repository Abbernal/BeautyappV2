# BeautyApp - Aplicación Android de Gestión de Citas

Aplicación móvil Android desarrollada en **Java** para la gestión de citas en un centro de peluquería y estética.  
Proyecto realizado como **Proyecto Final del Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM)**.

---

## Descripción general

BeautyApp permite gestionar de forma sencilla las citas de un centro de belleza, diferenciando funcionalidades según el rol del usuario.

La aplicación funciona de manera local utilizando una base de datos SQLite integrada en la propia aplicación.

---

## Roles de usuario

La aplicación contempla tres tipos de usuario:

- **Administrador**: gestión de usuarios, servicios y citas.
- **Empleado**: visualización de agenda y actualización del estado de las citas asignadas.
- **Cliente**: reserva, consulta y modificación de sus propias citas.
- 
## Usuarios de prueba

Administrador:
- Email: admin@beautyapp.com
- Contraseña: 123456

Empleado:
- Email: maria.g@beautyapp.com
- Contraseña: 123456

Cliente:
- Email: ana.r@beautyapp.com
- Contraseña: 123456
---

## Tecnologías utilizadas

- **Lenguaje**: Java
- **Entorno de desarrollo**: Android Studio
- **Base de datos**: SQLite
- **Persistencia**: Room (Android Jetpack)
- **Interfaz**: XML + Material Design

---

## Base de datos

La base de datos está compuesta por las siguientes tablas principales:

- Roles
- Usuarios
- Servicios
- Estados de cita
- Citas
- Cita_Empleado

Las relaciones entre tablas se gestionan mediante claves externas, utilizando Room como capa de acceso a datos.

---

## Instalación y ejecución

1. Clonar o descargar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Sincronizar Gradle.
4. Ejecutar la aplicación en un emulador o dispositivo Android (API 26 o superior).

---

## Ejecución de la aplicación

La aplicación incluye datos de prueba cargados automáticamente al primer inicio, lo que permite probar todas las funcionalidades sin necesidad de configuración adicional.

---

## Estado del proyecto

Proyecto funcional y completo, desarrollado con fines educativos.  
Incluye autenticación, gestión de roles, persistencia local de datos y validaciones básicas.

---

## Autor

Proyecto desarrollado por **Antonio Benítez Bernal** como parte del Proyecto Final del ciclo DAM.


