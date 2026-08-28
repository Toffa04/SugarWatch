package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.Pathology;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PathologyRepository extends JpaRepository<Pathology, Integer> {

    List<Pathology> findByPatientId(Integer patientId);
    List<Pathology> findByPatientIdAndEndDateIsNull(Integer patientId);
    Optional<Pathology>  findByIdAndPatientId(Integer id, Integer patientId);
}
