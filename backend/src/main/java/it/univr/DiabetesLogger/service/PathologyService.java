package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.Pathology;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.repository.PathologyRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PathologyService {

    private final PathologyRepository pathologyRepository;
    private final PatientRepository patientRepository;

    public PathologyService(PathologyRepository pathologyRepository, PatientRepository patientRepository){
        this.pathologyRepository = pathologyRepository;
        this.patientRepository = patientRepository;
    }

    public List<Pathology> getAllForPatient(Integer patientId){
        return pathologyRepository.findByPatientId(patientId);
    }

    public List<Pathology> getActiveForPatient(Integer patientId){
        return pathologyRepository.findByPatientIdAndEndDateIsNull(patientId);
    }

    public Pathology getOne(Integer patientId, Integer id){
        return pathologyRepository.findByIdAndPatientId(id, patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patologia non trovata"));
    }

    public Pathology create(Integer patientId, Pathology payload){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paziente non trovato"));

        Pathology pathology = new Pathology(
                patient,
                payload.getDescription(),
                payload.getStartDate(),
                payload.getEndDate(),
                payload.getNotes()
        );

        return pathologyRepository.save(pathology);
    }

    public Pathology update(Integer patientId, Integer id, Pathology payload){
        Pathology existing = getOne(patientId, id);

        if(payload.getDescription() != null){
            existing.setDescription(payload.getDescription());
        }
        if(payload.getStartDate() != null){
            existing.setStartDate(payload.getStartDate());
        }
        existing.setEndDate(payload.getEndDate());

        if(payload.getNotes() != null){
            existing.setNotes(payload.getNotes());
        }

        return pathologyRepository.save(existing);
    }

    public Pathology close(Integer id){
        Pathology existing = pathologyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patologia non trovata"));

        existing.setEndDate(LocalDate.now());
        return pathologyRepository.save(existing);
    }

    public void delete(Integer patientId, Integer id){
        Pathology existing = getOne(patientId, id);
        pathologyRepository.delete(existing);
    }
}
