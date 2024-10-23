# Library Management System
A very basic and simple Library Management App as per the spec provided.

## Tech stack:
* Apache Maven 3.9.6
* Java 17
* Spring Boot 3.3.4
* H2 In memory DB (name = libraryDb)

## Assumptions
* Publication Year for a book is limited between 1950 and 2024
* Since there wasn't a requirement for book return limit, the available copies can go beyond the original number of copies provided while creating the book.
* Available copies cannot be negative while borrowing.

## Testing
* Unit testing is done using Mockito framework
* E2E/Integration testing is done using SpringBoot testing framework (Flavours of SpringBootTest and WebMvcTest used)

## Running the application
* Clone the GitHub repo https://github.com/hatimg/library-app
* cd into the above repo folder
* Run the following command to run the application
```shell
mvn spring-boot:run
```

## API Documentation
Once the application is up and running, the swagger documentation for the ledger apis can be accessed at http://localhost:9090/swagger-ui/index.html.
Json document for the APIs can be found on http://localhost:9090/library-api-docs
Swagger UI (http://localhost:9090/swagger-ui/index.html) can be used to execute and test the APIs.

#### Stuff not implemented/improvements that could be implemented due to lack of time:
* Rate Limiting
* JWT Authentication (Can be easily done using Sprint Security module for JWT)
* Explicit Unit tests for some utility classes. However, the coverage on the code is near to 100% 
* Full Rest API documentation for all the end points.