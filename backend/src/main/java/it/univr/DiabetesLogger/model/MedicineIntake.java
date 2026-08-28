package it.univr.DiabetesLogger.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medicine_intakes")
public class MedicineIntake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Therapy therapy;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Boolean matchesTherapy;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    protected MedicineIntake(){}

    public MedicineIntake(Patient patient, Therapy therapy, Double quantity, Boolean matchesTherapy, LocalDateTime dateTime){
        this.patient = patient;
        this.therapy = therapy;
        this.quantity = quantity;
        this.matchesTherapy = matchesTherapy;
        this.dateTime = dateTime;
    }

    public Integer getId(){
        return id;
    }

    public Patient getPatient(){
        return patient;
    }

    public void setPatient(Patient patient){
        this.patient = patient;
    }

    public Therapy getTherapy(){
        return therapy;
    }

    public void setTherapy(Therapy therapy){
        this.therapy = therapy;
    }

    public Double getQuantity(){
        return quantity;
    }

    public void setQuantity(Double quantity){
        this.quantity = quantity;
    }

    public Boolean getMatchesTherapy(){
        return matchesTherapy;
    }

    public void setMatchesTherapy(Boolean matchesTherapy){
        this.matchesTherapy = matchesTherapy;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
