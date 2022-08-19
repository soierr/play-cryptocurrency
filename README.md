## XM Cryptocurrency Recommendation Service

### Description

Developed application is designed as pretty standard spring boot application
used MySQL for cryptocurrency prices storage & management  
Once application is started it reads data from csv files and loads it to database  
Processed files won't be read again unless created and/or modified date are changed

Java 11 used

Project name: `play-cryptocurrency`

### API Documentaion

Provided as local `swagger-ui.html`

`${PROJECT_HOME}\swagger-ui\currency-contoller.html`

`${PROJECT_HOME}` is root directory of cloned project like `C:/java/play-cryptocurrency`

### Building

```shell
gradlew clean bootJar
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

1. Start app in Intellij Idea

- Create configuration with main `RecommendationService` class
- Create env variables for this configuration:

 Change `${PROJECT_HOME}` in script below, accordingly to your local path

```shell
DB_JDBC_URL=jdbc:mysql://localhost:3306/cryptodb;  
DB_USERNAME=crypto;  
DB_PASSWORD=0!*XMP0)*-_Enough;  
PRICE_FOLDER_PATH=${PROJECT_HOME}\prices
```

2. Start standalone jar

First, make sure you have application jar, see Building section for details

`cd ${PROJECT_HOME}/build/libs`

Change `${PROJECT_HOME}` in scripts below, accordingly to your local path

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

### API Examples

GET

```http://localhost:8080/currency/ranges```

```http://localhost:8080/currency/filtered?currency_code=BTC```

```http://localhost:8080/currency/highest-range?date=2022-01-01```

