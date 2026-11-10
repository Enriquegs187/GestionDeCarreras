CREATE TABLE Sponsor (
    cod     VARCHAR(20),
    nombre  VARCHAR(100) NOT NULL,

    CONSTRAINT cp_sponsor PRIMARY KEY (cod),
    CONSTRAINT uq_sponsor_nombre UNIQUE (nombre)
);