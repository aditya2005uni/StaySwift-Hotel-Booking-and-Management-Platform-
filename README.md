# StaySwift

StaySwift is a hotel room booking system built with Spring Boot. It allows users to browse available rooms, book a stay, view their bookings, and cancel them. Admins can manage room inventory and oversee all bookings across the platform.

This project was built as a learning exercise to practice Spring Boot, Spring Security with JWT, and REST API design.

## Tech Stack

**Backend**
- Java 17
- Spring Boot 3.2.0
- Spring Security (JWT-based authentication)
- Spring Data JPA (Hibernate)
- MySQL
- Lombok
- JJWT (JSON Web Token library)
- Maven

**Frontend**
- Plain HTML, CSS, JavaScript (no framework)
- Served as static files from the Spring Boot application

## Features

### Authentication
- User signup with role selection (USER or ADMIN)
- Login returns a JWT token used to authenticate subsequent requests
- Passwords are hashed using BCrypt before being stored

### Rooms
- Public endpoint to browse all rooms and check live availability, with no login required
- Admin can add new room types
- Admin can increase the quantity of an existing room type
- Room availability is tracked using a stock-style counter (`totalRooms`) rather than a simple boolean flag

### Bookings
- Authenticated users can book a room by selecting check-in and check-out dates
- Total amount is calculated on the backend at the time of booking (nights times price per night)
- Users can view their own booking history
- Users can permanently delete their own booking
- Admin can view all bookings across all users
- Admin can cancel any booking (status changes to CANCELLED, the booking record is retained for history)
- Room stock count automatically increases when a booking is deleted or cancelled, and decreases when a new booking is made

## Architecture

The backend follows a standard layered architecture:
Controller  ->  Service  ->  Repository  ->  Database
- **Controller layer** — handles HTTP requests and responses, performs no business logic
- **Service layer** — contains the business logic (booking rules, stock management, password hashing)
- **Repository layer** — Spring Data JPA interfaces for database access
- **Entity layer** — JPA entities mapped to MySQL tables

### Security Flow

Authentication is handled using stateless JWT tokens rather than sessions.

1. User logs in with email and password
2. `AuthenticationManager` delegates to `DaoAuthenticationProvider`, which uses `UserDetailsService` to fetch the user and `BCryptPasswordEncoder` to verify the password
3. On success, a JWT containing the user's email and role is generated and returned
4. On every subsequent request, a custom `JwtFilter` intercepts the request, validates the token, and sets the authentication in `SecurityContextHolder`
5. Endpoints under `/admin/**` are restricted to users with the ADMIN role; `/auth/**` and `/rooms/**` are public; everything else requires a valid token

### Database Schema

Three main tables:

**users**
| Column | Type | Notes |
|---|---|---|
| id | bigint | primary key, auto-increment |
| email | varchar | unique |
| password | varchar | BCrypt hashed |
| role | enum | USER or ADMIN |

**rooms**
| Column | Type | Notes |
|---|---|---|
| id | bigint | primary key, auto-increment |
| room_type | varchar | e.g. Deluxe, Standard, Suite |
| description | varchar | |
| price_per_night | double | |
| total_rooms | int | acts as stock count |

**bookings**
| Column | Type | Notes |
|---|---|---|
| id | bigint | primary key, auto-increment |
| email | varchar | email of the booking user |
| check_in_date | date | |
| check_out_date | date | |
| number_of_nights | bigint | calculated on booking |
| total_amount | double | calculated on booking |
| room_id | bigint | foreign key to rooms (Many-to-One) |
| status | enum | CONFIRMED or CANCELLED |


## Setup Instructions

### Prerequisites
- Java 17 or higher
- MySQL installed and running
- Maven (or use the included Maven wrapper)

