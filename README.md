# POC-Transcribe
## Descripción
POC-Transcribe es una aplicación de prueba de concepto que compara diferentes modelos de transcripción de OpenAI. La aplicación utiliza Spring Boot y Spring AI para transcribir archivos de audio utilizando los modelos "whisper-1" y "gpt-4o-transcribe", y muestra los resultados y tiempos de transcripción.
## Requisitos
- Java 17
- Maven 3.6+
- Una clave API de OpenAI

## Configuración
### Variables de entorno
Antes de ejecutar la aplicación, necesitas configurar tu clave API de OpenAI como una variable de entorno:
``` bash
# Para Linux/macOS
export KEY=tu_clave_api_de_openai

# Para Windows (CMD)
set KEY=tu_clave_api_de_openai

# Para Windows (PowerShell)
$env:KEY="tu_clave_api_de_openai"
```
Alternativamente, puedes crear un archivo en `src/main/resources` con el siguiente contenido: `application.properties`
``` properties
spring.application.name=poc-transcribe
spring.ai.openai.api-key=tu_clave_api_de_openai
spring.ai.openai.transcription.api-key=${KEY}
spring.output.ansi.enabled=ALWAYS
management.endpoints.web.exposure.include=metrics,prometheus
```
## Cómo ejecutar la aplicación
### Usando Maven
``` bash
# Compilar el proyecto
mvn clean package

# Ejecutar la aplicación
mvn spring-boot:run
```
### Usando Java
``` bash
java -jar target/poc-transcribe-0.0.1-SNAPSHOT.jar
```
## Uso de la API
La aplicación expone un endpoint para transcribir archivos de audio:
### Endpoint de transcripción
``` 
POST /transcribe
```
#### Cómo hacer peticiones usando curl
``` bash
curl --location 'http://localhost:8080/transcribe' \
--form 'file=@"/ruta/a/tu/archivo/audio.ogg"'
```
Ejemplo (usando la ruta proporcionada):
``` bash
curl --location 'http://localhost:8080/transcribe' \
--form 'file=@"/C:/Users/guesa/Downloads/sonido.ogg"'
```
#### Respuesta
La aplicación responderá con un mensaje de confirmación y mostrará los resultados detallados de la transcripción en la consola, incluyendo:
- Transcripción del modelo Whisper
- Tiempo de transcripción del modelo Whisper
- Transcripción del modelo GPT-4o
- Tiempo de transcripción del modelo GPT-4o

## Características
- Transcripción de audio usando múltiples modelos de IA
- Medición de rendimiento y tiempos de respuesta
- Exposición de métricas a través de actuator para monitoreo
- Soporte para archivos de audio en diferentes formatos

## Métricas
La aplicación expone métricas a través de Spring Boot Actuator, que pueden ser accedidas en:
``` 
GET /actuator/metrics
GET /actuator/prometheus
```
## Tecnologías utilizadas
- Spring Boot 3.5.0
- Spring AI 1.0.0
- OpenAI API (modelos whisper-1 y gpt-4o-transcribe)
- Spring Boot Actuator para métricas

## Estructura del proyecto
La aplicación sigue una estructura estándar de Spring Boot con un controlador REST que maneja las solicitudes de transcripción y utiliza el cliente de Spring AI para comunicarse con la API de OpenAI.
