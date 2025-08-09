# Используем образ с Maven и JDK
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и зависимости отдельно для кэширования
COPY pom.xml .
COPY src ./src

# Собираем JAR-файл
RUN mvn clean package -DskipTests

# Финальный образ с JDK
FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем собранный JAR из предыдущего этапа
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
