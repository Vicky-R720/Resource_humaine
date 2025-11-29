package com.itu.gest_emp.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itu.gest_emp.model.Appliance;
import com.itu.gest_emp.model.Notification;
import com.itu.gest_emp.model.Person;
import com.itu.gest_emp.repository.NotificationRepository;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public Notification createNotification(Person person, String message,Appliance appliance) {
        Notification notification = new Notification(person, message,appliance);
        return notificationRepository.save(notification);
    }

    
    
    public List<Notification> getNotificationsByPerson(Person person) {
        return notificationRepository.findByPersonOrderByCreatedAtAsc(person);
    }
    
   
    
  
    
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}