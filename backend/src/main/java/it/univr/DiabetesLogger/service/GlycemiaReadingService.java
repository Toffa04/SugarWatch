package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.controller.GlycemiaReadingController;
import it.univr.DiabetesLogger.model.GlycemiaReading;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.repository.GlycemiaReadingRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GlycemiaReadingService implements CrudService<GlycemiaReading>{

    @Autowired
    private GlycemiaReadingRepository glycemiaReadingRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NotificationService notificationService;

    private static final int BEFORE_MEAL_MIN = 80;
    private static final int BEFORE_MEAL_MAX = 130;
    private static final int AFTER_MEAL_MAX = 180;

    @Override
    public GlycemiaReading create(GlycemiaReading entity){
        return glycemiaReadingRepository.save(entity);
    }

    @Override
    public Iterable<GlycemiaReading> getAll(){
        return glycemiaReadingRepository.findAll();
    }

    @Override
    public Optional<GlycemiaReading> getById(Integer id){
        return glycemiaReadingRepository.findById(id);
    }

    @Override
    public GlycemiaReading update(Integer id, GlycemiaReading entity){
        GlycemiaReading existing = glycemiaReadingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rilevazione non trovata"));

        if (entity.getGlycemiaLevel() != null) {
            existing.setGlycemiaLevel(entity.getGlycemiaLevel());
        }
        if (entity.getDateTime() != null) {
            existing.setDateTime(entity.getDateTime());
        }
        if (entity.getBeforeMeal() != null) {
            existing.setBeforeMeal(entity.getBeforeMeal());
        }
        if (entity.getSymptoms() != null) {
            existing.setSymptoms(entity.getSymptoms());
        }
        if(entity.getSymptoms() != null){
            existing.setSymptoms(entity.getSymptoms());
        }

        return glycemiaReadingRepository.save(existing);
    }

    @Override
    public void delete(Integer id){
        glycemiaReadingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rilevazione non trovata"));

        glycemiaReadingRepository.deleteById(id);
    }

    // tutte le rilevazioni di un paziente
    public List<GlycemiaReading> getByPatient(Integer patientId){
        return glycemiaReadingRepository.findByPatientId(patientId);
    }

    // rilvevazioni di un paziente in un range di date
    public List<GlycemiaReading> getByPatientAndDateRange(Integer patientId, LocalDateTime from, LocalDateTime to){
        return glycemiaReadingRepository.findByPatientIdAndDateTimeBetween(patientId, from, to);
    }

    // Andamento settimana per settimana
    public List<GlycemiaReading> getByPatientAndWeek(Integer patientId, LocalDate week){
        LocalDateTime from = week.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime to = week.with(DayOfWeek.SUNDAY).atTime(23, 59, 59);
        return glycemiaReadingRepository
                .findByPatientIdAndDateTimeBetween(patientId, from, to);
    }

    // Andamento mese per mese
    public List<GlycemiaReading> getPatientAndMonth(Integer patientId, YearMonth month){
        LocalDateTime from = month.atDay(1).atStartOfDay();
        LocalDateTime to = month.atEndOfMonth().atTime(23, 59, 59);
        return glycemiaReadingRepository
                .findByPatientIdAndDateTimeBetween(patientId, from, to);
    }

    // Check supero soglie per rilevazione
    public boolean isAboveThreshold(GlycemiaReading reading){
        if(reading.getBeforeMeal()){
            return reading.getGlycemiaLevel() < BEFORE_MEAL_MIN ||
                    reading.getGlycemiaLevel() > BEFORE_MEAL_MAX;
        } else {
            return reading.getGlycemiaLevel() > AFTER_MEAL_MAX;
        }
    }

    // Ritorna tutte le rilevazioni oltre la soglia di una paziente
    public List<GlycemiaReading> getAboveThresholdByPatient(Integer patientId){
        return glycemiaReadingRepository.findByPatientId(patientId)
                .stream()
                .filter(GlycemiaReading::isValueHigh)
                .collect(Collectors.toList());
    }

    // creazione rilevazione dato il patientId
    public GlycemiaReading createForPatient(Integer patientId, GlycemiaReadingController.GlycemiaReadingRequest request){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));
        GlycemiaReading reading = new GlycemiaReading(request.getGlycemiaLevel(), request.getDateTime(), request.getBeforeMeal(), request.getSymptoms(), patient);
        GlycemiaReading saved = glycemiaReadingRepository.save(reading);

        //notificationService.checkGlycemiaAlerts(patientId);
        notificationService.checkGlycemiaAlertForReading(saved);

        return saved;
    }
}
