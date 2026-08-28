package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.GlycemiaReading;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.model.Symptom;
import it.univr.DiabetesLogger.repository.PatientRepository;
import it.univr.DiabetesLogger.repository.SymptomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SymptomService implements CrudService<Symptom> {

    @Autowired
    private SymptomRepository symptomRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public Symptom create(Symptom entity){
        return symptomRepository.save(entity);
    }

    @Override
    public Iterable<Symptom> getAll(){
        return symptomRepository.findAll();
    }

    @Override
    public Optional<Symptom> getById(Integer id){
        return symptomRepository.findById(id);
    }

    @Override
    public Symptom update(Integer id, Symptom entity){
        Symptom existing = symptomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sintomo non trovato"));

        if(entity.getDescription() != null){
            existing.setDescription(entity.getDescription());
        }
        if(entity.getNotes() != null){
            existing.setNotes(entity.getNotes());
        }
        if(entity.getStartDate() != null){
            existing.setStartDate(entity.getStartDate());
        }

        existing.setEndDate(entity.getEndDate());

        return symptomRepository.save(existing);
    }

    @Override
    public void delete(Integer id){
        symptomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sintomo non trovato"));

        symptomRepository.deleteById(id);
    }

    // crea un sintomo per un paziente
    public Symptom createForPatient(Integer patientId, Symptom symptom){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        symptom.setPatient(patient);
        return symptomRepository.save(symptom);
    }

    // tutti i sintomi di un paziente
    public List<Symptom> getByPatient(Integer patientId){
        return symptomRepository.findByPatientId(patientId);
    }

    // solo i sintomi ancora attivi di un paziente (endDate == null)
    public List<Symptom> getActiveByPatient(Integer patientId){
        return symptomRepository.findByPatientIdAndEndDateIsNull(patientId);
    }

    // chiude un sintomo impostando la endDate
    public Symptom closeSymptom(Integer id, LocalDate endDate){
        Symptom symptom = symptomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sintomo non trovato"));

        if(!symptom.isActive()){
            throw new RuntimeException("Sintomo gia' chiuso");
        }

        symptom.setEndDate(endDate);
        return symptomRepository.save(symptom);
    }
}
