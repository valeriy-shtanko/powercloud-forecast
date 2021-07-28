### Overview

The Forecast is a Spring Boot based application which provides REST API (using 8080 port) to retrieve weather forecast.
OpenWeather service is using to retrieve information about the weather.

Application can be used as stand alone or as a part of micro-service application.

### Build

Run `./mvnw clean install` in the project root build application or `./mvnw clean install -DskipTests` to build projects without running tests.

### Start application

To start application use `mvn spring-boot:run` or `java -jar target/forecast-0.0.1-SNAPSHOT.jar` from the project root.
