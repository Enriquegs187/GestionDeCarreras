package es.uji.ei1027.project.service;

import es.uji.ei1027.project.dao.*;
import es.uji.ei1027.project.model.Participante;
import es.uji.ei1027.project.model.Carrera;
import es.uji.ei1027.project.model.CategoriaEdad;
import es.uji.ei1027.project.model.Competicion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class InscripcionService {

    // Logger para la depuración del código
    private static final Logger logger = LoggerFactory.getLogger(InscripcionService.class);

    private InscripcionDao inscripcionDao;
    private ParticipanteDao participanteDao;
    private CarreraDao carreraDao;
    private CarreraCategoriaEdadDao carreraCategoriaEdadDao;
    private CompeticionDao competicionDao;

    @Autowired
    public void setInscripcionDao(InscripcionDao inscripcionDao) {
        this.inscripcionDao = inscripcionDao;
    }
    @Autowired
    public void setParticipanteDao(ParticipanteDao participanteDao) {
        this.participanteDao = participanteDao;
    }
    @Autowired
    public void setCarreraDao(CarreraDao carreraDao) {
        this.carreraDao = carreraDao;
    }
    @Autowired
    public void setCarreraCategoriaEdadDao(CarreraCategoriaEdadDao carreraCategoriaEdadDao) {
        this.carreraCategoriaEdadDao = carreraCategoriaEdadDao;
    }
    @Autowired
    public void setCompeticionDao(CompeticionDao competicionDao) {
        this.competicionDao = competicionDao;
    }

    public static class InscripcionData {
        private String codCategoriaEdadAsignada;
        private String sexoParticipanteInscripcion;

        public InscripcionData(String codCategoriaEdadAsignada, String sexoParticipanteInscripcion) {
            this.codCategoriaEdadAsignada = codCategoriaEdadAsignada;
            this.sexoParticipanteInscripcion = sexoParticipanteInscripcion;
        }

        public String getCodCategoriaEdadAsignada() { return codCategoriaEdadAsignada; }
        public String getSexoParticipanteInscripcion() { return sexoParticipanteInscripcion; }
    }

    public InscripcionData checkEligibility(String dniParticipante, String codCarrera) {
        logger.debug("Iniciando checkEligibility para DNI: {} y CodCarrera: {}", dniParticipante, codCarrera);

        // --- 1. Obtener Participante ---
        Participante participante = participanteDao.getParticipante(dniParticipante);
        if (participante == null) {
            logger.warn("Participante con DNI {} no encontrado.", dniParticipante);
            throw new IllegalArgumentException("Participante no encontrado.");
        }
        logger.debug("Participante encontrado: DNI {}, Sexo: {}, FechaNacimiento: {}",
                participante.getDni(), participante.getSexo(), participante.getFechaNacimiento());

        // --- 2. Obtener Carrera ---
        Carrera carrera = carreraDao.getCarrera(codCarrera);
        if (carrera == null) {
            logger.warn("Carrera con CodCarrera {} no encontrada.", codCarrera);
            throw new IllegalArgumentException("Carrera no encontrada.");
        }
        logger.debug("Carrera encontrada: Cod {}, Sexo: {}, CodCompeticion: {}",
                carrera.getCod(), carrera.getCategoriaSexo(), carrera.getCodCompeticion());

        // --- 3. Verificación: ¿Ya está inscrito? ---
        boolean yaInscrito = inscripcionDao.getInscripcion().stream()
                .anyMatch(i -> i.getCodCarrera().equals(codCarrera) && i.getDniParticipante().equals(dniParticipante));
        if (yaInscrito) {
            logger.info("El participante {} ya está inscrito en la carrera {}.", dniParticipante, codCarrera);
            throw new IllegalArgumentException("Ya estás inscrito en esta carrera.");
        }
        logger.debug("El participante {} no está inscrito previamente en la carrera {}.", dniParticipante, codCarrera);

        // --- 4. Verificación: Sexo del Participante ---
        if (!"Mixto".equalsIgnoreCase(carrera.getCategoriaSexo())) {
            logger.debug("La carrera {} no es mixta. Sexo requerido: {}", codCarrera, carrera.getCategoriaSexo());
            if (!carrera.getCategoriaSexo().equalsIgnoreCase(participante.getSexo())) {
                logger.info("Participante {} (Sexo: {}) no cumple con el requisito de sexo de la carrera {} (Sexo requerido: {}).",
                        dniParticipante, participante.getSexo(), codCarrera, carrera.getCategoriaSexo());
                throw new IllegalArgumentException("Esta carrera es solo para participantes " + carrera.getCategoriaSexo().toLowerCase() + "s.");
            }
        }
        logger.debug("El participante {} cumple el requisito de sexo para la carrera {}.", dniParticipante, codCarrera);

        // --- 5. Verificación: Edad del Participante ---
        Competicion competicion = competicionDao.getCompeticion(carrera.getCodCompeticion());
        if (competicion == null || competicion.getFechaInicio() == null) {
            logger.error("Error de configuración: Competición {} o su fecha de inicio no definidas.", carrera.getCodCompeticion());
            throw new IllegalArgumentException("No se pudo verificar la edad: La competición o su fecha de inicio no están definidas.");
        }
        if (participante.getFechaNacimiento() == null) {
            logger.error("Error de datos: Fecha de nacimiento del participante {} no registrada.", dniParticipante);
            throw new IllegalArgumentException("No se pudo verificar la edad: Fecha de nacimiento del participante no registrada.");
        }
        logger.debug("Se ha obtenido correctamente la competición {} y la fecha de nacimiento {}.", competicion.getCod(), participante.getFechaNacimiento());

        //Obtener edad del Participante
        LocalDate fechaNacimientoParticipante = participante.getFechaNacimiento();
        LocalDate fechaInicioCompeticion = competicion.getFechaInicio();

        int edadParticipante = Period.between(fechaNacimientoParticipante, fechaInicioCompeticion).getYears();
        logger.debug("Edad del participante {} calculada a la fecha de inicio de la competición {}: {} años.",
                dniParticipante, fechaInicioCompeticion, edadParticipante);

        // Obtener las categorías de edad permitidas para esta carrera
        List<CategoriaEdad> categoriasPermitidasCarrera = carreraCategoriaEdadDao.getCategoriasByCarrera(codCarrera);
        logger.debug("Categorías de edad permitidas para la carrera {}: {}", codCarrera, categoriasPermitidasCarrera);

        String codCategoriaEdadAsignada = null;
        boolean elegiblePorEdad = false;

        if (categoriasPermitidasCarrera == null || categoriasPermitidasCarrera.isEmpty()) {
            logger.debug("La carrera {} no tiene categorías de edad asignadas. Se asume que cualquier edad es válida.", codCarrera);
            elegiblePorEdad = true; // Si no hay restricciones de edad específicas
        } else {
            for (CategoriaEdad ce : categoriasPermitidasCarrera) {
                logger.debug("Comprobando participante {} ({} años) contra categoría {}: Min={} Max={}",
                        dniParticipante, edadParticipante, ce.getNombre(), ce.getEdadMinima(), ce.getEdadMaxima());
                if (edadParticipante >= ce.getEdadMinima() &&
                        (ce.getEdadMaxima() == null || edadParticipante <= ce.getEdadMaxima())) {
                    codCategoriaEdadAsignada = ce.getCod();
                    elegiblePorEdad = true;
                    logger.debug("Participante {} ({} años) es elegible para la categoría de edad {}.",
                            dniParticipante, edadParticipante, ce.getNombre());
                    break;
                }
            }
        }

        if (!elegiblePorEdad) {
            logger.info("Participante {} ({} años) no cumple con los requisitos de edad para ninguna categoría permitida en la carrera {}.",
                    dniParticipante, edadParticipante, codCarrera);
            throw new IllegalArgumentException("No cumples con los requisitos de edad para ninguna categoría permitida en esta carrera.");
        }

        logger.debug("Todas las comprobaciones de elegibilidad superadas para DNI: {} y CodCarrera: {}. Categoría de edad asignada: {}",
                dniParticipante, codCarrera, codCategoriaEdadAsignada);
        return new InscripcionData(codCategoriaEdadAsignada, participante.getSexo());
    }
}