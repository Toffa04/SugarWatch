package it.univr.DiabetesLogger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "glycemia_readings")
public class GlycemiaReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer glycemiaLevel; // mg/dL

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private Boolean beforeMeal;
    // True = pre pasto, False = post pasto

    @Column()
    private String symptoms;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Patient patient;

    protected GlycemiaReading(){}

    public GlycemiaReading(Integer glycemiaLevel, LocalDateTime dateTime, Boolean beforeMeal, String symptoms, Patient patient){
        this.glycemiaLevel = glycemiaLevel;
        this.dateTime = dateTime;
        this.beforeMeal = beforeMeal;
        this.symptoms = symptoms;
        this.patient = patient;
    }

    public Boolean isValueHigh(){
        if(beforeMeal)
            return glycemiaLevel < 80 || glycemiaLevel > 130;
        else
            return glycemiaLevel > 180;
    }

    public Integer getId(){
        return id;
    }

    public Integer getGlycemiaLevel(){
        return glycemiaLevel;
    }

    public void setGlycemiaLevel(Integer glycemiaLevel){
        this.glycemiaLevel = glycemiaLevel;
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime){
        this.dateTime = dateTime;
    }

    public Boolean getBeforeMeal(){
        return beforeMeal;
    }

    public void setBeforeMeal(Boolean beforeMeal){
        this.beforeMeal = beforeMeal;
    }

    public Patient getPatient(){
        return patient;
    }

    public void setPatient(Patient patient){
        this.patient = patient;
    }

    public String getSymptoms() {return symptoms; }

    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

}
