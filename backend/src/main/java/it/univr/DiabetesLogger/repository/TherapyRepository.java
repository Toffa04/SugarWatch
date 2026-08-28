package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.Therapy;
import it.univr.DiabetesLogger.model.enums.TherapyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TherapyRepository extends JpaRepository<Therapy, Integer> {
    List<Therapy> findByPatientId(Integer patientId);
    List<Therapy> findByPatientIdAndStatus(Integer patientId, TherapyStatus status);
}
