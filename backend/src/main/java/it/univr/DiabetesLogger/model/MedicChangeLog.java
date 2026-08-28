package it.univr.DiabetesLogger.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medicChangeLog")
public class MedicChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Medic medic;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Patient patient;

    @Column(name = "action")
    private String action;

    @Column(name = "entityType")
    private String entityType; // THERAPY, PATIENT_INFO, GLYCEMIA

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    protected MedicChangeLog(){}

    public MedicChangeLog(Medic medic, Patient patient, String action, String entityType, LocalDateTime timestamp){
        this.medic = medic;
        this.patient = patient;
        this.action = action;
        this.entityType = entityType;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Medic getMedic() {
        return medic;
    }

    public void setMedic(Medic medic) {
        this.medic = medic;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType(){
        return entityType;
    }

    public void setEntityType(String entityType){
        this.entityType = entityType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
