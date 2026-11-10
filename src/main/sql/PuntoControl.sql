CREATE TABLE PuntoControl (
    cod             VARCHAR(20),
    localizacion    VARCHAR(100) NOT NULL,
    codCarrera      VARCHAR(20) NOT NULL,

    CONSTRAINT cp_puntocontrol PRIMARY KEY (cod),
    CONSTRAINT fk_puntocontrol_carrera FOREIGN KEY (codCarrera)
            REFERENCES Carrera(cod)
            ON DELETE CASCADE ON UPDATE CASCADE
);
