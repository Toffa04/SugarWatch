package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserId(Integer userId);
    List<Notification> findByUserIdAndSeen(Integer userId, Boolean seen);
}
