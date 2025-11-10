package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.Notification;
import com.itu.gest_emp.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPersonOrderByCreatedAtAsc(Person person);
 
}