package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.Medic;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MedicRepository extends JpaRepository<Medic, Integer> {
    Optional<Medic> findByUserId(Integer userId);
    Optional<Medic> findByUser(User user);

    SimpleJpaRepository<Object, Object> countMedicsByPatients(Set<Patient> patients);

    @Query("""
    SELECT m FROM Medic m
    LEFT JOIN m.patients p
    JOIN m.user u
    WHERE u.verified = true
    GROUP BY m
    ORDER BY COUNT(p) ASC
    """)
    List<Medic> findMedicsOrderedByPatientCountAsc(Pageable pageable);

    @Query("SELECT m FROM Medic m WHERE m.user.verified = true")
    List<Medic> findByUserVerified();
}
