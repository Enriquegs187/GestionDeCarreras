CREATE TABLE SolicitudPrevia (
    id                  VARCHAR(20),
    nombre              VARCHAR(50) NOT NULL,
    estadoSolicitud     BOOLEAN NOT NULL,
    fechaAprobacion     DATE,
    motivo              VARCHAR(100),
    personaContacto     VARCHAR(100),
    mailEmpresaContacto VARCHAR(100),
    dniPromotor         VARCHAR(20) NOT NULL,

    CONSTRAINT cp_solicitudprevia PRIMARY KEY (id),
    CONSTRAINT calt_solicitudprevia FOREIGN KEY (dniPromotor)
            REFERENCES Promotor(dni)
    ON DELETE RESTRICT ON UPDATE CASCADE
);
