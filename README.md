# ReportesApp

Aplicación Android en Java para consultar y registrar reportes de acumulación de residuos.

## Ejecutar

Abre esta carpeta en Android Studio, sincroniza Gradle e inicia un emulador. La aplicación se conecta a la API pública de Render en `https://reporteservice-monja.onrender.com/`.

Para volver a usar una API local, cambia `BASE_URL` en `app/src/main/java/com/monja/reportesapp/ApiClient.java` a `http://10.0.2.2:8080/` y ejecuta `reporteservice` localmente.

## Funciones

- Lista paginada de reportes.
- Registro con tipo de residuo seleccionado de una lista.
- Búsqueda por DNI.
- Detalle y acción para marcar un reporte como atendido.
