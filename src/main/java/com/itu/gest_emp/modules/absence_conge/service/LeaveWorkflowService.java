package com.itu.gest_emp.modules.absence_conge.service;

import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;
import com.itu.gest_emp.modules.shared.model.NotificationRh;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.service.NotificationRhService;
import com.itu.gest_emp.modules.shared.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class LeaveWorkflowService {

    @Autowired
    private PersonService personService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private NotificationRhService notificationService;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    /**
     * Soumet une demande de congé et notifie le manager
     */
    @Transactional
    public LeaveRequest submitLeaveRequest(LeaveRequest leaveRequest) {
        leaveRequest.setStatut("en_attente");
        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        // Notification au manager
        NotificationRh notification = new NotificationRh();
        notification.setTitle("Nouvelle demande de congé");
        notification.setMessage(String.format(
                "L'employé %s a soumis une demande de congé du %s au %s",
                leaveRequest.getPersonnel().getPerson().getFullName(),
                leaveRequest.getDateDebut(),
                leaveRequest.getDateFin()));
        notification.setTitle("leave_request");
        Person superior = personService.getPersonById(13L).get();
        notification.setRecipient(superior); // ID du manager

        notificationService.createNotification(notification);

        return savedRequest;
    }

    /**
     * Approuve une demande de congé
     */
    @Transactional
    public LeaveRequest approveLeaveRequest(Long requestId, Long validatorId, String comment) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        Person superior = personService.getPersonById(validatorId).get();
        request.setStatut("approuve");
        request.setValidatedBy(superior);
        request.setValidationDate(LocalDateTime.now());
        request.setValidationComment(comment);

        // Mise à jour du solde
        leaveBalanceService.updateBalanceAfterApproval(request);

        // Notification à l'employé
        NotificationRh notification = new NotificationRh();
        notification.setTitle("Demande de congé approuvée");
        notification.setMessage("Votre demande de congé a été approuvée");
        notification.setTitle("leave_approval");
        notification.setRecipient(request.getPersonnel().getPerson());

        notificationService.createNotification(notification);

        return leaveRequestRepository.save(request);
    }

    /**
     * Refuse une demande de congé
     */
    @Transactional
    public LeaveRequest rejectLeaveRequest(Long requestId, Long validatorId, String comment) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        Person superior = personService.getPersonById(validatorId).get();
        request.setStatut("refuse");
        request.setValidatedBy(superior);
        request.setValidationDate(LocalDateTime.now());
        request.setValidationComment(comment);

        // Notification à l'employé
        NotificationRh notification = new NotificationRh();
        notification.setTitle("Demande de congé refusée");
        notification.setMessage("Votre demande de congé a été refusée: " + comment);
        notification.setTitle("leave_rejection");
        notification.setRecipient(request.getPersonnel().getPerson());

        notificationService.createNotification(notification);

        return leaveRequestRepository.save(request);
    }
}