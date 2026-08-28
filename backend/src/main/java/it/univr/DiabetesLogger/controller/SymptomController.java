package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.Symptom;
import it.univr.DiabetesLogger.service.SymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/symptom")
public class SymptomController {

    @Autowired
    private SymptomService symptomService;

    // GET /symptom/patient/{patientId} -> MEDIC o PATIENT
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatient(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<Symptom> symptoms = symptomService.getByPatient(patientId);
            if(symptoms.isEmpty()){
                return ResponseEntity.ok("Nessun sintomo trovato");
            }
            return ResponseEntity.ok(symptoms);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei sintomi");
        }
    }

    // GET /symptom/patient/{patientId}/{id} -> MEDIC o PATIENT
    @GetMapping("/patient/{patientId}/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer patientId, @PathVariable Integer id){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id sintomo non valido");
        }

        try{
            Optional<Symptom> symptom = symptomService.getById(id);
            if(symptom.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Sintomo non trovato");
            }
            return ResponseEntity.ok(symptom);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero del sintomo");
        }
    }

    // GET /symptom/patient/{patientId}/active -> MEDIC o PATIENT
    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<?> getActiveByPatient(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<Symptom> symptoms = symptomService.getActiveByPatient(patientId);
            if(symptoms.isEmpty()){
                return ResponseEntity.ok("Nessun sintomo attivo trovato");
            }
            return ResponseEntity.ok(symptoms);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei sintomi attivi");
        }
    }

    // POST /symptom/patient/{patientId} -> PATIENT
    @PostMapping("/patient/{patientId}")
    public ResponseEntity<?> createSymptom(@PathVariable Integer patientId, @RequestBody Symptom symptom){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non trovato");
        }
        if(symptom.getDescription() == null || symptom.getDescription().isBlank()){
            return ResponseEntity.badRequest().body("Descrizione obbligatoria");
        }
        if(symptom.getStartDate() == null){
            return ResponseEntity.badRequest().body("Data inizio obbligatoria");
        }
        if(symptom.getEndDate() != null && symptom.getEndDate().isBefore(symptom.getStartDate())){
            return ResponseEntity.badRequest().body("Data fine non puo' essere prima della data di inizio");
        }

        try {
            Symptom saved = symptomService.createForPatient(patientId, symptom);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante il salvataggio del sintomo");
        }
    }

    // PUT /symptom/patient/{patientId}/{id} → PATIENT (solo sé stesso)
    @PutMapping("/patient/{patientId}/{id}")
    public ResponseEntity<?> updateSymptom(@PathVariable Integer patientId,
                                           @PathVariable Integer id,
                                           @RequestBody Symptom symptom) {
        if (patientId == null || patientId <= 0) {
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("Id sintomo non valido");
        }
        if (symptom.getEndDate() != null && symptom.getStartDate() != null &&
                symptom.getEndDate().isBefore(symptom.getStartDate())) {
            return ResponseEntity.badRequest()
                    .body("Data fine non può essere prima della data inizio");
        }

        try {
            Symptom updated = symptomService.update(id, symptom);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'aggiornamento del sintomo");
        }
    }

    // PATCH /symptom/{id}/close?endDate=2024-01-01 → PATIENT (solo sé stesso)
    @PatchMapping("/{id}/close")
    public ResponseEntity<?> closeSymptom(@PathVariable Integer id,
                                          @RequestParam String endDate) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("Id sintomo non valido");
        }
        if (endDate == null || endDate.isBlank()) {
            return ResponseEntity.badRequest().body("Data fine obbligatoria");
        }

        try {
            LocalDate date = LocalDate.parse(endDate);
            Symptom closed = symptomService.closeSymptom(id, date);
            return ResponseEntity.ok(closed);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body("Formato data non valido, usa YYYY-MM-DD");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la chiusura del sintomo");
        }
    }

    // DELETE /symptom/patient/{patientId}/{id} → PATIENT (solo sé stesso)
    @DeleteMapping("/patient/{patientId}/{id}")
    public ResponseEntity<?> deleteSymptom(@PathVariable Integer patientId,
                                           @PathVariable Integer id) {
        if (patientId == null || patientId <= 0) {
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("Id sintomo non valido");
        }

        try {
            symptomService.delete(id);
            return ResponseEntity.ok("Sintomo eliminato con successo");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione del sintomo");
        }
    }
}
