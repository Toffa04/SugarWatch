package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Integer> {
    List<Symptom> findByPatientId(Integer patientId);
    List<Symptom> findByPatientIdAndEndDateIsNull(Integer patientId);
    // sintomi ancora attivi
}
