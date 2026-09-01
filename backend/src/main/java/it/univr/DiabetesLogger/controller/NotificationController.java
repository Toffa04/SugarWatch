package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.Notification;
import it.univr.DiabetesLogger.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.MutuallyExclusiveConfigurationPropertiesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // GET /notification/user/{userId} -> MEDIC o PATIENT
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable Integer userId){
        if(userId == null || userId <= 0){
            return ResponseEntity.badRequest().body("Id utente non valido");
        }

        try{
            List<Notification> notification = notificationService.getByUser(userId);
            if(notification.isEmpty()){
                return ResponseEntity.ok(notification); // modificato perche il backend restituisce 200 ok ma il body era una stringa semplice
            }
            return ResponseEntity.ok(notification);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle notifiche");
        }
    }

    // GET /notification/user/{userId}/unread -> MEDIC o PATIENT
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<?> getUnreadByUser(@PathVariable Integer userId){
        if(userId == null || userId <= 0){
            return ResponseEntity.badRequest().body("Id utente non valido");
        }

        try{
            List<Notification> notifications = notificationService.getUnreadByUser(userId);
            if(notifications.isEmpty()){
                return ResponseEntity.ok("Nessuna notifica non letta");
            }
            return ResponseEntity.ok(notifications);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle notifiche non lette");
        }
    }

    // GET /notification/{id} -> MEDIC o PATIENT
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id){
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id notifica non valido");
        }

        try{
            Optional<Notification> notification = notificationService.getById(id);
            if(notification.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Notifica non trovata");
            }
            return ResponseEntity.ok(notification.get());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero della notifica");
        }
    }

    // PATCH /notification/{id}/seen -> MEDIC o PATIENT
    @PatchMapping("/{id}/seen")
    public ResponseEntity<?> markAsSeen(@PathVariable Integer id){
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id notifica non valido");
        }

        try{
            Notification notification = notificationService.markAsSeen(id);
            return ResponseEntity.ok(notification);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la lettura della notifica");
        }
    }

    // PATCH /notification/user/{userId}/seen -> segna tutte come lette
    @PatchMapping("user/{userId}/seen")
    public ResponseEntity<?> markAllAsSeen(@PathVariable Integer userId){
        if(userId == null || userId<= 0){
            return ResponseEntity.badRequest().body("Id utente non valido");
        }

        try{
            List<Notification> unread = notificationService.getUnreadByUser(userId);
            if(unread.isEmpty()){
                return ResponseEntity.ok("Nessuna notifica da segnare come letta");
            }
            unread.forEach(n -> notificationService.markAsSeen(n.getId()));
            return ResponseEntity.ok("Tutte le notifiche segnate come lette");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la lettura delle notifiche");
        }
    }

    // DELETE /notification/{id} -> MEDIC o PATIENT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Integer id){
        if(id == null || id <= 0){
            return ResponseEntity.badRequest().body("Id notifica non valido");
        }

        try{
            notificationService.delete(id);
            return ResponseEntity.ok("Notifica eliminata con successo");
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione della notifica");
        }
    }
}
