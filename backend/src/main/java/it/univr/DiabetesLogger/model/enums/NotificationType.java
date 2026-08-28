package it.univr.DiabetesLogger.model.enums;

public enum NotificationType {
    MISSED_MEDICINE,  // paziente ha dimenticato il farmaco
    THERAPY_NOT_FOLLOWED,  // medico: paziente non segue la terapia da 3+ giorni
    HIGH_GLYCEMIA_WARNING,  // medico: glicemia lievemente alta
    HIGH_GLYCEMIA_ALERT, // medico: glicemia motlo alta
    INCONSISTEN_MEDICINE, // assunzione non coerente
    LOW_GLYCEMIA_WARNING, // medico: glicemia lievemente bassa
    LOW_GLYCEMIA_ALERT // medico: glicemia motlo bassa
}
