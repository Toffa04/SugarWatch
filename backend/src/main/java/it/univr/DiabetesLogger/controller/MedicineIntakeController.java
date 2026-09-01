package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.MedicineIntake;
import it.univr.DiabetesLogger.service.MedicineIntakeService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medicine-intake")
public class MedicineIntakeController {

    @Autowired
    private MedicineIntakeService medicineIntakeService;

    // GET/medicine-intake/patient/{patientId} -> MEDIC o PATIENT
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatient(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<MedicineIntake> intakes = medicineIntakeService.getByPatient(patientId);
            if(intakes.isEmpty()){
                return ResponseEntity.ok(intakes);
            }
            return ResponseEntity.ok(intakes);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle assunzioni");
        }
    }

    // GET /medicine-intake/patient/{patientId}/{id} -> MEDIC o PATIENT
    @GetMapping("/patient/{patientId}/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer patientId, @PathVariable Integer id){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id assunzione non valido");
        }

        try{
            Optional<MedicineIntake> intake = medicineIntakeService.getById(id);
            if(intake.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Assunzione non trovata");
            }
            return ResponseEntity.ok(intake.get());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dell'assunzione");
        }
    }

    // GET /medicine-intake/patient/{patientId}/inconsistent -> MEDIC
    @GetMapping("/patient/{patientId}/'inconsistent")
    public ResponseEntity<?> getIncosistentIntakes(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<MedicineIntake> intakes =
                    medicineIntakeService.getInconsistentIntakes(patientId);
            if(intakes.isEmpty()){
                return ResponseEntity.ok(intakes); // modifica bug
            }
            return ResponseEntity.ok(intakes);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle assunzioni");
        }
    }

    // GET /medicine-intake/patient/{patientId}/missed -> MEDIC
    @GetMapping("/patient/{patientId}/missed")
    public ResponseEntity<?> hasMissedIntakes(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            boolean missed = medicineIntakeService.hasMissedIntakesFor3Days(patientId);
            if(missed){
                return ResponseEntity.ok(missed); // farmaco non assunto da + di 3 gg
            }
            return ResponseEntity.ok("il paziente sta seguendo la terapia");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel controllo delle assunzioni");
        }
    }

    // POST /medicine-intake/patient/{patientId} -> PATIENT
    @PostMapping("/patient/{patientId}")
    public ResponseEntity<?> createIntake(@PathVariable Integer patientId, @RequestBody CreateIntakeRequest request){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(request.getTherapyId() == null){
            return ResponseEntity.badRequest().body("Id terapia obbligatorio");
        }
        if(request.getQuantity() == null){
            return ResponseEntity.badRequest().body("Quantita' obbligatoria");
        }
        if(request.getQuantity() <= 0){
            return ResponseEntity.badRequest().body("Quantita' non valida");
        }
        if(request.getDateTime() == null){
            return ResponseEntity.badRequest().body("Data e ora obbligatoria");
        }

        try{
            MedicineIntake intake = new MedicineIntake(
                    null,
                    null,
                    request.getQuantity(),
                    null,
                    request.getDateTime()
            );
            MedicineIntake saved = medicineIntakeService.createForPatient(
                    patientId,
                    request.getTherapyId(),
                    intake
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante il salvataggio dell'assunzione");
        }
    }

    // PUT /medicine-intake/patient/{patientId}/{id} -> PATIENT

    @PutMapping("/patient/{patientId}/{id}")
    public ResponseEntity<?> updateIntake(@PathVariable Integer patientId, @PathVariable Integer id, @RequestBody MedicineIntake intake){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id assunzione non valido");
        }
        if(intake.getQuantity() != null && intake.getQuantity() <= 0){
            return ResponseEntity.badRequest().body("Quantita' non valida");
        }

        try{
            MedicineIntake updated = medicineIntakeService.update(id, intake);
            return ResponseEntity.ok(updated);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'aggiornamento dell'assunzione");
        }
    }

    // DELETE /medicine-intake/patient/{patientId}/{id} -> PATIENT
    @DeleteMapping("/patient/{patientId}/{id}")
    public ResponseEntity<?> deleteIntake(@PathVariable Integer patientId, Integer id){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id assunzione non valido");
        }

        try{
            medicineIntakeService.delete(id);
            return ResponseEntity.ok("Assunzione eliminata con successo");
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione dell'assunzione");
        }
    }

    public static class CreateIntakeRequest{
        private Integer therapyId;
        private Double quantity;
        private LocalDateTime dateTime;

        public Integer getTherapyId() { return therapyId; }
        public void setTherapyId(Integer therapyId) { this.therapyId = therapyId; }

        public Double getQuantity() { return quantity; }
        public void setQuantity(Double quantity) { this.quantity = quantity; }

        public LocalDateTime getDateTime() { return dateTime; }
        public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    }
}
