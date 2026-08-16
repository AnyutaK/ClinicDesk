# ClinicDesk
ClinicDesk is a database-driven desktop clinic management system built using Java, JavaFX, and PostgreSQL.
The project combines a JavaFX frontend with a PostgreSQL relational database to manage patients, doctors, appointments, and scheduling slots. It was developed as a Database Management Systems project with a focus on applying database concepts within a complete desktop application.
The project demonstrates relational database design, constraints, indexing, views, stored functions, transactions, triggers, audit logging, role-based permissions, JDBC connectivity, Docker-based database deployment, and layered application architecture.

## Overview
ClinicDesk is designed around the core operations of a clinic, where patient records, doctors, appointments, and scheduling information need to be stored and managed reliably.
Rather than implementing all logic inside the Java application, responsibilities are distributed between the JavaFX application and PostgreSQL.
```text
┌─────────────────────────────────────────────┐
│              ClinicDesk Application         │
│                                             │
│ Dashboard | Patients | Doctors | Appointments │
└───────────────────────┬─────────────────────┘
                        │
                       JDBC
                        │
                        ▼
┌─────────────────────────────────────────────┐
│                 PostgreSQL                  │
│                                             │
│ Tables | Views | Functions | Transactions   │
│ Constraints | Triggers | Audit | Roles      │
└─────────────────────────────────────────────┘
```
This allows the project to demonstrate both application development and database-level implementation.
## Project Objectives
The project was developed with the following objectives:
* Design a relational database for clinic management
* Maintain data integrity using database constraints
* Implement CRUD operations through a Java application
* Connect Java to PostgreSQL using JDBC
* Separate database access from application logic
* Use PostgreSQL functions for reusable database operations
* Implement transaction-based appointment operations
* Automatically maintain slot availability using triggers
* Maintain an audit trail of appointment operations
* Create database views for reporting and analytics
* Implement database roles with different permissions
* Containerize PostgreSQL using Docker
* Build a functional desktop interface using JavaFX
## Features
### Dashboard
The dashboard provides an overview of clinic activity, including:
* Today's appointments
* Patients waiting
* Doctors on duty
* Slot utilization
* Upcoming appointments
* Quick actions for common clinic operations
Dashboard statistics are retrieved from PostgreSQL through the DAO and service layers.
### Patient Management
ClinicDesk provides functionality for managing patient records.
* Search patients
* View patient information
* Add new patients
* Update patient information
* Delete patient records
* Search using database functions
Patient records include:
* Patient ID
* Name
* Sex
* Date of birth
* Insurance
* Phone
* Email
* Creation timestamp
### Doctor Management
The doctor management module provides functionality for maintaining doctor information.
* Search doctors
* View doctor information
* Add doctors
* Update doctor information
* Delete doctors
* Organize doctors by department
* Organize doctors by specialization
Doctor records include:
* Doctor ID
* Doctor name
* Department
* Specialization
* Phone
* Email
* Creation timestamp
### Appointment Management
ClinicDesk supports appointment CRUD operations.
* Search appointments
* Create appointments
* View appointment details
* Edit appointments
* Delete appointments
* Assign patients to available slots
* Assign appointments to doctors
* Track appointment status
* Reschedule appointments
* Cancel appointments
Appointments connect patients with scheduling slots.
```text
Patient
   |
   v
Appointment
   |
   v
Slot
   |
   v
Doctor
```
### Slot Management
The system maintains doctor scheduling slots and their availability.
* View available slots
* Find slots by doctor
* Associate slots with appointments
* Track whether a slot is available or booked
Slot availability is maintained at the database level through appointment operations and triggers.
## Architecture
ClinicDesk follows a layered architecture that separates the user interface, application logic, database access, and database implementation.
```text
┌─────────────────────────────┐
│         JavaFX UI           │
│                             │
│ Dashboard / Patients        │
│ Doctors / Appointments      │
└──────────────┬──────────────┘
               |
               v
┌─────────────────────────────┐
│       Service Layer         │
│                             │
│ PatientService              │
│ DoctorService               │
│ AppointmentService          │
│ SlotService                 │
│ DashboardService            │
└──────────────┬──────────────┘
               |
               v
┌─────────────────────────────┐
│          DAO Layer          │
│                             │
│ PatientDAO                  │
│ DoctorDAO                   │
│ AppointmentDAO              │
│ SlotDAO                     │
│ DashboardDAO                │
└──────────────┬──────────────┘
               |
               v
┌─────────────────────────────┐
│        PostgreSQL           │
│                             │
│ Tables / Views / Functions  │
│ Triggers / Transactions     │
└─────────────────────────────┘
```
#### Presentation Layer
The JavaFX interface handles:
* Navigation
* User interaction
* Forms
* Tables
* Dashboard components
* Data presentation
#### Service Layer
The service layer handles application-level operations between the UI and database access layer.
Examples include:
```text
PatientService
DoctorService
AppointmentService
SlotService
DashboardService
```
#### DAO Layer
The DAO layer handles database communication through JDBC.
Examples include:
```text
PatientDAO
DoctorDAO
AppointmentDAO
SlotDAO
DashboardDAO
```
#### Database Layer
PostgreSQL handles:
* Persistent data
* Constraints
* Functions
* Views
* Transactions
* Triggers
* Audit logging
* Roles and permissions
### Request Flow
A typical operation follows this flow:
```text
User Action
    |
    v
JavaFX Page
    |
    v
Service
    |
    v
DAO
    |
    v
JDBC
    |
    v
PostgreSQL
    |
    v
Query / Function / View
    |
    v
Result
    |
    v
DAO
    |
    v
Service
    |
    v
JavaFX UI
```
For example, a patient search can follow:
```text
Patient Search
      |
      v
Patients Page
      |
      v
PatientService
      |
      v
PatientDAO
      |
      v
JDBC
      |
      v
PostgreSQL
      |
      v
search_patients()
      |
      v
Search Results
```
## Database
ClinicDesk uses PostgreSQL as its relational database.
The main entities are:
```text
Patients
    |
    v
Appointments -------> Slots
                       |
                       v
                    Doctors
```

