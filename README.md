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
- Contraseña: admin123

Empleado:
- Email: maria@beautyapp.com
- Contraseña: empleado123

Cliente:
- Email: ana@beautyapp.com
- Contraseña: cliente123
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

### Flujo de citas (cliente y empleado)

Reglas principales implementadas:

1. El cliente crea la cita con estado inicial `Pendiente`.
2. El empleado asignado revisa pendientes y puede confirmar/cancelar la cita.
3. Si el cliente modifica servicio, fecha u hora, la cita vuelve automáticamente a `Pendiente` para una nueva revisión del empleado.
4. Si una cita está `Confirmada`, el cliente solo puede modificarla o eliminarla cuando faltan al menos 48 horas para su fecha/hora.
5. Si el horario solicitado no tiene empleados disponibles, la app muestra al cliente una lista de horarios alternativos disponibles para ese día.

### Verificación de correo (local con SMTP de pruebas)

La entidad de usuarios incluye:

- `cuentaVerificada` (0/1): indica si la cuenta ya fue activada.
- `tokenVerificacion`: token temporal para validar el enlace recibido por correo.

Flujo implementado:

1. Registro de usuario con cuenta no verificada.
2. Envío de correo de verificación con enlace principal `https://beautyapp.local/verify?token=...`.
3. Apertura de `VerificarCorreoActivity` , al pulsar en el enlace el sistema operativo android da la opión de abrirlo con navegador o con la propia app, debemos elegir la app.
4. Validación de token y activación de cuenta.
5. Bloqueo de login si la cuenta no está verificada.

> Nota: la configuración SMTP actual es de pruebas (Mailtrap).  
> Para producción se recomienda backend propio o proveedor seguro de email.

---

## Instalación y ejecución

1. Clonar o descargar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Configurar el SMTP de prueba: dentro de la carpeta utils, en la clase CorreoHelper.java, hay que sustituir los valores de las constantes SMTP_USER y SMTP_PASSWORD por los valores proporcionados en tu cuenta de Mailtrap.
4. Sincronizar Gradle.
5. Ejecutar la aplicación en un emulador o dispositivo Android (API 26 o superior).

---

## Ejecución de la aplicación

La aplicación incluye datos de prueba cargados automáticamente al primer inicio, lo que permite probar todas las funcionalidades sin necesidad de configuración adicional, salvo la configuración de las credenciales de prueba SMTP de Mailtrap.

---

## Estado del proyecto

Proyecto funcional y completo, desarrollado con fines educativos.  
Incluye autenticación, gestión de roles, persistencia local de datos y validaciones básicas.

---

## Autor

Proyecto desarrollado por **Antonio Benítez Bernal** como parte del Proyecto Final del ciclo DAM.


