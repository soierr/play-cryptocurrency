## XM Cryptocurrency Recommendation Service

### Description

Developed application is designed as pretty standard spring boot application
used MySQL for cryptocurrency prices storage & management  
Once application is started it reads data from csv files and loads it to database  
Processed files won't be read again unless created and/or modified date are changed

Java 11 used

Project name: `play-cryptocurrency`

### API Documentation

Provided as local `swagger-ui.html`

`${PROJECT_HOME}\swagger-ui\currency-contoller.html`

`${PROJECT_HOME}` is root directory of cloned project like `C:/java/play-cryptocurrency`

### Building

Create executable

```shell
gradlew clean bootJar
```

Run tests

```shell
gradlew clean test
```

Run test with docker based tests, checks sql on real MySQL
See `src/test/resources/.testcontainer.properties` for configuration details

```shell
gradlew clean test -PincludeIt
```

Jar location: `${PROJECT_HOME}\build\libs\play-cryptocurrency-1.0-SNAPSHOT.jar`

### Start

In order to start an application MySQL up and running needed.

`docker-compose.yml` can be used located here: `${PROJECT_HOME}\docker`

Change `${PROJECT_HOME}` in script below, accordingly to your local path

```shell
export DB_NAME='cryptodb' \
DB_USERNAME='crypto' \
DB_PASSWORD='0!*XMP0)*-_Enough' \
DB_ROOT_PASSWORD='test' && \
docker-compose -f ${PROJECT_HOME}/docker/docker-compose.yml up
```

Once container is created & started MySQL user `crypto` will be created automatically.

Possible start options

Change `${PROJECT_HOME}` in scripts below, accordingly to your local path

1. Start app in Intellij Idea

- Create configuration with main `RecommendationService` class
- Create env variables for this configuration, copy block below:

```shell
DB_JDBC_URL=jdbc:mysql://localhost:3306/cryptodb;  
DB_USERNAME=crypto;  
DB_PASSWORD=0!*XMP0)*-_Enough;  
PRICE_FOLDER_PATH=${PROJECT_HOME}\prices
```

2. Start all via docker compose

Make sure you have application jar, see Building section for details

Create docker image:

```shell
cd ${PROJECT_HOME}

docker build . -t play-cryptocurrency:1.0-SNAPSHOT
```

Start via `docker-compose-all.yml`

```shell
export DB_NAME='cryptodb' \
DB_USERNAME='crypto' \
DB_PASSWORD='0!*XMP0)*-_Enough' \
DB_ROOT_PASSWORD='test' \
DB_JDBC_URL=jdbc:mysql://mysql-8.0.28:3306/cryptodb \
PRICE_FOLDER_PATH=/opt/prices &&\
docker-compose -f ${PROJECT_HOME}/docker/docker-compose-all.yml up
```

3. Start standalone jar with only MySQL in Docker

Make sure you have application jar, see Building section for details

`cd ${PROJECT_HOME}/build/libs`

Windows terminal example: 
```shell
java -jar play-cryptocurrency-1.0-SNAPSHOT.jar ^
--DB_USERNAME=crypto ^
--DB_PASSWORD=0!*XMP0)*-_Enough ^
--PRICE_FOLDER_PATH=${PROJECT_HOME}\prices ^
--DB_JDBC_URL=jdbc:mysql://localhost:3306/cryptodb
```

Linux terminal example:
```shell
java -jar play-cryptocurrency-1.0-SNAPSHOT.jar \
--DB_USERNAME=crypto \
--DB_PASSWORD='0!*XMP0)*-_Enough' \
--PRICE_FOLDER_PATH=${PROJECT_HOME}\prices \
--DB_JDBC_URL=jdbc:mysql://localhost:3306/cryptodb
```
<br>

> P.S. All creds are committed for simplicity not for production usage

### Logging

Kibana ready json log configured, can be activated with proper spring profile

Details in `src/main/resources/logback-spring.xml`

### API Examples

GET

```http://localhost:8080/currency/ranges```

```http://localhost:8080/currency/filtered?currency_code=BTC```

```http://localhost:8080/currency/highest-range?date=2022-01-01```

