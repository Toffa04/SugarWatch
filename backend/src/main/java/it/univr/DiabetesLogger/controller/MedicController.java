package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.GlycemiaReading;
import it.univr.DiabetesLogger.model.Medic;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.service.MedicService;
import org.apache.coyote.Response;
import org.aspectj.apache.bcel.classfile.ExceptionTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medic")
public class MedicController {

    @Autowired
    private MedicService medicService;

    // GET /medic -> ADMIN
    @GetMapping
    public ResponseEntity<?> getAllMedics(){
        try{
            Iterable<Medic> medics = medicService.getAll();
            return ResponseEntity.ok(medics);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei medici");
        }
    }

    // GET /medic/{id} -> ADMIN, MEDIC
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicById(@PathVariable Integer id){
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }

        try{
            Optional<Medic> medic = medicService.getById(id);
            if(medic.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Medico non trovato");
            }
            return ResponseEntity.ok(medic);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero del medico");
        }
    }

    // GET /medic/{id}/patients -> ADMIN, MEDIC
    @GetMapping("/{id}/patients")
    public ResponseEntity<?> getPatientsByMedic(@PathVariable Integer id){

        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }

        try{
            List<Patient> patients = medicService.getPatients(id);
            if(patients.isEmpty()){
                return ResponseEntity.ok(patients); // se vuoto torna []
            }

            return ResponseEntity.ok(patients);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei pazienti");
        }
    }

    // GET /medic/{id}/patients/high-glycemia -> MEDIC, ADMIN
    @GetMapping("/{id}/patients/high-glycemia")
    public ResponseEntity<?> getPatientsWithHighGlycemia(@PathVariable Integer id){

        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }

        try{
            List<Patient> patients = medicService.getPatientWithHighGlycemia(id);
            if(patients.isEmpty()){
                return ResponseEntity.ok("Nessun paziente con glicemia oltre la soglia");
            }
            return ResponseEntity.ok(patients);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei pazienti");
        }
    }

    // POST /medic -> crea un nuovo medico
    @PostMapping
    public ResponseEntity<?> createMedic(@RequestBody CreateMedicRequest request){
        if(request.getUserId() == null){
            return ResponseEntity.badRequest().body("UserId obbligatorio");
        }
        if(request.getFirstName() == null){
            return ResponseEntity.badRequest().body("Nome obbligatorio");
        }
        if(request.getLastName() == null){
            return ResponseEntity.badRequest().body("Cognome obbligatorio");
        }

        try{
            Medic medic = medicService.createMedic(
                    request.getUserId(),
                    request.getFirstName(),
                    request.getLastName()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(medic);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la creazione del medico");
        }
    }

    // PUT /medic/{id} -> ADMIN, MEDIC
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedic(@PathVariable Integer id, @RequestBody Medic medic){

        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }
        if(medic.getFirstName() == null || medic.getFirstName().isBlank()){
            return ResponseEntity.badRequest().body("Nome obbligatorio");
        }
        if(medic.getLastName() == null || medic.getLastName().isBlank()){
            return ResponseEntity.badRequest().body("Cognome obbligatorio");
        }

        try{
            Medic updated = medicService.update(id, medic);
            return ResponseEntity.ok(updated);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'aggiornamento del medico");
        }
    }

    // DELETE /medic/{id} -> ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedic(@PathVariable Integer id){

        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }

        try{
            medicService.delete(id);
            return ResponseEntity.ok("Medico eliminato con successo");
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione del medico");
        }
    }

    public static class CreateMedicRequest{

        private Integer userId;
        private String firstName;
        private String lastName;

        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }

        public String getFirstName() { return firstName; }
        public void SetFirstName(String firstName){ this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }
}