An additional `Audit_Log` table records appointment-related actions.
### Database Schema
#### Patients
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
#### Doctors
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
#### Slots
Stores doctor scheduling slots.
```text
slot_id
doctor_id
appointment_date
appointment_time
is_available
```
#### Appointments
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
#### Audit Log
Records appointment-related actions.
```text
log_id
appointment_id
action
performed_at
```
### Relationships
The database uses foreign keys to maintain relationships between the major entities.
```text
Patients
   |
   | patient_id
   v
Appointments
   |
   | slot_id
   v
Slots
   |
   | doctor_id
   v
Doctors
```
This maintains referential integrity between patients, appointments, slots, and doctors.
## Database Features
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
These constraints prevent invalid records from being inserted into the database.
### Indexes
Indexes are created for commonly queried fields, including:
```text
Patients.name
Slots.appointment_date
Slots.doctor_id
Appointments.patient_id
Appointments.slot_id
```
These support frequently used operations such as patient searches, appointment lookups, doctor scheduling, and slot availability queries.
### Views
ClinicDesk uses PostgreSQL views for reusable reporting and analytics queries.
Available views include:
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
### Stored Functions
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
These functions allow reusable data processing and analytical logic to be implemented directly within PostgreSQL.
### Transactions
Appointment operations are implemented using PostgreSQL functions and transaction-oriented database logic.
#### Booking
```text
book_appointment()
```
The booking process checks:
1. Whether the patient exists
2. Whether the requested slot exists
3. Whether the slot is available
4. Whether the appointment can be created
#### Cancellation
```text
cancel_appointment()
```
Cancels an appointment and allows the associated slot to become available.
#### Rescheduling
```text
reschedule_appointment()
```
Checks the new slot and moves the appointment to the selected available slot.
These operations help keep appointment and slot information consistent.
### Triggers and Audit Logging
ClinicDesk uses PostgreSQL triggers to automatically react to appointment changes.
#### Appointment Creation
```text
Appointment Created
        |
        v
Slot marked unavailable
        |
        v
Audit log created
```
#### Appointment Cancellation
```text
Appointment Cancelled
        |
        v
Slot marked available
        |
        v
Audit log created
```
#### Appointment Rescheduling
```text
Appointment Rescheduled
        |
        +----> Old slot released
        |
        +----> New slot marked unavailable
        |
        +----> Audit information recorded
```
This allows related database state to be maintained automatically rather than requiring every change to be manually handled by the JavaFX interface.
### Database Roles
The project defines three PostgreSQL roles:
```text
receptionist
doctor
admin
```
Each role receives different permissions over database objects.
#### Receptionist
Can manage patient and appointment information and update slot information.
#### Doctor
Has read access to relevant:
* Patient information
* Appointment information
* Doctor information
* Slot information
* Audit information
#### Admin
Has full privileges over the database tables and sequences.
This demonstrates database-level access control in addition to application-level logic.
## Data Pipeline
The project uses CSV files as the source dataset.
The database setup separates raw data loading from the production schema.
```text
CSV files
    |
    v
Staging tables
    |
    v
Production tables
    |
    v
Functions / Views / Triggers
    |
    v
JavaFX Application
```
Staging tables are defined in:
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
This separates the data-import process from the application itself.
## Tech Stack

