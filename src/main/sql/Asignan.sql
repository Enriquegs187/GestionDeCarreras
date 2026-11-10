CREATE TABLE Asignan (
    dniVoluntario   VARCHAR(20) NOT NULL,
    codPuntoControl VARCHAR(20) NOT NULL,

    CONSTRAINT pk_asignan PRIMARY KEY (dniVoluntario, codPuntoControl),

    CONSTRAINT calt_asignan_voluntario FOREIGN KEY (dniVoluntario)
            REFERENCES Voluntario(dni)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT calt_asignan_puntocontrol  FOREIGN KEY (codPuntoControl)
            REFERENCES PuntoControl(cod)
            ON DELETE CASCADE ON UPDATE CASCADE
);