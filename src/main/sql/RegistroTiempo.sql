CREATE TABLE RegistroTiempo (
    cod             VARCHAR(20),
    tiempo          TIME NOT NULL,
    comentarios     VARCHAR(200),
    codPuntoControl VARCHAR(20) NOT NULL,
    codInscripcion  VARCHAR(20) NOT NULL,

    CONSTRAINT cp_registrotiempo PRIMARY KEY (cod),
    CONSTRAINT calt_registrotiempo_puntocontrol FOREIGN KEY (codPuntoControl)
            REFERENCES PuntoControl(cod)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT calt_registrotiempo_inscripcion FOREIGN KEY (codInscripcion)
            REFERENCES Inscripcion(cod)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_tiempo_registro_positivo CHECK (tiempo >= '00:00:00')
);