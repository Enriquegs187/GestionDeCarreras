CREATE TABLE Inscripcion (
    cod              VARCHAR(20),
    tiempoMeta       TIME,
    codCarrera       VARCHAR(20) NOT NULL,
    dniParticipante  VARCHAR(20) NOT NULL,
    dorsal 	         VARCHAR(20) NOT NULL,


    CONSTRAINT cp_inscripcion PRIMARY KEY (cod),
    CONSTRAINT calt_inscripcion_carrera FOREIGN KEY (codCarrera)
            REFERENCES Carrera(cod)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT calt_inscripcion_participante FOREIGN KEY (dniParticipante)
            REFERENCES Participante(dni)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_tiempo_meta_positivo CHECK (tiempoMeta >= '00:00:00')
);