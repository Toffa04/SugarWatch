package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.service.PatientService;
import org.apache.coyote.Response;
import org.aspectj.weaver.bcel.ExceptionRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // GET /patient -> MEDIC, ADMIN
    @GetMapping
    public ResponseEntity<?> getAllPatients(){
        try{
            Iterable<Patient> patients = patientService.getAll();
            return ResponseEntity.ok(patients);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei pazienti");
        }
    }

    // GET /patient/{id} -> MEDIC, ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Integer id){
        if(id == 0 || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }

        try{
            Optional<Patient> patient = patientService.getById(id);
            if(patient.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Paziente non trovato");
            }
            return ResponseEntity.ok(patient.get());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero del paziente");
        }
    }

    // GET /patient/medic/{medicId} -> MEDIC, ADMIN
    @GetMapping("/medic/{medicId}")
    public ResponseEntity<?> getPatientByMedic(@PathVariable Integer medicId){
        if(medicId == 0 || medicId <= 0){
            return ResponseEntity.badRequest().body("Id medico non valido");
        }

        try{
            List<Patient> patients = patientService.getByMedic(medicId);
            if(patients.isEmpty()){
                return ResponseEntity.ok("Nessun paziente trovato per questo medico");
            }
            return ResponseEntity.ok(patients);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero dei pazienti");
        }
    }

    // POST /patient -> ADMIN, PATIENT
    // crea un paziente dato userId e medicId
    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody CreatePatientRequest request){

        try{
            Patient patient = patientService.createPatient(
                    request.getUserId(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getBirthDate(),
                    request.getIsSmoker(),
                    request.getIsExSmoker(),
                    request.getHasAlcoholDependency(),
                    request.getHasObesity(),
                    request.getMedicalHistory()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(patient);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la creazione del paziente");
        }
    }

    // PUT /patient/{id} -> MEDIC
    // aggiorna i dati base del paziente
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Integer id, @RequestBody Patient patient){

        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }
        if(patient.getFirstName() == null || patient.getFirstName().isBlank()){
            return ResponseEntity.badRequest().body("Nome obbligatorio");
        }
        if(patient.getLastName() == null || patient.getLastName().isBlank()){
            return ResponseEntity.badRequest().body("Cognome obbligatorio");
        }

        try{
            Patient updated = patientService.update(id, patient);
            return ResponseEntity.ok(updated);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'aggiornamento del paziente");
        }
    }

    // PUT /patient/{id}/risk-factors -> MEDIC
    // aggiorna fattori rischio
    @PutMapping("/{id}/risk-factors")
    public ResponseEntity<?> updateRiskFactors(@PathVariable Integer id, @RequestBody RiskFactorsRequest request){

        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }

        try{
            Patient updated = patientService.updateRiskFactors(
                    id,
                    request.getIsSmoker(),
                    request.getIsExSmoker(),
                    request.getHasAlcoholDependency(),
                    request.getHasObesity(),
                    request.getMedicalHistory()
            );
            return ResponseEntity.ok(updated);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'aggiornamento dei fattori di rischio");
        }
    }

    // PUT /patient/{patientId}/medic/{medicId}
    // assegna medico di riferimento
    @PutMapping("/{patientId}/medic/{medicId}")
    public ResponseEntity<?> assignMedic(@PathVariable Integer patientId, @PathVariable Integer medicId){

        if(patientId == null || patientId <= 0){
            return ResponseEntity.badRequest().body("Id paziente non valido");
        }
        if(medicId == null || medicId <= 0){
            return ResponseEntity.badRequest().body("Id medico non valido");
        }

        try{
            Patient updated = patientService.assignMedic(patientId, medicId);
            return ResponseEntity.ok(updated);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'assegnazione del medico");
        }
    }

    // DELETE /patient/{id} -> ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Integer id){

        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id non valido");
        }

        try{
            patientService.delete(id);
            return ResponseEntity.ok("Paziente eliminato con successo");
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione del paziente");
        }
    }

    public static class CreatePatientRequest{

        private Integer userId;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private Boolean isSmoker;
        private Boolean isExSmoker;
        private Boolean hasAlcoholDependency;
        private Boolean hasObesity;
        private String medicalHistory;

        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public LocalDate getBirthDate(){ return birthDate; }
        public void setBirthDate(LocalDate birthDate){ this.birthDate = birthDate; }

        public Boolean getIsSmoker() { return isSmoker; }
        public void setIsSmoker(Boolean isSmoker) { this.isSmoker = isSmoker; }

        public Boolean getIsExSmoker() { return isExSmoker; }
        public void setIsExSmoker(Boolean isExSmoker) { this.isExSmoker = isExSmoker; }

        public Boolean getHasAlcoholDependency() { return hasAlcoholDependency; }
        public void setHasAlcoholDependency(Boolean hasAlcoholDependency) { this.hasAlcoholDependency = hasAlcoholDependency; }

        public Boolean getHasObesity() { return hasObesity; }
        public void setHasObesity(Boolean hasObesity){ this.hasObesity = hasObesity; }

        public String getMedicalHistory() { return medicalHistory; }
        public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    }

    public static class RiskFactorsRequest{

        private Boolean isSmoker;
        private Boolean isExSmoker;
        private Boolean hasAlcoholDependency;
        private Boolean hasObesity;
        private String medicalHistory;

        public Boolean getIsSmoker() { return isSmoker; }
        public void setIsSmoker(Boolean isSmoker) { this.isSmoker = isSmoker; }

        public Boolean getIsExSmoker() { return isExSmoker; }
        public void setIsExSmoker(Boolean isExSmoker) { this.isExSmoker = isExSmoker; }

        public Boolean getHasAlcoholDependency() { return hasAlcoholDependency; }
        public void setHasAlcoholDependency(Boolean hasAlcoholDependency) { this.hasAlcoholDependency = hasAlcoholDependency; }

        public Boolean getHasObesity() { return hasObesity; }
        public void setHasObesity(Boolean hasObesity){ this.hasObesity = hasObesity; }

        public String getMedicalHistory() { return medicalHistory; }
        public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    }
}


