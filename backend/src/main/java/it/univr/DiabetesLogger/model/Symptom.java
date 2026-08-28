package it.univr.DiabetesLogger.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "symptoms")
public class Symptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String description;  // nausea, spossatezza ecc...

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate; // null = sintomo ancora presente

    @Column
    private String notes;

    protected Symptom(){}

    public Symptom(Patient patient, LocalDate startDate, LocalDate endDate, String notes){
        this.patient = patient;
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
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
