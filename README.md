# inventory

This project correspond to the inventory microservice of the application. This service is responsible for managing all
data related to inventory for all beans, such as price, available quantity etc. It also manages orders placed by clients,

## Running the application on local environment

In order to be able to run this application on the local environment, there are a couple of configurations that need to
be performed first.

1. Run the docker-compose file from this project. It will spin up a container with the database required by the service.
2. Connect to the database container on port 5732, with username "postgres" and password "pass". JDBC URL: jdbc:postgresql://localhost:5732/postgres
3. Run the following SQL queries on the database:
```postgresql
CREATE SCHEMA inventory;
CREATE ROLE inventory with LOGIN ENCRYPTED PASSWORD 'inventory';
```
4. Run the following command in order to create all the necessary tables of the database:
```shell script
./gradlew update
```
5. Run the sql script from the src/main/resources/database/loadData folder, and then execute the following sql query to populate the database with data:
```postgresql
CALL insert_data();
```

Now that all the necessary configurations have been performed, the application itself can be started in development mode with the following command:
```shell script
./gradlew quarkusDev
```
The application will be reachable on port 9091.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

The artifacts created by the build step will be used when creating the Docker image using the Dockerfile from this project.
