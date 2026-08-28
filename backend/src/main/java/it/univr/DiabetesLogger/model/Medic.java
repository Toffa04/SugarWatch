package it.univr.DiabetesLogger.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medics")
public class Medic {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @OneToMany(mappedBy = "referralMedic", cascade = CascadeType.ALL)
    private Set<Patient> patients = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private User user;

    protected Medic(){

    }

    public Medic(User user, String firstName, String lastName){
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void updateMedic(Medic medic){

        String firstName = medic.getFirstName();
        if(firstName != null){
            setFirstName(firstName);
        }

        String lastName = medic.getLastName();
        if(lastName != null){
            setLastName(lastName);
        }
    }

    public String getEmail(){
        if(this.getUser() == null){
            return null;
        }
        return this.getUser().getEmail();
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

    public String toString(){
        return "Medic [id = " + id + ", firstName = " + firstName + ", lastName = " + lastName + ", user = " + user + "]";
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String surname){
        this.lastName = surname;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }
}
