CREATE TABLE Aportacion (
    codCompeticion  VARCHAR(50),
    codSponsor      VARCHAR(50),

    CONSTRAINT cp_aportacion PRIMARY KEY (codCompeticion, codSponsor),
    CONSTRAINT calt_aportacion_competicion FOREIGN KEY (codCompeticion)
            REFERENCES Competicion(cod)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT calt_aportacion_sponsor FOREIGN KEY (codSponsor)
            REFERENCES Sponsor(cod)
            ON DELETE RESTRICT ON UPDATE CASCADE
);