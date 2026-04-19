# Nabd Clinic System

A modern clinic management system built with Spring Boot and vanilla JavaScript. This project provides a complete solution for managing doctors, patients, and appointments with a beautiful Arabic-first UI.

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?style=flat-square&logo=spring)
![SQL Server](https://img.shields.io/badge/SQL%20Server-2019+-red?style=flat-square&logo=microsoft-sql-server)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

## Features

- **Doctor Management** - Add, view, and manage doctor profiles with specializations
- **Patient Registration** - Register patients with validation for Egyptian phone numbers
- **Appointment Booking** - Book appointments with real-time availability
- **Multi-table Relations** - Full support for patient-doctor and appointment-doctor relationships
- **Arabic UI** - Native RTL support with beautiful Arabic typography
- **Responsive Design** - Works on desktop, tablet, and mobile devices
- **REST API** - Clean RESTful endpoints for all operations

## Tech Stack

### Backend
- Java 17+
- Spring Boot 3.5
- Spring Data JPA
- MS SQL Server
- Maven

### Frontend
- HTML5, CSS3, JavaScript (ES6+)
- No frameworks - pure vanilla JS
- Custom CSS with CSS Variables
- Font Awesome icons
- Google Fonts (Tajawal, Amiri)

## Project Structure

```
clinic_ssys/
├── backend/
│   ├── src/main/java/hana/com/clinic_system/
│   │   ├── controllers/       # REST Controllers
│   │   ├── entities/          # JPA Entities
│   │   ├── repositories/      # Data Repositories
│   │   └── config/            # Configuration Classes
│   └── pom.xml
├── index.html                 # Landing page
├── booking.html               # Appointment booking
├── appointments.html          # View appointments
├── main.js                    # UI logic
├── data.js                    # API service layer
├── style.css                  # All styles
├── clinic_system.sql          # Database schema
└── run.bat                    # Quick start script
```

## Database Schema

The system uses 5 tables with proper foreign key relationships:

```
doctors                 patients               appointments
├── id (PK)             ├── id (PK)            ├── id (PK)
├── name                ├── name               ├── date
└── specialization      ├── phone              ├── time
                        └── email              ├── status
                                               ├── patient_id (FK)
patient_doctors         └── doctor_id (FK)
├── patient_id (PK,FK)
├── doctor_id (PK,FK)   appointment_doctors
                        ├── appointment_id (PK,FK)
                        ├── doctor_id (PK,FK)
                        └── patient_id (FK)
```

## Quick Start

### Prerequisites

- Java 17 or higher
- MS SQL Server (Express edition works)
- Maven (included via wrapper)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/nabd-clinic.git
   cd nabd-clinic
   ```

2. **Set up the database**
   
   Open SQL Server Management Studio and run:
   ```sql
   -- Run the contents of clinic_system.sql
   -- This creates the database and tables
   ```

3. **Configure database connection**
   
   Edit `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost\SQLEXPRESS;databaseName=clinic_db;encrypt=false;trustServerCertificate=true
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run the application**
   
   **Option A: Using the batch script (Windows)**
   ```bash
   run.bat
   ```
   
   **Option B: Manual start**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   
   Then open `index.html` in your browser.

5. **Access the application**
   - Frontend: Open `index.html` in a browser
   - Backend API: `http://localhost:8080/api`

## API Endpoints

### Doctors
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctors` | Get all doctors |
| GET | `/api/doctors/{id}` | Get doctor by ID |
| DELETE | `/api/doctors/{id}` | Delete doctor |

### Patients
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patients` | Get all patients |
| GET | `/api/patients/{id}` | Get patient by ID |
| GET | `/api/patients/email/{email}` | Get patient by email |
| POST | `/api/patients` | Create new patient |
| PUT | `/api/patients/{id}` | Update patient |
| DELETE | `/api/patients/{id}` | Delete patient |

### Appointments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/appointments` | Get all appointments |
| GET | `/api/appointments/{id}` | Get appointment by ID |
| POST | `/api/appointments` | Create new appointment |
| PUT | `/api/appointments/{id}` | Update appointment |
| DELETE | `/api/appointments/{id}` | Delete appointment |

### Patient-Doctors Relations
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patient-doctors/patient/{id}` | Get doctors for patient |
| GET | `/api/patient-doctors/doctor/{id}` | Get patients for doctor |
| POST | `/api/patient-doctors` | Create relation |
| DELETE | `/api/patient-doctors/patient/{pId}/doctor/{dId}` | Delete relation |

### Appointment-Doctors Relations
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/appointment-doctors/appointment/{id}` | Get doctors for appointment |
| GET | `/api/appointment-doctors/doctor/{id}` | Get appointments for doctor |
| POST | `/api/appointment-doctors` | Create relation |
| DELETE | `/api/appointment-doctors/appointment/{aId}/doctor/{dId}` | Delete relation |

## Configuration

### Database Configuration

The application uses MS SQL Server. Key properties:

```properties
# Database connection
spring.datasource.url=jdbc:sqlserver://localhost\SQLEXPRESS;databaseName=clinic_db
spring.datasource.username=sa
spring.datasource.password=your_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

### CORS Configuration

CORS is configured in `CorsConfig.java` to allow requests from:
- `http://localhost:5500`
- `http://127.0.0.1:5500`

Add more origins if needed.

## Development

### Building the project

```bash
cd backend
./mvnw clean package
```

### Running tests

```bash
./mvnw test
```

### Database validation

The app uses `ddl-auto=validate` which means:
- Schema must match entities exactly
- No automatic table creation
- Safer for production

## Screenshots

*Screenshots to be added*

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot team for the excellent framework
- Google Fonts for Tajawal and Amiri Arabic fonts
- Font Awesome for the beautiful icons

---

Built with ❤️ for healthcare management
