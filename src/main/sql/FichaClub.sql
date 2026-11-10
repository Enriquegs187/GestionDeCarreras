CREATE TABLE FichaClub (
    id              VARCHAR(20),
    validezFecha    DATE NOT NULL,
    dniParticipante VARCHAR(20) NOT NULL,
    cifClub         VARCHAR(20) NOT NULL,

    CONSTRAINT cp_fichaclub PRIMARY KEY (id),
    CONSTRAINT calt_fichaclub_participante FOREIGN KEY (dniParticipante)
            REFERENCES Participante(dni)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT calt_fichaclub_club  FOREIGN KEY (cifClub)
            REFERENCES Club(cif)
            ON DELETE RESTRICT ON UPDATE CASCADE
);