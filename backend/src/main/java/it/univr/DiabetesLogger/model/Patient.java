package it.univr.DiabetesLogger.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "birthDate")
    private LocalDate birthDate;

    @Column
    private Boolean isSmoker;

    @Column
    private Boolean isExSmoker;

    @Column
    private Boolean hasAlcoholDependency;

    @Column
    private Boolean hasObesity;

    @Column
    private String medicalHistory;

    @ManyToOne
    @JoinColumn(name = "medic_id", referencedColumnName = "id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Medic referralMedic;

    @OneToOne
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<GlycemiaReading> glycemiaReadings = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MedicineIntake> medicineIntakes = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Symptom> symptoms = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Therapy> therapies = new ArrayList<>();

    protected Patient(){

    }

    public Patient(User user, String firstName, String lastName, LocalDate birthDate, Medic referralMedic){
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.referralMedic = referralMedic;
    }

    public Patient(User user, String firstName, String lastName, LocalDate birthDate, Boolean isSmoker, Boolean isExSmoker, Boolean hasAlcoholDependency, Boolean hasObesity, String medicalHistory, Medic referralMedic){
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.isSmoker = isSmoker;
        this.isExSmoker = isExSmoker;
        this.hasAlcoholDependency = hasAlcoholDependency;
        this.hasObesity = hasObesity;
        this.medicalHistory = medicalHistory;
        this.referralMedic = referralMedic;
    }

    public void updatePatient(Patient patient) throws IllegalArgumentException{
        String firstName = patient.getFirstName();
        if(firstName != null){
            setFirstName(firstName);
        }

        String lastName = patient.getLastName();
        if(lastName != null){
            setLastName(lastName);
        }

        setReferralMedic(patient.getReferralMedic());
    }

    public Integer getId(){
        return id;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String name){
        this.firstName = name;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String surname){
        this.lastName = surname;
    }

    public void setBirthDate(LocalDate birthDate){ this.birthDate = birthDate;}

    public LocalDate getBirthDate(){return this.birthDate;}

    public Medic getReferralMedic(){
        return referralMedic;
    }

    public void setReferralMedic(Medic medic){
        this.referralMedic = medic;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String toString(){
        return "Patient [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", referralMedic=" + referralMedic + ", user=" + user + "]";
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public Boolean isSmoker() { return isSmoker; }

    public void setSmoker(Boolean smoker) { isSmoker = smoker;}

    public Boolean isExSmoker() { return isExSmoker; }

    public void setExSmoker(Boolean exSmoker) { isExSmoker = exSmoker; }

    public Boolean getHasAlcoholDependency() { return hasAlcoholDependency; }

    public void setHasAlcoholDependency(Boolean hasAlcoholDependency) {
        this.hasAlcoholDependency = hasAlcoholDependency;
    }

    public Boolean getHasObesity() { return hasObesity; }

    public void setHasObesity(Boolean hasObesity) { this.hasObesity = hasObesity; }

    public String getMedicalHistory() { return medicalHistory; }

    public void setMedicalHistory(String medicalHistory){
        this.medicalHistory = medicalHistory;
    }

}
