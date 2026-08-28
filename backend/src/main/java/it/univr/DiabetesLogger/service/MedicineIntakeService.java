package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.MedicineIntake;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.model.Therapy;
import it.univr.DiabetesLogger.repository.MedicineIntakeRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import it.univr.DiabetesLogger.repository.TherapyRepository;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicineIntakeService implements CrudService<MedicineIntake>{

    @Autowired
    private MedicineIntakeRepository medicineIntakeRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TherapyRepository therapyRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public MedicineIntake create(MedicineIntake entity){
        return medicineIntakeRepository.save(entity);
    }

    @Override
    public Iterable<MedicineIntake> getAll(){
        return medicineIntakeRepository.findAll();
    }

    @Override
    public Optional<MedicineIntake> getById(Integer id){
        return medicineIntakeRepository.findById(id);
    }

    @Override
    public MedicineIntake update(Integer id, MedicineIntake entity){
        MedicineIntake existing = medicineIntakeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assunzione non trovata"));

        if(entity.getQuantity() != null){
            existing.setQuantity(entity.getQuantity());
        }
        if(entity.getDateTime() != null){
            existing.setDateTime(entity.getDateTime());
        }
        if(entity.getTherapy() != null){
            existing.setTherapy(entity.getTherapy());
        }

        // ricalcola matches therapy dopo aggiornamento
        existing.setMatchesTherapy(checkConsistencyWithTherapy(existing));

        return medicineIntakeRepository.save(existing);
    }

    @Override
    public void delete(Integer id){
        medicineIntakeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assunzione non trovata"));
        medicineIntakeRepository.deleteById(id);
    }

    public MedicineIntake createForPatient(Integer patientId, Integer therapyId, MedicineIntake intake){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        Therapy therapy = therapyRepository.findById(therapyId)
                .orElseThrow(() -> new RuntimeException("Terapia non trovata"));

        intake.setPatient(patient);
        intake.setTherapy(therapy);

        // verifica coerenza con la terapia prescritta
        intake.setMatchesTherapy(checkConsistencyWithTherapy(intake));

        MedicineIntake saved = medicineIntakeRepository.save(intake);

        // dopo aver salvato controlla
        // 1. se il paziente ha dimenticato farmaci oggi
        // 2. se non segue la terapia da 3+ giorni
        notificationService.checkMissedMedicine(patientId);
        notificationService.checkTherapyNotFollowed(patientId);

        return saved;
    }

    // tutte le assuzioni di un paziente
    public List<MedicineIntake> getByPatient(Integer patientId){
        return medicineIntakeRepository.findByPatientId(patientId);
    }

    // assunzioni in un range di date
    public List<MedicineIntake> getByPatientAndDateRange(Integer patientId, LocalDateTime from, LocalDateTime to){
        return medicineIntakeRepository
                .findByPatientIdAndDateTimeBetween(patientId, from, to);
    }

    // check quantita assunta = a quella prescritta
    public boolean checkConsistencyWithTherapy(MedicineIntake intake){
        Therapy therapy = intake.getTherapy();
        if(therapy == null) return false;

        // verifica che il farmaco corrisponda
        boolean medicineMatches = intake.getTherapy().getMedicine()
                .equals(therapy.getMedicine());

        // verifica che la quantita corrisponda
        boolean quantityMathces = intake.getQuantity().equals(therapy.getQuantity());

        return medicineMatches && quantityMathces;
    }

    // controlla se il paziente non assume farmaci da piu di 3 giorni consec
    public boolean hasMissedIntakesFor3Days(Integer patientId){
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        LocalDateTime now = LocalDateTime.now();

        for(int i = 1; i <= 3; i++){
            LocalDateTime dayStart = now.minusDays(i).toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = now.minusDays(i).toLocalDate().atTime(23, 59, 59);

            List<MedicineIntake> intakesForDay = medicineIntakeRepository
                    .findByPatientIdAndDateTimeBetween(patientId, dayStart, dayEnd);

            if(!intakesForDay.isEmpty()) return false;
        }
        return true;
    }

    // ritorna le assunzioni non coerenti con la terapia
    public List<MedicineIntake> getInconsistentIntakes(Integer patientId){
        return medicineIntakeRepository.findByPatientId(patientId)
                .stream()
                .filter(intake -> !intake.getMatchesTherapy())
                .collect(Collectors.toList());
    }
}
