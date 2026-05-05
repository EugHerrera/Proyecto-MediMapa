# Usamos una imagen oficial que ya tiene Maven y Java 17 integrados
FROM maven:3.9.6-eclipse-temurin-21-alpine

WORKDIR /app

# Copiamos SOLO tu código y el pom.xml
COPY pom.xml .
COPY src ./src

# Compilamos usando el Maven nativo de la imagen (mvn en vez de ./mvnw)
RUN mvn clean package -DskipTests

# Ejecutamos el archivo .jar
CMD ["sh", "-c", "java -jar target/*.jar"]