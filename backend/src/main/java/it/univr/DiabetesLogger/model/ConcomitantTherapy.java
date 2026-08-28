package it.univr.DiabetesLogger.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "concomitant_therapies")
public class ConcomitantTherapy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String medicine; // farmaco assunto per un'altra condizione

    @Column
    private String reason; // motivazione assunzione farmaco

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private String notes;

    protected ConcomitantTherapy() {}

    public ConcomitantTherapy(Patient patient, String medicine, String reason, LocalDate startDate, LocalDate endDate, String notes){
        this.patient = patient;
        this.medicine = medicine;
        this.reason = reason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public boolean isActive(){
        return endDate == null;
    }

    public Integer getId() { return id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getMedicine() { return medicine; }
    public void setMedicine(String medicine) { this.medicine = medicine; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getStartDate() {return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() {return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
