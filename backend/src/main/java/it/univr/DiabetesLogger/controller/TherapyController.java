package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.Therapy;
import it.univr.DiabetesLogger.service.TherapyService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/therapy")
public class TherapyController {

    @Autowired
    private TherapyService therapyService;

    // GET therapy/patient/{patientId} MEDIC o PATIENT
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatient(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<Therapy> therapies = therapyService.getByPatient(patientId);
            if(therapies.isEmpty()){
                return ResponseEntity.ok("Nessuna terapia trovata");
            }
            return ResponseEntity.ok(therapies);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle terapie");
        }
    }

    // GET /therapy/patient/{patientId}/active MEDIC o PATIENT
    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<?> getActiveTherapy(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            Optional<Therapy> therapy = therapyService.getActiveTherapy(patientId);
            if(therapy.isEmpty()){
                return ResponseEntity.status((HttpStatus.NOT_FOUND))
                        .body("Nessuna terapia attiva trovata");
            }
            return ResponseEntity.ok(therapy.get());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero della terapia attiva");
        }
    }

    // GET /therapy/patient/{patientId}/suspended -> MEDIC
    @GetMapping("/patient/{patientId}/suspended")
    public ResponseEntity<?> getSuspendedTherapy(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<Therapy> therapies = therapyService.getSuspendedTherapies(patientId);
            if(therapies.isEmpty()){
                return ResponseEntity.ok("Nessuna terapia sospesa trovata");
            }
            return ResponseEntity.ok(therapies);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle terapie sospese");
        }
    }

    // GET /therapy/patient/{patientId}/modified -> MEDIC
    @GetMapping("/patient/{patientId}/modified")
    public ResponseEntity<?> getModifiedTherapy(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<Therapy> therapies = therapyService.getModifiedTherapies(patientId);
            if(therapies.isEmpty()){
                return ResponseEntity.ok("Nessuna terapia modificata trovata");
            }
            return ResponseEntity.ok(therapies);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle terapie modificate");
        }
    }

    // GET /therapy/{id} -> MEDIC
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id){
        if(id == null || id <= 0){
            return ResponseEntity.ok("Id terapia non valido");
        }

        try{
            Optional<Therapy> therapy = therapyService.getById(id);
            if(therapy.isEmpty()){
                return ResponseEntity.ok("Terapia non trovata");
            }
            return ResponseEntity.ok(therapy.get());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero della terapia");
        }
    }

    // POST /therapy/patient/{patientId}/medic/{medicId}
    @PostMapping("/patient/{patientId}/medic/{medicId}")
    public ResponseEntity<?> createTherapy(@PathVariable Integer patientId, @PathVariable Integer medicId, @RequestBody Therapy therapy){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(medicId == null || medicId <= 0){
            return ResponseEntity.badRequest().body("Id medico non valido");
        }
        if(therapy.getMedicine() == null || therapy.getMedicine().isBlank()){
            return ResponseEntity.badRequest().body("Farmaco obbligatorio");
        }
        if(therapy.getDosesPerDay() == null || therapy.getDosesPerDay() <= 0){
            return ResponseEntity.badRequest().body("Numero dosi giornaliere non valido");
        }
        if(therapy.getQuantity() == null || therapy.getQuantity() <= 0){
            return ResponseEntity.badRequest().body("Quantita' non valida");
        }

        try{
            Therapy saved = therapyService.createForPatient(patientId, medicId, therapy);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la creazione della terapia");
        }
    }

    // PUT /therapy/{id}/medic/{mediId} -> MEDIC
    @PutMapping("/{id}/medic/{medicId}")
    public ResponseEntity<?> updateTherapy(@PathVariable Integer id, @PathVariable Integer medicId, @RequestBody Therapy therapy){
        if(medicId == null || medicId <= 0){
            return ResponseEntity.badRequest().body("Id medico non valido");
        }
        if(therapy.getMedicine() == null || therapy.getMedicine().isBlank()){
            return ResponseEntity.badRequest().body("Farmaco obbligatorio");
        }
        if(therapy.getDosesPerDay() == null || therapy.getDosesPerDay() <= 0){
            return ResponseEntity.badRequest().body("Numero dosi giornaliere non valido");
        }
        if(therapy.getQuantity() == null || therapy.getQuantity() <= 0){
            return ResponseEntity.badRequest().body("Quantita' non valida");
        }

        try{
            Therapy updated = therapyService.updateTherapy(id, therapy, medicId);
            return ResponseEntity.ok(updated);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'aggiornamento della terapia");
        }
    }

    // PATCH /therapy/{id}/suspended/medi/{medicId} -> MEDIC
    @PatchMapping("/{id}/suspended/medic/{medicId}")
    public ResponseEntity<?> suspendedTherapy(@PathVariable Integer id, @PathVariable Integer medicId){
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id terapia non valido");
        }
        if(medicId == null || medicId <= 0){
            return ResponseEntity.badRequest().body("Id medico non valido");
        }

        try{
            Therapy suspended = therapyService.suspendTherapy(id, medicId);
            return ResponseEntity.ok(suspended);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la sospensione della terapia");
        }
    }

    // DELETE /teraphy/{id} -> MEDIC
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTherapy(@PathVariable Integer id){
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id terapia non valido");
        }

        try{
            therapyService.delete(id);
            return ResponseEntity.ok("Terapia eliminata con successo");
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione della terapia");
        }
    }
}
