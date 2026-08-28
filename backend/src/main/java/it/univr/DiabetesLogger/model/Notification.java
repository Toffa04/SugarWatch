package it.univr.DiabetesLogger.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.univr.DiabetesLogger.model.enums.NotificationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column
    private String message;

    @Column(name = "seen")
    private Boolean seen;

    @Column(name = "time")
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "id", nullable = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    protected Notification(){}

    public Notification(String message, Boolean seen, LocalDateTime time, User user){
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.user = user;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public Boolean isSeen(){
        return seen;
    }

    public Boolean getSeen() { return seen; }
    public void setSeen(Boolean seen){
        this.seen = seen;
    }

    public LocalDateTime getTime(){
        return time;
    }

    public void setTime(LocalDateTime time){
        this.time = time;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void toggleSeen(){
        this.setSeen(!this.isSeen());
    }

    public String toString(){
        return "Notification [id=" + id + ", message=" + message + ", seen=" + seen + ", time=" + time + ", user="
                + user + ", getId()=" + getId() + "]";
    }

    public NotificationType getType() { return type; }

    public void setType(NotificationType type) { this.type = type; }
}
