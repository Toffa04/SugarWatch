package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.GlycemiaReading;
import it.univr.DiabetesLogger.service.GlycemiaReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/glycemia")
public class GlycemiaReadingController {

    @Autowired
    private GlycemiaReadingService glycemiaReadingService;

    // GET /glycemia/patient/{patientId} -> MEDIC o PATIENT (solo se stesso)
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatient(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<GlycemiaReading> readings = glycemiaReadingService.getByPatient(patientId);
            return ResponseEntity.ok(readings);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle rilevazioni");
        }
    }

    // GET /glycemia/patient/{patientId}/week?date -> MEDIC o PATIENT
    @GetMapping("/patient/{patientId}/week")
    public ResponseEntity<?> getByWeek(@PathVariable Integer patientId, @RequestParam String date){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(date == null || date.isBlank()){
            return ResponseEntity.badRequest().body("Data obbligatoria");
        }

        try{
            LocalDate week = LocalDate.parse(date);
            List<GlycemiaReading> readings =
                    glycemiaReadingService.getByPatientAndWeek(patientId, week);

            if(readings.isEmpty()){
                return ResponseEntity.ok("Nessuna rilevazione trovata per questa settimana");
            }
            return ResponseEntity.ok(readings);
        } catch(DateTimeParseException e){
            return ResponseEntity.badRequest().body("Formato data non valido, usa YYYY-MM-DD");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero della rilevazione");
        }
    }

    // GET /glycemia/patient/{patientId}/month?month -> MEDIC o PATIENT
    @GetMapping("/patient/{patientId}/month")
    public ResponseEntity<?> getByMonth(@PathVariable Integer patientId, @RequestParam String month){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(month == null || month.isBlank()){
            return ResponseEntity.badRequest().body("Mese obbligatorio");
        }

        try{
            YearMonth yearMonth = YearMonth.parse(month);
            List<GlycemiaReading> readings =
                    glycemiaReadingService.getPatientAndMonth(patientId, yearMonth);

            if(readings.isEmpty()){
                return ResponseEntity.ok("Nessuna rilevazione trovata per questo mese");
            }
            return ResponseEntity.ok(readings);
        } catch(DateTimeParseException e){
            return ResponseEntity.badRequest().body("Formato mese non valido, usa YYYY-MM-DD");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle rilevazioni");
        }
    }

    // GET /glycemia/patient/{patientId}/above-threshold -> MEDIC
    @GetMapping("/patient/{patientId}/above-threshold")
    public ResponseEntity<?> getAboveThreshold(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<GlycemiaReading> readings =
                    glycemiaReadingService.getAboveThresholdByPatient(patientId);

            if(readings.isEmpty()){
                return ResponseEntity.ok("Nessuna rilevazione oltre la soglia");
            }
            return ResponseEntity.ok(readings);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle rilevazioni");
        }
    }

    // POST /glycemia/patient/{patientId} -> PATIENT (solo se stesso)
    @PostMapping("/patient/{patientId}")
    public ResponseEntity<?> createReading(@PathVariable Integer patientId, @RequestBody GlycemiaReadingRequest reading){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(reading.getGlycemiaLevel() == null){
            return ResponseEntity.badRequest().body("Livello di glicemia obbligatorio");
        }
        if(reading.getGlycemiaLevel() <= 0){
            return ResponseEntity.badRequest().body("Livello glicemia non valido");
        }
        if(reading.getDateTime() == null){
            return ResponseEntity.badRequest().body("Data e ora obbligatorie");
        }
        if(reading.getBeforeMeal() == null){
            return ResponseEntity.badRequest().body("Specificare se prima o dopo il pasto");
        }

        try{
            GlycemiaReading saved =
                    glycemiaReadingService.createForPatient(patientId, reading);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante il salvataggio della rilevazione");
        }
    }

    // PUT /glycemia/patient/{patientId}/{id} -> PATIENT (solo se stesso)
    @PutMapping ("/patient/{patientId}/{id}")
    public ResponseEntity<?> updateReading(@PathVariable Integer patientId, @PathVariable Integer id, @RequestBody GlycemiaReading reading){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id rilevazione non valido");
        }
        if(reading.getGlycemiaLevel() != null && reading.getGlycemiaLevel() <= 0){
            return ResponseEntity.badRequest().body("Livello di glicemia non valido");
        }

        try{
            GlycemiaReading updated =
                    glycemiaReadingService.update(id, reading);
            return ResponseEntity.ok(updated);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'aggiornamento della rilevazione");
        }
    }

    // DELETE /glycemia/patient/{patientId}/{id} -> PATIENT (solo se stesso)
    @DeleteMapping("/patient/{patientId}/{id}")
    public ResponseEntity<?> deleteReading(@PathVariable Integer patientId, @PathVariable Integer id){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id rilevazione non valido");
        }

        try{
            glycemiaReadingService.delete(id);
            return ResponseEntity.ok("Rilevazione eliminata con successo");
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione della rilevazione");
        }
    }

    public static class GlycemiaReadingRequest{
        private Integer glycemiaLevel;
        private LocalDateTime dateTime;
        private Boolean beforeMeal;
        private String symptoms;


        public Integer getGlycemiaLevel() { return glycemiaLevel; }
        public void setGlycemiaLevel(Integer glycemiaLevel) { this.glycemiaLevel = glycemiaLevel; }

        public LocalDateTime getDateTime() { return dateTime; }
        public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

        public Boolean getBeforeMeal() { return beforeMeal; }
        public void setBeforeMeal(Boolean beforeMeal) { this.beforeMeal = beforeMeal; }

        public String getSymptoms() { return this.symptoms; }
        public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    }
}
