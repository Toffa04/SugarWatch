package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.Medic;
import it.univr.DiabetesLogger.model.MedicChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicChangeLogRepository extends JpaRepository<MedicChangeLog, Integer> {
    List<MedicChangeLog> findByPatientId(Integer patientId);
    List<MedicChangeLog> findByMedicId(Integer medicId);
    List<MedicChangeLog> findByMedicIdAndPatientId(Integer medicId, Integer patientId);
    List<MedicChangeLog> findByEntityType(String entityType);
}
