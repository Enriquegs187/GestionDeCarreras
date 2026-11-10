CREATE TABLE Participante (
    dni             VARCHAR(20),
    nombre          VARCHAR(20) NOT NULL,
    apellido        VARCHAR(20) NOT NULL,
    sexo            VARCHAR(20) NOT NULL,
    fechaNacimiento DATE NOT NULL,
    email           VARCHAR(100) NOT NULL,
    metodoDePago    VARCHAR(50) NOT NULL,
    clubcif         VARCHAR(20),

    CONSTRAINT cp_participante PRIMARY KEY (dni),
    CONSTRAINT fk_club FOREIGN KEY (clubcif)
            REFERENCES club(cif)
            ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_participante_usuario FOREIGN KEY (dni)
            REFERENCES Usuario (username)
            ON DELETE RESTRICT ON UPDATE CASCADE

    CONSTRAINT chk_fecha_nacimiento_valida CHECK (fechaNacimiento <= CURRENT_DATE AND fechaNacimiento > '1900-01-01'),
    CONSTRAINT chk_sexo_valido CHECK (sexo IN ('Masculino','Femenino'))

);