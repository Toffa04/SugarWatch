package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.Medic;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.model.User;
import it.univr.DiabetesLogger.repository.MedicChangeLogRepository;
import it.univr.DiabetesLogger.repository.MedicRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import it.univr.DiabetesLogger.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.annotations.DialectOverride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService implements CrudService<Patient>{

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicRepository medicRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Patient create(Patient entity){
        return patientRepository.save(entity);
    }

    @Override
    public Iterable<Patient> getAll(){
        return patientRepository.findByUserVerified();
    }

    @Override
    public Optional<Patient> getById(Integer patientId){
        return patientRepository.findById(patientId);
    }

    @Override
    public Patient update(Integer patientId, Patient entity){
        Patient existing = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        existing.updatePatient(entity);

        return patientRepository.save(existing);
    }

    @Override
    public void delete(Integer patientId){
        patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        patientRepository.deleteById(patientId);
    }

    // crea un paziente dato uno userId e medicId
    public Patient createPatient(Integer userId, String firstName, String lastName, LocalDate birthDate, Boolean isSmoker, Boolean isExSmoker, Boolean hasAlcoholDependency, Boolean hasObesity, String medicalHistory){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Medic medic = medicRepository
                .findMedicsOrderedByPatientCountAsc(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nessun medico trovato"));

        Patient patient = new Patient(user, firstName, lastName, birthDate, isSmoker, isExSmoker, hasAlcoholDependency, hasObesity, medicalHistory, medic);
        patient.setId(userId);
        return patientRepository.save(patient);
    }

    // tutti i pazienti di un medico
    public List<Patient> getByMedic(Integer medicId){
        return patientRepository.findByReferralMedicId(medicId);
    }

    // assegna un nuovo medico di riferimento
    public Patient assignMedic(Integer patientId, Integer medicId){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        Medic medic = medicRepository.findById(medicId)
                .orElseThrow(() -> new RuntimeException("Medico non trovato"));

        patient.setReferralMedic(medic);
        return patientRepository.save(patient);
    }

    // aggiorna i fattori di rischio del paziente
    public Patient updateRiskFactors(Integer patientId, Boolean isSmoker, Boolean isExSmoker, Boolean hasAlcoholDependency, Boolean hasObesity, String medicalHistory){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        if(isSmoker != null) patient.setSmoker(isSmoker);
        if(isExSmoker != null) patient.setExSmoker(isExSmoker);
        if(hasAlcoholDependency != null) patient.setHasAlcoholDependency(hasAlcoholDependency);
        if(hasObesity != null) patient.setHasObesity(hasObesity);
        if(medicalHistory != null) patient.setMedicalHistory(medicalHistory);

        return patientRepository.save(patient);
    }

    // trova paziente dal suo userId
    public Optional<Patient> getByUserId(Integer userId){
        return patientRepository.findByUserId(userId);
    }
}