| Technology    | Purpose                         |
| ------------- | ------------------------------- |
| Java 17       | Application development         |
| JavaFX 21.0.1 | Desktop GUI                     |
| PostgreSQL    | Relational database             |
| JDBC          | Java–PostgreSQL connectivity    |
| Maven         | Build and dependency management |
| Docker        | PostgreSQL environment          |
| SQL / PLpgSQL | Database logic                  |

### Docker
PostgreSQL is run inside Docker to provide a consistent database environment.
The configured PostgreSQL container is:
```text
clinicdesk-db
```
Start the database with:
```bash
docker compose up -d
```
Check the running containers with:
```bash
docker ps
```
## Project Structure
```text
miniproject_dbs/
|
├── data/
│   ├── appointments.csv
│   ├── patients.csv
│   └── slots.csv
|
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
|
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
|
├── docker-compose.yaml
├── pom.xml
└── README.md
```
## Getting Started
### Prerequisites
Install:
* Java 17
* Maven
* Docker Desktop
* Git
### 1. Clone the Repository
```bash
git clone https://github.com/AnyutaK/ClinicDesk.git
cd ClinicDesk
```
### 2. Start PostgreSQL
```bash
docker compose up -d
```
Verify that the container is running:
```bash
docker ps
```
### 3. Set Up the Database
Execute the SQL scripts against the PostgreSQL database in the appropriate order.
A typical setup sequence is:
```text
schema.sql
      |
      v
staging.sql
      |
      v
CSV data
      |
      v
load_production.sql
      |
      v
insert_doctors.sql
      |
      v
schema_updates.sql
      |
      v
functions.sql
      |
      v
views.sql
      |
      v
triggers.sql
      |
      v
transactions.sql
      |
      v
roles.sql
```

