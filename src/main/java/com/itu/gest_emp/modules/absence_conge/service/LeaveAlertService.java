package com.itu.gest_emp.modules.absence_conge.service;

import com.itu.gest_emp.modules.absence_conge.model.LeaveBalance;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveBalanceRepository;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;
import com.itu.gest_emp.modules.shared.model.NotificationRh;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.service.NotificationRhService;
import com.itu.gest_emp.modules.shared.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveAlertService {
    @Autowired
    private PersonService personService;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private NotificationRhService notificationService;

    /**
     * Alerte pour congés non validés après 48h
     */
    @Scheduled(cron = "0 50 20 * * *", zone = "Indian/Antananarivo") // Tous les jours à 9h
    public void checkPendingRequests() {
        List<LeaveRequest> pendingRequests = leaveRequestRepository.findByStatut("en_attente");

        for (LeaveRequest request : pendingRequests) {
            long hoursPending = ChronoUnit.HOURS.between(request.getCreatedAt(), LocalDateTime.now());

            if (hoursPending > 48) {
                // Notification au manager
                NotificationRh notification = new NotificationRh();
                notification.setTitle("Demande de congé en attente depuis plus de 48h");
                notification.setMessage(String.format(
                        "La demande de congé de %s est en attente de validation depuis le %s",
                        request.getPersonnel().getPerson().getFullName(),
                        request.getCreatedAt()));
                notification.setType("leave_reminder");
                Person superior = personService.getPersonById(13L).get();
                notification.setRecipient(superior);

                notificationService.createNotification(notification);
            }
        }
    }

    /**
     * Alerte pour soldes négatifs
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "Indian/Antananarivo") // Tous les jours à 8h
    public void checkNegativeBalances() {
        List<LeaveBalance> balances = leaveBalanceRepository.findAll();

        for (LeaveBalance balance : balances) {
            if (balance.getSoldeRestant().compareTo(BigDecimal.ZERO) < 0) {
                // Notification à l'employé et au manager
                NotificationRh notification = new NotificationRh();
                notification.setTitle("Solde de congé négatif");
                notification.setMessage(String.format(
                        "Votre solde de %s est négatif: %s jours",
                        balance.getLeaveType().getName(),
                        balance.getSoldeRestant()));
                notification.setType("negative_balance");
                notification.setRecipient(balance.getPersonnel().getPerson());
                notificationService.createNotification(notification);
            }
        }
    }

    /**
     * Alerte pour absences répétées
     */
    @Scheduled(cron = "0 0 7 1 * *", zone = "Indian/Antananarivo") // Le 1er de chaque mois à 7h
    public void checkRepeatedAbsences() {
        // Implémentation de la détection d'absences répétées
        // (>3 absences en 1 mois)
    }
}