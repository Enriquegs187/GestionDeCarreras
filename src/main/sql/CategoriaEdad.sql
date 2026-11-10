CREATE TABLE CategoriaEdad (
    cod VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    edadMinima INT NOT NULL,
    edadMaxima INT, -- Puede ser NULL para Veterano
    CONSTRAINT chk_edad_minima_valida CHECK (edadMinima >= 0),
    CONSTRAINT chk_edad_maxima_valida CHECK (edadMaxima IS NULL OR edadMaxima >= edadMinima)
);