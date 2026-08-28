package it.univr.DiabetesLogger.model;

import it.univr.DiabetesLogger.model.enums.TherapyStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "therapies")
public class Therapy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Medic medic;

    @Column(name = "medicine")
    private String medicine;

    @Column(name = "dosesPerDay")
    private Integer dosesPerDay;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "notes")
    private String notes;

    //Il medico puo modificare la terapia nel tempo
    private LocalDate startDate;
    private LocalDate endDate;

    //Il medico puo aggiungere o modificare la terapia
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TherapyStatus status = TherapyStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "last_modified_by_id")
    private Medic lastModifiedBy;

    @Column(name = "lastModifiedAt")
    private LocalDateTime lastModifiedAt;

    protected Therapy() {}

    public Therapy(String medicine, Integer dosesPerDay, Double quantity, String notes){
        this.medicine = medicine;
        this.dosesPerDay = dosesPerDay;
        this.quantity = quantity;
        this.notes = notes;
    }

    public void updateTherapy(Therapy therapy){
        String medicine = therapy.getMedicine();
        if(medicine != null){
            this.medicine = medicine;
        }

        Integer dosesPerDay = therapy.getDosesPerDay();
        if(dosesPerDay != null){
            this.dosesPerDay = dosesPerDay;
        }

        Double quantity = therapy.getQuantity();
        if(quantity != null){
            this.quantity = quantity;
        }

        String notes = therapy.getNotes();
        if(notes != null){
            this.notes = notes;
        }
    }

    public Integer getId(){
        return id;
    }

    public String getMedicine(){
        return medicine;
    }

    public Integer getDosesPerDay(){
        return dosesPerDay;
    }

    public Double getQuantity(){
        return quantity;
    }

    public String getNotes(){
        return notes;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public void setMedicine(String medicine){
        this.medicine = medicine;
    }

    public void setDosesPerDay(Integer dosesPerDay){
        this.dosesPerDay = dosesPerDay;
    }

    public void setQuantity(Double quantity){
        this.quantity = quantity;
    }

    public void setNotes(String notes){
        this.notes = notes;
    }

    public Patient getPatient() { return patient; }

    public void setPatient(Patient patient) { this.patient = patient; }

    public Medic getMedic() { return medic; }

    public void setMedic(Medic medic) { this.medic = medic; }

    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public TherapyStatus getStatus() { return status; }

    public void setStatus(TherapyStatus status) { this.status = status; }

    public Medic getLastModifiedBy() { return lastModifiedBy; }

    public void setLastModifiedBy(Medic medic) { this.lastModifiedBy = medic; }

    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    public String toString() {
        return "Therapy: [id=" + id + ", medicine=" + medicine + ", dosesPerDay=" +
                dosesPerDay + ", quantity=" + quantity + ", notes=" + notes + "]";
    }
}
