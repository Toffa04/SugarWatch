package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.MedicineIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicineIntakeRepository extends JpaRepository<MedicineIntake, Integer> {
    List<MedicineIntake> findByPatientId(Integer patientId);
    List<MedicineIntake> findByPatientIdAndDateTimeBetween(Integer patientId, LocalDateTime from, LocalDateTime to);
}
