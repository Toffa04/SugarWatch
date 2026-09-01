package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.Medic;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.model.Therapy;
import it.univr.DiabetesLogger.model.enums.TherapyStatus;
import it.univr.DiabetesLogger.repository.MedicRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import it.univr.DiabetesLogger.repository.TherapyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TherapyService implements CrudService<Therapy>{

    @Autowired
    private TherapyRepository therapyRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicRepository medicRepository;

    @Override
    public Therapy create(Therapy entity){
        return therapyRepository.save(entity);
    }

    @Override
    public Iterable<Therapy> getAll(){
        return therapyRepository.findAll();
    }

    @Override
    public Optional<Therapy> getById(Integer id){
        return therapyRepository.findById(id);
    }

    @Override
    public Therapy update(Integer id, Therapy entity){
        Therapy existing = therapyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terapia non trovata"));

        existing.updateTherapy(entity);

        existing.setLastModifiedAt(LocalDateTime.now());

        return therapyRepository.save(existing);
    }

    @Override
    public void delete(Integer id){
        therapyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terapia non trovata"));
        therapyRepository.deleteById(id);
    }

    // crea una terapia per un paziente, prescritta da un medico
    public Therapy createForPatient(Integer patientId, Integer medicId, Therapy therapy){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        Medic medic = medicRepository.findById(medicId)
                .orElseThrow(() -> new RuntimeException("Medico non trovato"));

        therapy.setPatient(patient);
        therapy.setMedic(medic);
        therapy.setStatus(TherapyStatus.ACTIVE);
        therapy.setStartDate(LocalDate.now());
        therapy.setLastModifiedBy(medic);
        therapy.setLastModifiedAt(LocalDateTime.now());

        return therapyRepository.save(therapy);
    }

    // tutte le terapie di un paziente
    public List<Therapy> getByPatient(Integer patientId){
        return therapyRepository.findByPatientId(patientId);
    }

    // terapia attiva di un paziente
    public List<Therapy> getActiveTherapy(Integer patientId){
        return therapyRepository
                .findByPatientIdAndStatus(patientId, TherapyStatus.ACTIVE);
    }

    // terapie sospese di un paziente
    public List<Therapy> getSuspendedTherapies(Integer patientId){
        return therapyRepository
                .findByPatientIdAndStatus(patientId, TherapyStatus.SUSPENDED);
    }

    // terapie modificate di un paziente
    public List<Therapy> getModifiedTherapies(Integer patientId){
        return therapyRepository
                .findByPatientIdAndStatus(patientId, TherapyStatus.MODIFIED);
    }

    // sospende una terapia
    public Therapy suspendTherapy(Integer id, Integer medicId){
        Therapy existing = therapyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terapia non trovata"));

        Medic medic = medicRepository.findById(medicId)
                .orElseThrow(() -> new RuntimeException("Medico non trovato"));

        existing.setStatus(TherapyStatus.SUSPENDED);
        existing.setLastModifiedBy(medic);
        existing.setLastModifiedAt(LocalDateTime.now());

        return therapyRepository.save(existing);
    }

    // modifica terapia -> status diventa MODIFIED
    public Therapy updateTherapy(Integer id, Therapy entity, Integer medicId){
        Therapy existing = therapyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terapia non trovata"));

        Medic medic = medicRepository.findById(medicId)
                .orElseThrow(() -> new RuntimeException("Medico non trovato"));

        existing.updateTherapy(entity);
        existing.setStatus(TherapyStatus.MODIFIED);
        existing.setLastModifiedBy(medic);
        existing.setLastModifiedAt(LocalDateTime.now());

        return therapyRepository.save(existing);
    }
}
