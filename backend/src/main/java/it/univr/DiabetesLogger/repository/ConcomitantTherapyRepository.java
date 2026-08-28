package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.ConcomitantTherapy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcomitantTherapyRepository extends JpaRepository<ConcomitantTherapy, Integer> {

    List<ConcomitantTherapy> findByPatientId(Integer patientId);
    List<ConcomitantTherapy> findByPatientIdAndEndDateIsNull(Integer patientId);
    Optional<ConcomitantTherapy> findByIdAndPatientId(Integer id, Integer patientId);
}
