# Шаг 1: Используем официальный образ JRE 21
FROM eclipse-temurin:21-jre-alpine

# Шаг 2: Указываем рабочую директорию
WORKDIR /app

# Шаг 3: Универсальное копирование любого JAR-файла из папки target
COPY target/*.jar app.jar

# Шаг 4: Открываем порт 8080
EXPOSE 8080

# Шаг 5: Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]