The SQL files are provided in the `sql/` directory.
### 4. Build the Application
```bash
mvn clean compile
```
### 5. Run the Application
```bash
mvn javafx:run
```
The JavaFX application starts from:
```text
app.App
```
### Configuration
Database connection settings are defined in `DBConfig`.
The application requires:
```text
JDBC URL
Username
Password
```
For a public deployment, use environment-specific credentials rather than production credentials.
## Roadmap
### Version 1.0 — Current
Completed:
* [x] Relational database schema
* [x] Patient management
* [x] Doctor management
* [x] Appointment management
* [x] Slot management
* [x] Dashboard
* [x] CRUD operations
* [x] Database constraints
* [x] Indexes
* [x] PostgreSQL functions
* [x] Database views
* [x] Transactions
* [x] Triggers
* [x] Audit logging
* [x] Database roles
* [x] JDBC integration
* [x] DAO layer
* [x] Service layer
* [x] Docker-based PostgreSQL environment
* [x] CSV data-loading pipeline
### Version 1.1 — Application Refinement
Potential improvements:
* [ ] Advanced patient and appointment search
* [ ] More detailed filtering
* [ ] Improved form validation
* [ ] Better error messages
* [ ] Expanded dashboard statistics
* [ ] More detailed appointment status handling
* [ ] Improved UI consistency
* [ ] Additional database validation
### Version 2.0 — Expanded Clinic Management
Potential future features:
* [ ] Application-level authentication
* [ ] Role-specific application access
* [ ] Role-specific dashboards
* [ ] Calendar-based appointment scheduling
* [ ] Doctor availability management
* [ ] Patient medical history
* [ ] Prescription management
* [ ] Billing management
* [ ] Appointment reminders
* [ ] Expanded reporting
* [ ] Report export
* [ ] Advanced analytics and visualizations
## Challenges and What I Learned
ClinicDesk was developed incrementally, and several of the most difficult parts came from integrating the database, backend logic, and JavaFX interface rather than implementing each component independently.
### 1. Recovering and rebuilding the database environment
One of the most difficult parts of the project was dealing with the PostgreSQL database while the schema and database logic were still evolving.
At one point, the database had to be rebuilt after changes to the Docker environment and database state. This required making sure that the schema, seed data, functions, views, triggers, transactions, and roles could all be recreated in the correct order.
The problem was not simply starting PostgreSQL again. A working application depended on a specific collection of database objects and data being present.
This led to a more structured database setup process:
```text
Docker PostgreSQL
       |
       v
Schema
       |
       v
Staging / Data Loading
       |
       v
Production Data
       |
       v
Schema Updates
       |
       v
Functions
       |
       v
Views
       |
       v
Triggers
       |
       v
Transactions
       |
       v
Roles
````
#### What I learned
* Database state should not be treated as something that exists only on one machine.
* SQL initialization scripts need to have a clear dependency order.
* Docker makes rebuilding the environment easier, but only if the database setup itself is reproducible.
* Database changes should be tracked carefully alongside application changes.
### 2. Debugging appointment status and editing logic
Appointment management became one of the more complicated parts of the application because an appointment is connected to a patient, a doctor through a slot, a scheduled time, and an appointment status.
While implementing appointment editing and status handling, changes to one part of the workflow could affect the others.
For example, editing an appointment could involve:
```text
Appointment
    |
    +-- Patient
    |
    +-- Slot
    |     |
    |     +-- Doctor
    |     +-- Date
    |     +-- Time
    |     +-- Availability
    |
    +-- Status
```
This became particularly important when implementing appointment cancellation and rescheduling, because the slot associated with the appointment also needed to remain consistent.
#### What I learned
* CRUD operations become more complicated when records participate in multiple relationships.
* Appointment state and slot availability should not be treated as completely independent pieces of data.
* Database transactions and triggers are useful when one operation affects several related records.
* Debugging an incorrect appointment state often requires checking the database relationships rather than only the JavaFX screen.
### 3. Replacing placeholder dashboard data with database-driven statistics
The dashboard initially contained values that were useful for building the interface, but the final application needed those values to come from the actual database.
This meant replacing static values with database queries and functions for information such as:
* Today's appointments
* Patients waiting
* Doctors on duty
* Slot utilization
* Upcoming appointments
This exposed problems that were not visible while the dashboard was using placeholder data.
One example was the slot-utilization query, where a PostgreSQL syntax error appeared during integration. The issue had to be traced from the dashboard through the DAO and into the SQL being executed.
The resulting flow became:
```text
Dashboard
    |
    v
DashboardService
    |
    v
DashboardDAO
    |
    v
PostgreSQL Query / Function
    |
    v
Database Result
    |
    v
