package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.GlycemiaReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GlycemiaReadingRepository extends JpaRepository<GlycemiaReading, Integer> {
    List<GlycemiaReading> findByPatientId(Integer patientId);
    List<GlycemiaReading> findByPatientIdAndDateTimeBetween(Integer patientId, LocalDateTime from, LocalDateTime to);
}
