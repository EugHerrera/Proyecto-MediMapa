# Fase de build: compilación estricta y empaquetado
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiamos solo el pom y el código fuente
COPY pom.xml .
COPY src ./src

# Quality Gate: build completo y empaque. El contenedor falla si Maven detecta errores o tests rotos.
RUN mvn clean package
RUN cp target/*.jar app.jar

# Fase de runtime: solo el artefacto empaquetado
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]