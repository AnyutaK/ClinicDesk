-- Remove old roles if they exist
DROP ROLE IF EXISTS receptionist;
DROP ROLE IF EXISTS doctor;
DROP ROLE IF EXISTS admin;

-- Create roles
CREATE ROLE receptionist LOGIN PASSWORD 'reception123';

CREATE ROLE doctor LOGIN PASSWORD 'doctor123';

CREATE ROLE admin LOGIN PASSWORD 'admin123';

REVOKE ALL ON ALL TABLES IN SCHEMA public FROM PUBLIC;

GRANT SELECT, INSERT, UPDATE
ON Patients
TO receptionist;

GRANT SELECT, INSERT, UPDATE
ON Appointments
TO receptionist;

GRANT SELECT
ON Doctors
TO receptionist;

GRANT SELECT, UPDATE
ON Slots
TO receptionist;

GRANT INSERT
ON Audit_Log
TO receptionist;

GRANT SELECT
ON Patients
TO doctor;

GRANT SELECT
ON Appointments
TO doctor;

GRANT SELECT
ON Doctors
TO doctor;

GRANT SELECT
ON Slots
TO doctor;

GRANT SELECT
ON Audit_Log
TO doctor;

GRANT ALL PRIVILEGES
ON ALL TABLES IN SCHEMA public
TO admin;

GRANT ALL PRIVILEGES
ON ALL SEQUENCES IN SCHEMA public
TO admin;