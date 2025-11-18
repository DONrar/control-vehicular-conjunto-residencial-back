FROM openjdk:22-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR generado en tu proyecto a la carpeta de trabajo en el contenedor
COPY target/control-vehicular-0.0.1-SNAPSHOT.jar app.jar


# Expone el puerto que utiliza la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]