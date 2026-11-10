CREATE TABLE Club (
    cif         VARCHAR(20),
    nombre      VARCHAR(50) NOT NULL,
    dniPromotor VARCHAR(20) NOT NULL,

    CONSTRAINT cp_club PRIMARY KEY (cif),

    CONSTRAINT calt_club_promotor FOREIGN KEY (dniPromotor)
            REFERENCES Promotor(dni)
            ON DELETE RESTRICT ON UPDATE CASCADE
);