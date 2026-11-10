CREATE TABLE EmpresaExterna(
    cif         VARCHAR(20),
    nombre      VARCHAR(50) NOT NULL,
    dniPromotor VARCHAR(20) NOT NULL,

    CONSTRAINT cp_empresaexterna PRIMARY KEY (cif),

    CONSTRAINT calt_empresaexterna_promotor FOREIGN KEY (dniPromotor)
            REFERENCES Promotor(dni)
            ON DELETE RESTRICT ON UPDATE CASCADE
);