CREATE TABLE Voluntario (
    dni             VARCHAR(20),
    nombre          VARCHAR(20) NOT NULL,
    apellido        VARCHAR(20) NOT NULL,
    email           VARCHAR(50) NOT NULL,
    disponibilidad  VARCHAR(100) NOT NULL,

    CONSTRAINT cp_voluntario PRIMARY KEY (dni)
);