package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    List<Patient> findByReferralMedicId(Integer medicId);
    Optional<Patient> findByUserId(Integer userId);
    Optional<Patient> findByUser(User user);

    @Query("SELECT p FROM Patient p WHERE p.user.verified = true")
    List<Patient> findByUserVerified();
}
