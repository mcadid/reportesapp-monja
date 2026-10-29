# ReportesApp

Aplicación Android en Java para consultar y registrar reportes de acumulación de residuos.

## Ejecutar

Abre esta carpeta en Android Studio, sincroniza Gradle e inicia un emulador. La app está configurada por defecto para conectarse a una API local mediante `http://10.0.2.2:8080/`; primero debe estar ejecutándose `reporteservice`.

Para usar Render, cambia `BASE_URL` en `app/src/main/java/com/monja/reportesapp/ApiClient.java` por la URL HTTPS pública de la API, con barra final.

## Funciones

- Lista paginada de reportes.
- Registro con tipo de residuo seleccionado de una lista.
- Búsqueda por DNI.
- Detalle y acción para marcar un reporte como atendido.
