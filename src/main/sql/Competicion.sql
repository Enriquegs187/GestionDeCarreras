CREATE TABLE Competicion (
    cod                 VARCHAR(20),
    nombre              VARCHAR(50) NOT NULL,
    localizacion        VARCHAR(100) NOT NULL,
    fechaInicio         DATE NOT NULL,
    fechaFin            DATE NOT NULL,
    tasaDeRegistro      NUMERIC(10,2) NOT NULL,
    dniPromotor         VARCHAR(20) NOT NULL,

    CONSTRAINT cp_competicion PRIMARY KEY (cod),
    CONSTRAINT calt_competicion FOREIGN KEY (dniPromotor)
            REFERENCES Promotor(dni)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_fechas_competicion CHECK (fechaFin > fechaInicio),
    CONSTRAINT chk_tasa_registro_no_negativa CHECK (tasaDeRegistro >= 0)
);
