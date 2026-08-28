package it.univr.DiabetesLogger.service;


import it.univr.DiabetesLogger.model.Medic;
import it.univr.DiabetesLogger.model.MedicChangeLog;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.repository.MedicChangeLogRepository;
import it.univr.DiabetesLogger.repository.MedicRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicChangeLogService {

    @Autowired
    private MedicChangeLogRepository medicChangeLogRepository;

    @Autowired
    private MedicRepository medicRepository;

    @Autowired
    private PatientRepository patientRepository;

    public List<MedicChangeLog> getAll(){
        return medicChangeLogRepository.findAll();
    }

    public Optional<MedicChangeLog> getById(Integer id){
        return medicChangeLogRepository.findById(id);
    }

    public List<MedicChangeLog> getByMedic(Integer medicId){
        return medicChangeLogRepository.findByMedicId(medicId);
    }

    public List<MedicChangeLog> getByPatient(Integer patientId){
        return medicChangeLogRepository.findByPatientId(patientId);
    }

    public List<MedicChangeLog> getByMedicAndPatient(Integer medicId, Integer patientId){
        return medicChangeLogRepository.findByMedicIdAndPatientId(medicId, patientId);
    }

    public List<MedicChangeLog> getByEntityType(String entityType){
        return medicChangeLogRepository.findByEntityType(entityType);
    }

    public MedicChangeLog createLog(Integer medicId, Integer patientId, String action, String entityType){
        Medic medic = medicRepository.findById(medicId)
                .orElseThrow(() -> new RuntimeException("Medico non trovato"));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        MedicChangeLog log = new MedicChangeLog(medic, patient, action, entityType, LocalDateTime.now());
        return medicChangeLogRepository.save(log);
    }

    public void delete(Integer id){
        if(!medicChangeLogRepository.existsById(id)){
            throw new RuntimeException("Log non trovato");
        }
        medicChangeLogRepository.deleteById(id);
    }
}