Dashboard
```
#### What I learned
* A UI can appear completely functional while its underlying data layer is still incorrect.
* Database queries need to be tested independently before being integrated into the UI.
* Debugging is much easier when the request path can be traced layer by layer.
### 4. Debugging JavaFX compilation and integration errors
As the JavaFX frontend grew, some changes caused compilation and integration problems that were not related to the database itself.
For example, changes to the dashboard page resulted in a Maven compilation error:
```text
class, interface, enum, or record expected
```
The error pointed to `DashboardPage.java`, requiring the source structure to be checked rather than assuming that the problem was in the database or Maven configuration.
Other UI features also depended on the correct interaction between:
```text
JavaFX Page
     |
     v
Controller
     |
     v
Service
     |
     v
DAO
     |
     v
PostgreSQL
```
#### What I learned
* Compilation errors can originate from small structural mistakes in a large Java class.
* Debugging should begin with the actual compiler error rather than changing unrelated components.
* Separating UI, service, and DAO responsibilities makes it easier to identify where a problem originates.

### 5. Debugging data-loading and database dependencies
ClinicDesk uses CSV data as part of its database setup, which introduced another dependency chain.
The application could not simply be run against an empty PostgreSQL database. The required tables, relationships, data, functions, views, and triggers all had to exist in the expected state.
This made the distinction between:
```text
Raw Data
   |
   v
Staging
   |
   v
Production Tables
   |
   v
Database Logic
   |
   v
Application
```
important during debugging.
When something appeared incorrect in the application, the problem could originate from the original dataset, the loading process, the production schema, or the application query.
#### What I learned
* Data pipelines are part of the application environment.
* When debugging database-driven applications, the source data and loading process also need to be considered.
* Separating staging data from production tables makes the loading process easier to reason about.
### 6. Keeping the database and Java application synchronized
One of the recurring challenges throughout development was that the database and Java application evolved together.
A database change could require corresponding changes to:
```text
SQL Schema
    |
    v
Model
    |
    v
DAO
    |
    v
Service
    |
    v
Controller
    |
    v
JavaFX UI
```
This became especially noticeable when implementing new appointment functionality and dashboard operations.
A feature was not considered complete simply because the SQL worked or the JavaFX interface appeared correctly. The entire path had to work together.
#### What I learned
* Database-driven applications require coordination between multiple layers.
* Changes should be made incrementally and tested across the complete request path.
* A layered architecture reduces the risk of mixing UI code with database logic.
### 7. Managing Git and project state during development
Because the project evolved through multiple stages, keeping track of working versions became important.
The repository contains milestones corresponding to major development stages, including the backend, frontend, appointment fixes, and the final `v1.0.0` milestone
This provided useful checkpoints when debugging newer functionality or considering whether to revert changes.
#### What I learned
* Version control is useful not only for collaboration but also for safely experimenting.
* Meaningful commits provide recovery points during debugging.
* Tagging stable milestones makes it easier to identify known-working versions of a project.
## What This Project Taught Me
The biggest lesson from ClinicDesk was that building a database application is an integration problem as much as it is a programming problem.
The most useful skills I developed through the project were:
* Tracing bugs across the JavaFX, service, DAO, JDBC, and PostgreSQL layers
* Designing database logic around relationships and data integrity
* Understanding how transactions and triggers affect application behavior
* Rebuilding a database environment using Docker and SQL scripts
* Debugging SQL independently from the Java application
* Managing changes across a multi-layer application
* Using Git history and stable milestones to recover from problematic changes
The project also made the distinction between **"the feature exists"** and **"the complete feature works end-to-end"** much clearer.
A button, query, function, or database table can work individually while the overall feature is still broken. ClinicDesk required repeatedly tracing those pieces together until the entire path worked.
## Current Status
**Version 1.0.0**
ClinicDesk currently provides a functional JavaFX desktop application backed by PostgreSQL.
The current version includes:
* Patient management
* Doctor management
* Appointment management
* Slot management
* Dashboard functionality
* PostgreSQL database integration
* Database functions
* Views
* Transactions
* Triggers
* Audit logging
* Database roles
* Docker-based database setup
* DAO and service layers
Future development is focused on application-level authentication, richer scheduling, analytics, reporting, testing, and UI refinement.

## Author
**Anyuta Kumar**
