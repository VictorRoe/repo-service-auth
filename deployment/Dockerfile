# --- ETAPA 1: Construcción (Builder) ---
# Usamos una imagen con el JDK completo y Gradle para construir el proyecto.
FROM gradle:9.0.0-jdk21-alpine AS builder

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /home/gradle/project

# Copiamos todo el código fuente del proyecto al contenedor

COPY --chown=gradle:gradle . .

# Convierte las terminaciones de línea de Windows (CRLF) a Unix (LF)
RUN sed -i 's/\r$//' ./gradlew

RUN chmod +x ./gradlew

# Ejecutamos el build de Gradle para generar el JAR ejecutable.
# El comando 'build' compila y empaqueta la aplicación. '-x test' se salta los tests para acelerar el build.
RUN ./gradlew build -x test


# --- ETAPA 2: Imagen Final ---
# Usamos una imagen más ligera que solo contiene el Java Runtime (JRE), no el JDK completo.
FROM eclipse-temurin:21-jre-alpine

# Creamos un grupo y un usuario 'appuser' sin privilegios
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Establecemos el directorio de trabajo para la aplicación
WORKDIR /app

# Copiamos ÚNICAMENTE el JAR ejecutable desde la etapa de construcción (builder)
# Ajusta la ruta si el nombre de tu JAR es diferente.
COPY --from=builder /home/gradle/project/applications/app-service/build/libs/*.jar application.jar

# Le damos la propiedad del JAR al nuevo usuario
RUN chown appuser:appgroup application.jar

# Cambiamos al usuario sin privilegios por seguridad
USER appuser

# Exponemos el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT [ "java", "-jar", "application.jar" ]