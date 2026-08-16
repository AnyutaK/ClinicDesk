# ClinicDesk 

ClinicDesk is a database-driven desktop clinic management system built using **Java**, **JavaFX**, and **PostgreSQL**.
The project combines a JavaFX frontend with a PostgreSQL database to manage patients, doctors, appointments, and scheduling slots. It also demonstrates database concepts including constraints, indexes, views, stored functions, transactions, triggers, audit logging, and role-based permissions.

## Features
### Dashboard

The dashboard provides an overview of clinic activity, including:

- Today's appointments
- Patients waiting
- Doctors on duty
- Slot utilization
- Upcoming appointments
- Quick actions for common clinic operations

Dashboard statistics are retrieved from the PostgreSQL database through the application's DAO and service layers.
### Patient Management

ClinicDesk provides functionality for managing patient records.
- Search patients
- View patient information
- Add new patients
- Update patient information
- Delete patient records
- Search using database functions
Patient records contain information such as:
- Patient ID
- Name
- Sex
- Date of birth
- Insurance
- Phone
- Email

### Doctor Management

The application provides doctor management functionality.

- Search doctors
- View doctor information
- Add doctors
- Update doctor information
- Delete doctors
- Organize doctors by department and specialization

Doctor records include:

- Doctor ID
- Doctor name
- Department
- Specialization
- Phone
- Email

###  Appointment Management

ClinicDesk supports appointment CRUD operations.

- Search appointments
- Create appointments
- View appointment details
- Edit appointments
- Delete appointments
- Assign patients to available slots
- Assign appointments to doctors
- Track appointment status

Appointments are associated with both a patient and a scheduling slot.


###  Slot Management

The system maintains doctor scheduling slots and their availability.

- View available slots
- Find slots by doctor
- Associate slots with appointments
- Track whether a slot is available or booked

Appointment operations interact with slot availability through PostgreSQL triggers.



# Database

ClinicDesk uses **PostgreSQL** as its relational database.

The database contains the following primary entities:

```text
Patients
    │
    ▼
Appointments ───────► Slots
                       │
                       ▼
                    Doctors
````

An additional `Audit_Log` table records appointment-related actions.

---

## Database Schema

### Patients

Stores patient information.

```text
patient_id
name
sex
dob
insurance
phone
email
created_at
```

### Doctors

Stores doctor information.

```text
doctor_id
doctor_name
department
specialization
phone
email
created_at
```

### Slots

Stores doctor scheduling slots.

```text
slot_id
doctor_id
appointment_date
appointment_time
is_available
```

### Appointments

Stores appointments between patients and scheduling slots.

```text
appointment_id
patient_id
slot_id
scheduling_date
status
check_in_time
start_time
end_time
appointment_duration
created_at
```

### Audit Log

Records appointment-related actions.

```text
log_id
appointment_id
action
performed_at
```

---

# Database Features

The project applies several DBMS concepts directly to the application.

### Constraints

The database uses:

* Primary keys
* Foreign keys
* Unique constraints
* CHECK constraints
* Referential integrity

Examples include:

* Unique patient email
* Unique doctor email
* Valid patient sex values
* Valid appointment statuses
* Positive appointment duration

---

### Indexes

Indexes are created for commonly queried fields, including:

```text
Patients.name
Slots.appointment_date
Slots.doctor_id
Appointments.patient_id
Appointments.slot_id
```

---

### Views

The project includes database views for commonly used reports and queries.

```text
vw_doctor_schedule
vw_available_slots
vw_patient_history
vw_doctor_statistics
vw_insurance_statistics
vw_daily_clinic_summary
vw_doctor_utilization
vw_upcoming_appointments
vw_frequent_patients
vw_monthly_appointments
```

These views provide information such as:

* Doctor schedules
* Available slots
* Patient history
* Doctor statistics
* Insurance statistics
* Daily clinic summaries
* Doctor utilization
* Upcoming appointments
* Frequent patients
* Monthly appointment counts

---

# Stored Functions

ClinicDesk uses PostgreSQL functions for reusable database operations and analytics.

Examples include:

```text
get_patient_visit_count()
get_doctor_appointments()
average_waiting_time()
count_available_slots()
estimate_revenue()
get_doctor_schedule()
get_patient_history()
get_clinic_summary()
get_doctor_utilization()
get_available_slots()
search_patients()
search_doctors()
```

This allows part of the application's data processing and analytical logic to be handled directly by PostgreSQL.

---

# Transactions

Appointment operations are implemented through PostgreSQL functions.

The project includes functions for:

### Booking

```text
book_appointment()
```

The booking process checks:

1. Whether the patient exists
2. Whether the slot exists
3. Whether the slot is available
4. Whether the appointment can be created

### Cancellation

```text
cancel_appointment()
```

Cancels an appointment and allows the associated slot to become available through the database trigger.

### Rescheduling

```text
reschedule_appointment()
```

Checks the new slot and moves the appointment to the selected available slot.

---

# Triggers & Audit Logging

ClinicDesk uses a PostgreSQL trigger on the `Appointments` table.

The trigger handles appointment-related changes such as:

```text
Appointment Created
        │
        ▼
