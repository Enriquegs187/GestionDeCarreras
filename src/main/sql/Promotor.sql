CREATE TABLE Promotor (
    dni                 VARCHAR(20),
    nombre              VARCHAR(50) NOT NULL,
    nombreOrganizacion  VARCHAR(100),
    email               VARCHAR(100) NOT NULL,
    telefono            VARCHAR(20) NOT NULL,

    CONSTRAINT cp_promotor PRIMARY KEY (dni),
    CONSTRAINT fk_promotor_usuario FOREIGN KEY (dni)
            REFERENCES Usuario (username)
            ON DELETE RESTRICT ON UPDATE CASCADE
);