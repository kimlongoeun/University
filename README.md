# NextGen University Portal

The purpose of this web app is to manage university students. Only an admin can access the admin dashboard to manage all students, while registered students can log in to view and edit their own profile.

## Features

- **Authentication** — Register, login, and logout with Spring Security (dual-chain: Admin + Student)
- **Student Table** — View all students with full details and inline edit modal
- **Student Profile** — Each student can view and update their own profile after login
- **Responsive** — Mobile-friendly layout with Bootstrap 5
- **Modern UI** — Clean dark design with Bootstrap Icons

## Tools Used

Built with **Maven** and the following dependencies:

- Spring Web
- Spring Security
- Thymeleaf
- Spring Data JPA
- PostgreSQL Driver
- Spring Boot Actuator

## Data Structure

```java
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String studentId;   // e.g. STU-2024-001

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;    // BCrypt-hashed

    private String major;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    public enum Status { ACTIVE, INACTIVE, GRADUATED, SUSPENDED }
}
```

## How to Deploy on Railway

1. Deploy a **PostgreSQL** database and your **Spring Boot app** on Railway
2. Copy the environment variables from the PostgreSQL service and paste them into your Spring Boot app's variables
3. Update your `application.properties` to use Railway environment variables:
   ```properties
   spring.datasource.url=jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}
   spring.datasource.username=${PGUSER}
   spring.datasource.password=${PGPASSWORD}
   ```
4. If there are build errors due to environment differences, make sure to generate a JAR/WAR file so it matches the Railway runtime
5. Generate a domain from Railway and your app will be live