Slot marked unavailable
        │
        ▼
Audit log created
```

When an appointment is cancelled:

```text
Appointment Cancelled
        │
        ▼
Slot marked available
        │
        ▼
Audit log created
```

When an appointment is rescheduled, the old slot is released and the new slot is marked unavailable.

---

# Database Roles

The project defines three PostgreSQL roles:

```text
receptionist
doctor
admin
```

Each role receives different permissions over the database tables.

### Receptionist

Can manage patient and appointment information and update slot information.

### Doctor

Has read access to relevant patient, appointment, doctor, slot, and audit information.

### Admin

Has full privileges over the database tables and sequences.

---

# Application Architecture

ClinicDesk uses a layered architecture.

```text
┌─────────────────────────────┐
│         JavaFX UI           │
│                             │
│ Dashboard / Patients        │
│ Doctors / Appointments      │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│       Service Layer         │
│                             │
│ PatientService              │
│ DoctorService               │
│ AppointmentService          │
│ SlotService                 │
│ DashboardService            │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│          DAO Layer          │
│                             │
│ PatientDAO                  │
│ DoctorDAO                   │
│ AppointmentDAO              │
│ SlotDAO                     │
│ DashboardDAO                │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│        PostgreSQL           │
│                             │
│ Tables / Views / Functions  │
│ Triggers / Transactions     │
└─────────────────────────────┘
```

This separates:

* User interface
* Application logic
* Database access
* Database-level logic

---

# Tech Stack

| Technology    | Purpose                         |
| ------------- | ------------------------------- |
| Java 17       | Application development         |
| JavaFX 21.0.1 | Desktop GUI                     |
| PostgreSQL    | Relational database             |
| JDBC          | Java–PostgreSQL connectivity    |
| Maven         | Build and dependency management |
| Docker        | PostgreSQL environment          |
| SQL / PLpgSQL | Database logic                  |

---

# Project Structure

```text
miniproject_dbs/
│
├── data/
│   ├── appointments.csv
│   ├── patients.csv
│   └── slots.csv
│
├── sql/
│   ├── schema.sql
│   ├── schema_updates.sql
│   ├── staging.sql
│   ├── load_production.sql
│   ├── insert_doctors.sql
│   ├── functions.sql
│   ├── transactions.sql
│   ├── triggers.sql
│   ├── views.sql
│   └── roles.sql
│
├── src/
│   └── main/
│       ├── java/
│       │   ├── app/
│       │   ├── controller/
│       │   ├── dao/
│       │   ├── database/
│       │   ├── model/
│       │   ├── service/
│       │   └── utils/
│       │
│       └── resources/
│           └── css/
│
├── docker-compose.yaml
├── pom.xml
└── README.md
```

---

# Data Loading

The project uses CSV files as the source dataset.

The database setup separates raw data loading from the production schema:

```text
CSV files
    │
    ▼
Staging tables
    │
    ▼
Production tables
    │
    ▼
Functions / Views / Triggers
    │
    ▼
JavaFX Application
```

The staging tables are defined in:

```text
sql/staging.sql
```

Production data is loaded using:

```text
sql/load_production.sql
```

Doctor records are inserted using:

```text
sql/insert_doctors.sql
```

---

# Running the Project

## Prerequisites

Install:

* Java 17
* Maven
* Docker Desktop
* Git

---

## 1. Clone the repository

```bash
git clone https://github.com/AnyutaK/ClinicDesk.git
cd ClinicDesk
```

---

## 2. Start PostgreSQL

Start the Docker environment:

```bash
docker compose up -d
```

Check the running containers:

```bash
docker ps
```

The PostgreSQL container is configured as:

```text
clinicdesk-db
```

---

## 3. Set up the database

Execute the SQL scripts against the PostgreSQL database in the appropriate order.

A typical setup sequence is:

```text
schema.sql
      ↓
staging.sql
      ↓
CSV data
      ↓
load_production.sql
      ↓
insert_doctors.sql
      ↓
schema_updates.sql
      ↓
functions.sql
      ↓
views.sql
      ↓
triggers.sql
      ↓
transactions.sql
      ↓
roles.sql
```

The SQL files are provided in the `sql/` directory.

---

## 4. Build the application

```bash
mvn clean compile
```
## 5. Run the application
```bash
mvn javafx:run
```
The JavaFX application starts from:
```text
app.App
```
## Configuration
The application uses environment variables for database configuration, with development defaults defined in `DBConfig`.
The database connection consists of:
```text
JDBC URL
Username
Password
```
## Project Goals
The primary goal of ClinicDesk is to demonstrate how a relational database can be integrated into a complete desktop application.
The project brings together:
* Database design
* SQL
* PostgreSQL
* Java
* JavaFX
* JDBC
* CRUD operations
* Stored functions
* Transactions
* Triggers
* Views
* Constraints
* Indexing
* Database roles
* Docker
rather than treating the database and application as separate components.
## Current Version
**v1.0.0**
This version represents the current project milestone containing the JavaFX application, PostgreSQL database implementation, CRUD functionality, database functions, views, transactions, triggers, and role configuration.
## Author
**Anyuta Kumar**
### License
This project was developed as an academic database management systems project.
