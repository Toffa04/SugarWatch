package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.*;
import it.univr.DiabetesLogger.repository.GlycemiaReadingRepository;
import it.univr.DiabetesLogger.repository.MedicRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import it.univr.DiabetesLogger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicService implements CrudService<Medic>{

    @Autowired
    private MedicRepository medicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private GlycemiaReadingRepository glycemiaReadingRepository;

    @Override
    public Medic create(Medic entity){
        return medicRepository.save(entity);
    }

    @Override
    public Iterable<Medic> getAll(){
        return medicRepository.findByUserVerified();
    }

    @Override
    public Optional<Medic> getById(Integer medicId){
        return medicRepository.findById(medicId);
    }

    @Override
    public Medic update(Integer medicId, Medic entity){
        Medic existing = medicRepository.findById(medicId)
                .orElseThrow(() -> new RuntimeException("Medico non trovato"));

        existing.updateMedic(entity);

        return medicRepository.save(existing);
    }

    @Override
    public void delete(Integer medicId){
        medicRepository.findById(medicId)
                .orElseThrow(() -> new RuntimeException("Medico non trovato"));

        medicRepository.deleteById(medicId);
    }

    // crea un medico dato uno userId
    public Medic createMedic(Integer userId, String firstName, String lastName){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Medic medic = new Medic(user, firstName, lastName);
        medic.setId(userId);
        return medicRepository.save(medic);
    }

    // lista dei pazienti del medico
    public List<Patient> getPatients(Integer medicId){
        return patientRepository.findByReferralMedicId(medicId);
    }

    // pazienti con glicemia oltre alla soglia
    public List<Patient> getPatientWithHighGlycemia(Integer medicId){
        return patientRepository.findByReferralMedicId(medicId)
                .stream()
                .filter(patient -> {
                    List<GlycemiaReading> readings =
                            glycemiaReadingRepository.findByPatientId(patient.getId());
                    return readings.stream().anyMatch(GlycemiaReading::isValueHigh);
                })
                .collect(Collectors.toList());
    }

    // trova il medico dal suo userId
    public Optional<Medic> getByUserId(Integer userId){
        return medicRepository.findByUserId(userId);
    }
}
