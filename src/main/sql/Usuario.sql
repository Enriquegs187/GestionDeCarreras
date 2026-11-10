CREATE TABLE Usuario (
    username VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL CHECK (tipo_usuario IN ('promotor', 'participante')),

    CONSTRAINT cp_usuario PRIMARY KEY (username)

);