package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.ConcomitantTherapy;
import it.univr.DiabetesLogger.service.ConcomitantTherapyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concomitant-therapy")
public class ConcomitantTherapyController {

    private final ConcomitantTherapyService concomitantTherapyService;

    public ConcomitantTherapyController(ConcomitantTherapyService concomitantTherapyService){
        this.concomitantTherapyService = concomitantTherapyService;
    }

    @GetMapping("/patient/{patientId}")
    public List<ConcomitantTherapy> getAllForPatient(@PathVariable Integer patientId){
        return concomitantTherapyService.getAllForPatient(patientId);
    }

    @GetMapping("/patient/{patientId}/active")
    public List<ConcomitantTherapy> getActiveForPatient(@PathVariable Integer patientId){
        return concomitantTherapyService.getActiveForPatient(patientId);
    }

    @GetMapping("/patient/{patientId}/{id}")
    public ConcomitantTherapy getOne(@PathVariable Integer patientId, @PathVariable Integer id){
        return concomitantTherapyService.getOne(patientId, id);
    }

    @PostMapping("/patient/{patientId}")
    public ConcomitantTherapy create(@PathVariable Integer patientId, @RequestBody ConcomitantTherapy therapy){
        return concomitantTherapyService.create(patientId, therapy);
    }

    @PutMapping("/patient/{patientId}/{id}")
    public ConcomitantTherapy update(@PathVariable Integer patientId, @PathVariable Integer id, @RequestBody ConcomitantTherapy therapy){
        return concomitantTherapyService.update(patientId, id, therapy);
    }

    @PatchMapping("/{id}/close")
    public ConcomitantTherapy close(@PathVariable Integer id){
        return concomitantTherapyService.close(id);
    }

    @DeleteMapping("/patient/{patientId}/{id}")
    public void delete(@PathVariable Integer patientId, @PathVariable Integer id){
        concomitantTherapyService.delete(patientId, id);
    }
}
