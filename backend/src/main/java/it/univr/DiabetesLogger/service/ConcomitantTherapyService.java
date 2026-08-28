package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.ConcomitantTherapy;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.repository.ConcomitantTherapyRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConcomitantTherapyService {

    private final ConcomitantTherapyRepository concomitantTherapyRespository;
    private final PatientRepository patientRepository;

    public ConcomitantTherapyService(ConcomitantTherapyRepository concomitantTherapyRepository, PatientRepository patientRepository){
        this.concomitantTherapyRespository = concomitantTherapyRepository;
        this.patientRepository = patientRepository;
    }

    public List<ConcomitantTherapy> getAllForPatient(Integer patientId){
        return concomitantTherapyRespository.findByPatientId(patientId);
    }

    public List<ConcomitantTherapy> getActiveForPatient(Integer patientId){
        return concomitantTherapyRespository.findByPatientIdAndEndDateIsNull(patientId);
    }

    public ConcomitantTherapy getOne(Integer patientId, Integer id){
        return concomitantTherapyRespository.findByIdAndPatientId(id, patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Terapia concomitante non trovata"));
    }

    public ConcomitantTherapy create(Integer patientId, ConcomitantTherapy payload){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paziente non trovato"));

        ConcomitantTherapy therapy = new ConcomitantTherapy(
                patient,
                payload.getMedicine(),
                payload.getReason(),
                payload.getStartDate(),
                payload.getEndDate(),
                payload.getNotes()
        );

        return concomitantTherapyRespository.save(therapy);
    }

    public ConcomitantTherapy update(Integer patientId, Integer id, ConcomitantTherapy payload){
        ConcomitantTherapy existing = getOne(patientId, id);

        if(payload.getMedicine() != null) {
            existing.setMedicine(payload.getMedicine());
        }
        if(payload.getReason() != null) {
            existing.setReason(payload.getReason());
        }
        if(payload.getStartDate() != null) {
            existing.setStartDate(payload.getStartDate());
        }
        existing.setEndDate(payload.getEndDate());

        if(payload.getNotes() != null) {
            existing.setNotes(payload.getNotes());
        }

        return concomitantTherapyRespository.save(existing);
    }

    public ConcomitantTherapy close(Integer id){
        ConcomitantTherapy existing = concomitantTherapyRespository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Terapia concomitante non trovata"));

        existing.setEndDate(LocalDate.now());
        return concomitantTherapyRespository.save(existing);
    }

    public void delete(Integer patientId, Integer id){
        ConcomitantTherapy existing = getOne(patientId, id);
        concomitantTherapyRespository.delete(existing);
    }
}
