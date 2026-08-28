package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.Pathology;
import it.univr.DiabetesLogger.service.PathologyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pathology")
public class PathologyController {

    private final PathologyService pathologyService;

    public PathologyController(PathologyService pathologyService){
        this.pathologyService = pathologyService;
    }

    @GetMapping("/patient/{patientId}")
    public List<Pathology> getAllForPatient(@PathVariable Integer patientId) {
        return pathologyService.getAllForPatient(patientId);
    }

    @GetMapping("/patient/{patientId}/active")
    public List<Pathology> getActiveForPatient(@PathVariable Integer patientId){
        return pathologyService.getActiveForPatient(patientId);
    }

    @GetMapping("/patient/{patientId}/{id}")
    public Pathology getOne(@PathVariable Integer patientId, @PathVariable Integer id){
        return pathologyService.getOne(patientId, id);
    }

    @PostMapping("/patient/{patientId}")
    public Pathology create(@PathVariable Integer patientId, @RequestBody Pathology pathology){
        return pathologyService.create(patientId, pathology);
    }

    @PutMapping("/patient/{patientId}/{id}")
    public Pathology update(@PathVariable Integer patientId, @PathVariable Integer id, @RequestBody Pathology pathology){
        return pathologyService.update(patientId, id, pathology);
    }

    @PatchMapping("/{id}/close")
    public Pathology close(@PathVariable Integer id){
        return pathologyService.close(id);
    }

    @DeleteMapping("/patient//{patientId}/{id}")
    public void delete(@PathVariable Integer patientId, @PathVariable Integer id){
        pathologyService.delete(patientId, id);
    }
}
