package com.itu.gest_emp.modules.absence_conge.service;

import com.itu.gest_emp.modules.absence_conge.model.LeaveBalance;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveBalanceRepository;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Year;

@Service
public class LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveCalculationService calculationService;

    /**
     * Génère les soldes annuels automatiquement le 1er janvier
     */
    @Scheduled(cron = "0 0 0 1 1 *")
    @Transactional
    public void generateAnnualBalances() {
        int currentYear = Year.now().getValue();
        // Logique pour générer les soldes pour tous les employés
        // Report des soldes non utilisés, etc.
    }

    /**
     * Met à jour le solde après validation d'une demande
     */
    @Transactional
    public void updateBalanceAfterApproval(LeaveRequest leaveRequest) {
        LeaveBalance balance = leaveBalanceRepository
                .findByPersonnel_IdAndLeaveTypeIdAndAnnee(
                        leaveRequest.getPersonnel().getId(),
                        leaveRequest.getLeaveType().getId(),
                        leaveRequest.getDateDebut().getYear())
                .orElseThrow(() -> new RuntimeException("Solde non trouvé"));

        balance.setSoldePris(balance.getSoldePris().add(leaveRequest.getNombreJours()));
        // balance.setSoldeRestant(balance.getSoldeRestant().subtract(leaveRequest.getNombreJours()));

        leaveBalanceRepository.save(balance);
    }

    /**
     * Rétablit le solde après annulation d'une demande
     */
    @Transactional
    public void restoreBalanceAfterCancellation(LeaveRequest leaveRequest) {
        LeaveBalance balance = leaveBalanceRepository
                .findByPersonnel_IdAndLeaveTypeIdAndAnnee(
                        leaveRequest.getPersonnel().getId(),
                        leaveRequest.getLeaveType().getId(),
                        leaveRequest.getDateDebut().getYear())
                .orElseThrow(() -> new RuntimeException("Solde non trouvé"));

        balance.setSoldePris(balance.getSoldePris().subtract(leaveRequest.getNombreJours()));
        balance.setSoldeRestant(balance.getSoldeRestant().add(leaveRequest.getNombreJours()));

        leaveBalanceRepository.save(balance);
    }
}