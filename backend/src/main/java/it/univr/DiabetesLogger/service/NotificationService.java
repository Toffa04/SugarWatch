package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.*;
import it.univr.DiabetesLogger.model.enums.NotificationType;
import it.univr.DiabetesLogger.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService implements CrudService<Notification>{

    private static final int BEFORE_MEAL_MIN = 80;
    private static final int BEFORE_MEAL_MAX = 130;
    private static final int AFTER_MEAL_MAX = 180;

    // soglia per alert grave (es. 250 mg/dL)
    private static final int HIGH_GLYCEMIA_ALERT_THRESHOLD = 250;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicineIntakeRepository medicineIntakeRepository;

    @Autowired
    private GlycemiaReadingRepository glycemiaReadingRepository;

    @Override
    public Notification create(Notification entity){
        return notificationRepository.save(entity);
    }

    @Override
    public Iterable<Notification> getAll(){
        return notificationRepository.findAll();
    }

    @Override
    public Optional<Notification> getById(Integer notificationId){
        return notificationRepository.findById(notificationId);
    }

    @Override
    public Notification update(Integer notificationId, Notification entity){
        Notification existing = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notifica non trovata"));

        if(entity.getMessage() != null){
            existing.setMessage(entity.getMessage());
        }
        if(entity.getSeen() != null){
            existing.setSeen(entity.getSeen());
        }

        return notificationRepository.save(existing);
    }

    public void delete(Integer notificationId){
        notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notifica non trovata"));

        notificationRepository.deleteById(notificationId);
    }

    // tutte le notifiche di un utente
    public List<Notification> getByUser(Integer userId){
        return notificationRepository.findByUserId(userId);
    }

    // notifiche non lette da un utente
    public List<Notification> getUnreadByUser(Integer userId){
        return notificationRepository.findByUserIdAndSeen(userId, false);
    }

    // segna una notifica come letta
    public Notification markAsSeen(Integer id){
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notifica non trovat"));

        notification.setSeen(true);
        return notificationRepository.save(notification);
    }

    // crea una notifica generica
    public Notification createNotification(String message, NotificationType type, User user){
        Notification notification = new Notification(message, false, LocalDateTime.now(), user);
        notification.setType(type);
        return notificationRepository.save(notification);
    }

    // MISSED_MEDICINE -> avvisa il paziente che ha dimenticato il farmaco
    public void checkMissedMedicine(Integer patientId){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        LocalDateTime now = LocalDateTime.now();

        List<MedicineIntake> recentIntakes = medicineIntakeRepository
                .findByPatientIdAndDateTimeBetween(patientId, oneDayAgo, now);

        if(recentIntakes.isEmpty()){
            createNotification(
                    "Ricorda di assumere i farmaci prescritti!",
                    NotificationType.MISSED_MEDICINE,
                    patient.getUser()

            );
        }
    }

    // THERAPY_NOT_FOLLOWED -> avvisa il medico se paziente non segua la terapia da 3+ giorni
    public void checkTherapyNotFollowed(Integer patientId){
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        LocalDateTime now = LocalDateTime.now();

        List<MedicineIntake> recentIntakes = medicineIntakeRepository
                .findByPatientIdAndDateTimeBetween(patientId, threeDaysAgo, now);

        boolean notFollowing = recentIntakes.isEmpty() ||
                recentIntakes.stream().noneMatch(MedicineIntake::getMatchesTherapy);

        if(notFollowing && patient.getReferralMedic() != null){
            createNotification(
                    "Il paziente " + patient.getFirstName() + " " +
                            patient.getLastName() + " non segue la terapia da 3+ giorni!",
                    NotificationType.THERAPY_NOT_FOLLOWED,
                    patient.getReferralMedic().getUser()
            );
        }
    }

    // HIGH_GLYCEMIA_WARNING e HIGH_GLYCEMIA_ALERT → avvisa il medico
    public void checkGlycemiaAlerts(Integer patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paziente non trovato"));

        if (patient.getReferralMedic() == null) return;

        List<GlycemiaReading> readings =
                glycemiaReadingRepository.findByPatientId(patientId);

        readings.stream()
                .filter(GlycemiaReading::isValueHigh)
                .forEach(reading -> {

                    // distingue tra warning e alert grave
                    boolean isCritical = reading.getGlycemiaLevel()
                            > HIGH_GLYCEMIA_ALERT_THRESHOLD;

                    if (isCritical) {
                        createNotification(
                                "ALERT: Il paziente " + patient.getFirstName() + " " +
                                        patient.getLastName() + " ha una glicemia critica: " +
                                        reading.getGlycemiaLevel() + " mg/dL!",
                                NotificationType.HIGH_GLYCEMIA_ALERT,
                                patient.getReferralMedic().getUser()
                        );
                    } else {
                        createNotification(
                                "Attenzione: Il paziente " + patient.getFirstName() + " " +
                                        patient.getLastName() + " ha una glicemia elevata: " +
                                        reading.getGlycemiaLevel() + " mg/dL.",
                                NotificationType.HIGH_GLYCEMIA_WARNING,
                                patient.getReferralMedic().getUser()

                        );
                    }
                });
    }

    // rilevazioni storiche gia' esaminate in passato
    public void checkGlycemiaAlertForReading(GlycemiaReading reading){
        if(reading == null || !reading.isValueHigh()) return;

        Patient patient = reading.getPatient();
        if(patient == null || patient.getReferralMedic() == null) return;

        boolean isCritical = reading.getGlycemiaLevel() > HIGH_GLYCEMIA_ALERT_THRESHOLD;

        if(isCritical) {
            createNotification(
                    "ALERT: Il paziente " + patient.getFirstName() + " " +
                                patient.getLastName() + " ha un livello di glicemia critica: " +
                                reading.getGlycemiaLevel() + " mg/dL!",
                    NotificationType.HIGH_GLYCEMIA_ALERT,
                    patient.getReferralMedic().getUser()
            );
        } else {
            createNotification(
                    "Attenzione: Il paziente " + patient.getFirstName() + " " +
                                patient.getLastName() + " ha un livello di glicemia elevata: " +
                                reading.getGlycemiaLevel() + " mg/dL.",
                    NotificationType.HIGH_GLYCEMIA_WARNING,
                    patient.getReferralMedic().getUser()
            );
        }
    }

    // controlla tutti gli alert per un paziente in un unico metodo
    public void checkAllAlerts(Integer patientId){
        checkMissedMedicine(patientId);
        checkTherapyNotFollowed(patientId);
        checkGlycemiaAlerts(patientId);
    }

}
