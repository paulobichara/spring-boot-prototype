# Spring Boot Prototype

This project is a Spring Boot 2 prototype application containing:

- REST API with JWT authentication
- Method level authorization (using custom Spring Security annotations)
- SpringFox 2.9.2 integration providing Swagger UI (with JWT support)
- Spring Data Specification usage as part of the filtering functionality
- Spring integration with Liquibase for more control over incremental database changes
- Gradle 5.5 as project management tool
- jUnit 5 for tests

## Build and Test

The project consists of a simple gradle project. In order to build and test it, just clone the project, 
set the directory to project root and execute:

```
./gradlew clean build
``` 

All tests will be executed using a H2 in-memory database, so at this point no additional setup is required.

## Setup MySQL Database

The API project is configured with an [extension](https://github.com/palantir/gradle-docker#docker-run-plugin) for manipulating **docker** containers. Considering an environment with 
docker installed, all that needs to be done is:

```
./gradlew dockerRun
```

All the configuration for the required **development** database container can be found on **build.gradle** file. It 
basically uses MySQL v8.0 with a predefined root password and timezone setting to UTC:

```groovy
dockerRun {
    name 'prototype-mysql'
    image 'mysql:8.0'
    ports '3306:3306'
    daemonize true
    env 'MYSQL_ROOT_PASSWORD': 'password', 'TZ': 'UTC'
}
```

There's also the option to manually setup the database and change the sample **application.yml** properties related to 
the database connection:

```yaml
spring:
    (...)
    datasource:
        url: jdbc:mysql://localhost:3306/prototype?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
        username: root
        password: password
    (...)
```

## Running the Application

Now that the database is ready, the API can be started. The project is a Spring Boot application and can be started 
accordingly:

Running from jar file:
```
cd [project_root]/build/libs/
java -jar spring-boot-prototype-1.0-SNAPSHOT.jar
```

Using Spring Boot Gradle plugin:
```
cd [project_root]/
./gradlew bootRun
```

Finally, the project can also be executed from the main method of the **Application** class from your IDE of choice.


## Exploring the API

With the application running, please access 
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) for a full list of APIs and end points
with descriptions for each one. In the next sections, the resources (and how to access the protected ones) will be 
briefly described.

### Public End Points

The API basically has one public resource (no authentication required): the end point to 
[create regular users](http://localhost:8080/swagger-ui.html#/user-controller/addNewUserUsingPOST).

### JWT Authentication

For the protected resources, the API uses [JWT](https://tools.ietf.org/html/rfc7519) authentication. In order to get a
valid JWT, the client needs to authenticate with the created user email and password. This can be achieved by 
executing the following POST request (presented bellow as a **curl** command for simplicity):

```
curl -i -X POST -d username=paulo@email.com -d password=paulo-password http://localhost:8080/api/login
```

The above example is valid for a user with email **paulo@email.com** and password **paulo-password**. Please modify
the command with your intended credentials before executing it.

The server will reply with similar headers:

```yaml
HTTP/1.1 200 
   Authorization: Bearer EYj0ExaIOIJKV1QiLCJhbGciOiJIUzXXXiJ9.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InBhdWxvQGVtYWlsLmNvbSIsImV4cCI6MTU2MzkyNjQwMCwicm9sIjpbIlJPTEVfVVNFUiJdfQ.eCkg5dn3Ce3AdGnQGQgKJcMun1u8jgfjpO9C4f4yB8Mb-lRek4qOEU0hMH7gC-i5uGb5iW9DtqyyagrWrXYHqA
   X-Content-Type-Options: nosniff
   X-XSS-Protection: 1; mode=block
   Cache-Control: no-cache, no-store, max-age=0, must-revalidate
   Pragma: no-cache
   Expires: 0
   X-Frame-Options: DENY
   Content-Length: 0
   Date: Sun, 14 Jul 2019 17:48:07 GMT
```

 The important part for the authentication is the value for the **Authorization** header. This value needs to be sent
 to the server in the same way (as the value of the HTTP header **Authorization**) in every request involving a 
 protected resource (otherwise, request will fail with 403 forbidden error).
 
 When exploring the API through [Swagger UI]((http://localhost:8080/swagger-ui.html)), it is possible to set the header
 value to be used in all requests:
 
 ![Screenshot](images/swagger-ui-auth-1.png)
  
 All that is required is to click on **Authorize** button and provide the header retrieved from the **curl** command 
 executed previously:
 
 ![Screenshot](images/swagger-ui-auth-2.png)
 
 Now, the authorized user will be able to access the appropriate protected resources, depending on the user role:
 
 - Regular users can only access the owned account data.
 - Admins are created from a [protected end point](http://localhost:8080/swagger-ui.html#/admin-controller/addNewAdminUsingPOST)
  and can access and modify data from all users.
  
### Default Admin Account

In order to create other admins and user managers, the application creates a default admin user account based on the 
information provided in the `application.yml` configuration file:

```yaml
api:
    admin:
        email: admin@email.com
        password: admin-password
```

### References

I used a lot of different references for this project, so I'll try to summarize the most important ones bellow:

- https://spring.io/projects/spring-boot
- https://docs.spring.io/spring-security/site/docs/current/reference/html5/
- https://junit.org/junit5/docs/current/user-guide/
- https://dev.to/keysh/spring-security-with-jwt-3j76
- https://www.toptal.com/java/rest-security-with-jwt-spring-security-and-java
- https://www.toptal.com/java/spring-boot-rest-api-error-handling
- https://www.baeldung.com/rest-with-spring-series
