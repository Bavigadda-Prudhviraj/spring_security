# spring_security

# Spring Boot Project with Basic Authentication, Logback, and Flyway

This project is a Spring Boot application that includes the following features:

- **Basic Authentication** using Spring Security
- **Logging** using Logback
- **Database Migrations** using Flyway

## Features

### 1. Basic Authentication
The application uses Spring Security to implement basic authentication. Users must provide a valid username and password to access protected endpoints.

- **Default Credentials**:
  - Username: `prudhviraj`
  - Password: `admin`
  
To modify the default credentials, update the `application.properties`  file:

```properties
spring.security.user.name = prudhviraj
spring.security.user.password = admin
