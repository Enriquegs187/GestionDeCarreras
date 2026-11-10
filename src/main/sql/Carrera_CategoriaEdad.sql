CREATE TABLE Carrera_CategoriaEdad (
    codCarrera VARCHAR(20),
    codCategoriaEdad VARCHAR(20),

    CONSTRAINT pk_carrera_categoria_edad PRIMARY KEY (codCarrera, codCategoriaEdad),
    CONSTRAINT fk_cce_carrera FOREIGN KEY (codCarrera)
            REFERENCES Carrera(cod)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cce_categoria_edad FOREIGN KEY (codCategoriaEdad)
            REFERENCES CategoriaEdad(cod)
            ON DELETE RESTRICT ON UPDATE CASCADE
);
