package it.univr.DiabetesLogger.schedule;

import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.repository.PatientRepository;
import it.univr.DiabetesLogger.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyAlertsScheduler {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 20 * * *")
    public void runDailyChecks() {
        List<Patient> patients = patientRepository.findAll();

        for(Patient patient: patients){
            try {
                notificationService.checkMissedMedicine(patient.getId());
                notificationService.checkTherapyNotFollowed(patient.getId());
            } catch(Exception e) {
                System.err.println("Errore controllo giornaliero paziente " + patient.getId() + ": " + e.getMessage());
            }
        }
    }
}
