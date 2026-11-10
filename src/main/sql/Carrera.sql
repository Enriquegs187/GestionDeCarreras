CREATE TABLE Carrera (
    cod              VARCHAR(20),
    distancia        NUMERIC(6,2) NOT NULL,
    altitud          NUMERIC(8,2) NOT NULL,
    inicio           VARCHAR(100) NOT NULL,
    fin              VARCHAR(100) NOT NULL,
    categoriaSexo    VARCHAR(20) NOT NULL,
    codCompeticion   VARCHAR(20) NOT NULL,
    precio           NUMERIC(6,2) NOT NULL,

    CONSTRAINT cp_carrera PRIMARY KEY (cod),
    CONSTRAINT calt_carrera FOREIGN KEY(codCompeticion)
            REFERENCES Competicion(cod)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_distancia_positiva CHECK (distancia > 0),
    CONSTRAINT chk_altitud_no_negativa CHECK (altitud >= 0),
    CONSTRAINT chk_precio_carrera_no_negativo CHECK (precio >= 0),
    CONSTRAINT chk_tiposexo_valido CHECK (categoriaSexo IN ('Masculino', 'Femenino', 'Mixto'))
);