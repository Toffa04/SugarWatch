package it.univr.DiabetesLogger.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.univr.DiabetesLogger.model.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @JsonIgnore
    @Column (name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean verified = false;

    public User(String username, String email, String password, Role role){
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    protected User() {

    }

    public void setId(int id){
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return this.email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return this.password;
    }

    public Role getRole(){ return role; }

    public void setRole(Role role){ this.role = role; }

    public Boolean isVerified() { return verified; }

    public void setVerified(Boolean verified) { this.verified = verified; }

}
