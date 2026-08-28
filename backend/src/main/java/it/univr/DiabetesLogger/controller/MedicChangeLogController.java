package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.MedicChangeLog;
import it.univr.DiabetesLogger.service.MedicChangeLogService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medic-change-log")
public class MedicChangeLogController {

    @Autowired
    private MedicChangeLogService medicChangeLogService;

    // GET /medic-change-log -> ADMIN
    @GetMapping
    public ResponseEntity<?> getAllLogs() {
        try {
            List<MedicChangeLog> logs = medicChangeLogService.getAll();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei log");
        }
    }

    // GET /medic-change-log/{id} -> ADMIN, MEDIC
    @GetMapping("/{id}")
    public ResponseEntity<?> getLogById(@PathVariable Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("Id non valido");
        }

        try {
            Optional<MedicChangeLog> log = medicChangeLogService.getById(id);
            if (log.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Log non trovato");
            }
            return ResponseEntity.ok(log);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero del log");
        }
    }

    // GET /medic-change-log/medic/{medicId} -> ADMIN, MEDIC
    @GetMapping("/medic/{medicId}")
    public ResponseEntity<?> getLogsByMedic(@PathVariable Integer medicId) {
        if (medicId == null || medicId <= 0) {
            return ResponseEntity.badRequest().body("Id medico non valido");
        }

        try {
            List<MedicChangeLog> logs = medicChangeLogService.getByMedic(medicId);
            if (logs.isEmpty()) {
                return ResponseEntity.ok("Nessun log trovato per questo medico");
            }
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei log");
        }
    }

    // GET /medic-change-log/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getLogsByPatient(@PathVariable Integer patientId){
        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<MedicChangeLog> logs = medicChangeLogService.getByPatient(patientId);
            if(logs.isEmpty()){
                return ResponseEntity.ok("Nessun log trovato per questo paziente");
            }
            return ResponseEntity.ok(logs);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei log");
        }
    }

    // GET /medic-change-log/medic/{medicId}/patient/{patientId} -> ADMIN, MEDIC
    @GetMapping("/medic/{medicId}/patient/{patientId}")
    public ResponseEntity<?> getLogsByMedicAndPatient(@PathVariable Integer medicId, @PathVariable Integer patientId){
        if(medicId == null){
            return ResponseEntity.badRequest().body("Id medico non valido");
        }
        if(patientId == null){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }

        try{
            List<MedicChangeLog> logs = medicChangeLogService.getByMedicAndPatient(medicId, patientId);
            if(logs.isEmpty()){
                return ResponseEntity.ok("Nessun log trovato per questa combinazione medico-paziente");
            }
            return ResponseEntity.ok(logs);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei log");
        }
    }

    // GET /medic-change-log/type/{entityType} -> ADMIN
    @GetMapping("type/{entityType}")
    public ResponseEntity<?> getLogsByEntityType(@PathVariable String entityType){
        if(entityType == null || entityType.isBlank()){
            return ResponseEntity.badRequest().body("EntityType non valido");
        }

        try{
            List<MedicChangeLog> logs = medicChangeLogService.getByEntityType(entityType);
            if(logs.isEmpty()){
                return ResponseEntity.ok("Nessun log trovato per questo tipo di entità");
            }
            return ResponseEntity.ok(logs);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei log");
        }
    }

    // POST /medic-change-log -> crea un nuovo log
    @PostMapping
    public ResponseEntity<?> createLog(@RequestBody CreateMedicChangeLogRequest request){
        if(request.getMedicId() == null) {
            return ResponseEntity.badRequest().body("MedicId obbligatorio");
        }
        if(request.getPatientId() == null) {
            return ResponseEntity.badRequest().body("PatientId obbligatorio");
        }
        if(request.getAction() == null){
            return ResponseEntity.badRequest().body("Action obbligatoria");
        }
        if(request.getEntityType() == null){
            return ResponseEntity.badRequest().body("EntityType obbligatoria");
        }

        try{
            MedicChangeLog log = medicChangeLogService.createLog(
                    request.getMedicId(),
                    request.getPatientId(),
                    request.getAction(),
                    request.getEntityType()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(log);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la creazione del log");
        }
    }

    // DELETE /medic-change-log/{id} -> ADMIN
    @DeleteMapping
    public ResponseEntity<?> deleteLog(@PathVariable Integer id){
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }

        try{
            medicChangeLogService.delete(id);
            return ResponseEntity.ok("Log eliminato con successo");
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione del log");
        }
    }

    public static class CreateMedicChangeLogRequest{

        private Integer medicId;
        private Integer patientId;
        private String action;
        private String entityType;
        public Integer getMedicId() { return medicId; }
        public void setMedicId(Integer medicId) { this.medicId = medicId; }

        public Integer getPatientId() {return patientId; }
        public void setPatientId(Integer patientId) { this.patientId = patientId; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }
    }
}
