package com.itu.gest_emp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itu.gest_emp.model.AnomalieDetection;
import com.itu.gest_emp.model.AttendanceRH;
import com.itu.gest_emp.model.PayslipsRH;
import com.itu.gest_emp.model.Person;
import com.itu.gest_emp.model.PersonnelRH;
import com.itu.gest_emp.repository.AnomalieDetectionRepository;
import com.itu.gest_emp.repository.AttendanceRHRepository;
import com.itu.gest_emp.repository.PayslipsRHRepository;
import com.itu.gest_emp.repository.PersonnelRHRepository;


@Service
public class AnomalyDetectionService {

    @Autowired
    private PersonnelRHRepository personnelRepository;

    @Autowired
    private AttendanceRHRepository attendanceRepository;

    @Autowired
    private PayslipsRHRepository payslipsRepository;

    @Autowired
    private AnomalieDetectionRepository anomalieDetectionRepository;

    public List<AnomalieDetection> detectAnomalies(Long personnelId) {
        List<AnomalieDetection> anomalies = new ArrayList<>();
        PersonnelRH personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        anomalies.addAll(detectHeuresAnomalies(personnel));
        anomalies.addAll(detectPaieAnomalies(personnel));

        anomalieDetectionRepository.saveAll(anomalies);
        return anomalies;
    }

    private List<AnomalieDetection> detectHeuresAnomalies(PersonnelRH personnel) {
        List<AnomalieDetection> anomalies = new ArrayList<>();

        LocalDate startDate = LocalDate.now().minusDays(30);

        List<AttendanceRH> retards = attendanceRepository.findRetardsByPersonnelAfterDate(
                personnel.getId(), startDate, LocalTime.of(8, 15));

        List<AttendanceRH> heuresSup = attendanceRepository.findHeuresSupExcessives(
                personnel.getId(), startDate, 600);

        // Détection des retards fréquents
        if (retards.size() > 5) {
            AnomalieDetection anomalie = new AnomalieDetection();
            anomalie.setPersonnel(personnel);
            anomalie.setTypeAnomalie("retards_frequents");
            anomalie.setDescription(retards.size() + " retards détectés sur 30 jours");
            anomalie.setDonneesDetectees("Seuil dépassé: >5 retards/mois");
            anomalie.setSeverite("medium");
            anomalies.add(anomalie);
        }

        // Détection des heures supplémentaires excessives
        if (heuresSup.size() > 3) {
            AnomalieDetection anomalie = new AnomalieDetection();
            anomalie.setPersonnel(personnel);
            anomalie.setTypeAnomalie("heures_supp_excessives");
            anomalie.setDescription(heuresSup.size() + " jours avec >10h de travail");
            anomalie.setDonneesDetectees("Risque de burn-out détecté");
            anomalie.setSeverite("high");
            anomalies.add(anomalie);
        }

        // Détection des absences non justifiées
        List<AttendanceRH> absences = attendanceRepository.findByPersonnelIdAndDatePointageAfter(
                personnel.getId(), startDate);

        long absencesCount = absences.stream()
                .filter(a -> "absent".equals(a.getStatut()))
                .count();

        if (absencesCount > 3) {
            AnomalieDetection anomalie = new AnomalieDetection();
            anomalie.setPersonnel(personnel);
            anomalie.setTypeAnomalie("absences_frequentes");
            anomalie.setDescription(absencesCount + " absences sur 30 jours");
            anomalie.setDonneesDetectees("Vérifier les justificatifs");
            anomalie.setSeverite("medium");
            anomalies.add(anomalie);
        }

        return anomalies;
    }

 

        private List<AnomalieDetection> detectPaieAnomalies(PersonnelRH personnel) {
            List<AnomalieDetection> anomalies = new ArrayList<>();

            List<PayslipsRH> payslips = payslipsRepository
                    .findTop3ByPersonnelIdOrderByAnneeDescMoisDesc(personnel.getId());

            if (payslips.size() >= 2) {
                PayslipsRH dernier = payslips.get(0);
                PayslipsRH avantDernier = payslips.get(1);

                if (dernier.getNetAPayer() != null && avantDernier.getNetAPayer() != null &&
                        avantDernier.getNetAPayer() > 0) {

                    // CORRECTION : Convertir Double en BigDecimal pour les calculs
                    BigDecimal dernierNet = BigDecimal.valueOf(dernier.getNetAPayer());
                    BigDecimal avantDernierNet = BigDecimal.valueOf(avantDernier.getNetAPayer());

                    BigDecimal variation = dernierNet
                            .subtract(avantDernierNet)
                            .divide(avantDernierNet, 2, RoundingMode.HALF_UP);

                    if (variation.compareTo(new BigDecimal("-0.10")) < 0) {
                        AnomalieDetection anomalie = new AnomalieDetection();
                        anomalie.setPersonnel(personnel);
                        anomalie.setTypeAnomalie("baisse_salaire_anormale");
                        anomalie.setDescription(
                                "Baisse de " + variation.multiply(new BigDecimal(100)) + "% du salaire net");
                        anomalie.setDonneesDetectees("Vérifier les retenues exceptionnelles");
                        anomalie.setSeverite("medium");
                        anomalies.add(anomalie);
                    }
                }
            }

            // Détection IRSA anormal
            for (PayslipsRH payslip : payslips) {
                if (payslip.getIrsa() != null && payslip.getTotalBrut() != null &&
                        payslip.getTotalBrut() > 0) {

                    // CORRECTION : Convertir Double en BigDecimal pour les calculs
                    BigDecimal irsa = BigDecimal.valueOf(payslip.getIrsa());
                    BigDecimal totalBrut = BigDecimal.valueOf(payslip.getTotalBrut());

                    BigDecimal tauxIRSA = irsa.divide(totalBrut, 4, RoundingMode.HALF_UP);

                    if (tauxIRSA.compareTo(new BigDecimal("0.20")) > 0) {
                        AnomalieDetection anomalie = new AnomalieDetection();
                        anomalie.setPersonnel(personnel);
                        anomalie.setTypeAnomalie("taux_irsa_anormal");
                        anomalie.setDescription("Taux d'IRSA anormal: " + tauxIRSA.multiply(new BigDecimal(100)) + "%");
                        anomalie.setDonneesDetectees("Vérifier le calcul de l'IRSA");
                        anomalie.setSeverite("low");
                        anomalies.add(anomalie);
                        break;
                    }
                }
            }

            return anomalies;
        }

        // ... autres méthodes ...
    

    // CORRECTION : Supprimer les méthodes problématiques avec Double
    // et les remplacer par des méthodes utilisant BigDecimal

    public List<AnomalieDetection> getAnomaliesFiltrees(String severite, String statut, String type) {
        if (severite != null && !severite.isEmpty()) {
            if (statut != null && !statut.isEmpty()) {
                return anomalieDetectionRepository.findBySeveriteAndStatut(severite, statut);
            }
            return anomalieDetectionRepository.findBySeverite(severite);
        } else if (statut != null && !statut.isEmpty()) {
            return anomalieDetectionRepository.findByStatut(statut);
        }
        return anomalieDetectionRepository.findAll();
    }

    public List<AnomalieDetection> getAnomaliesNonResolues(Long personnelId) {
        return anomalieDetectionRepository.findByPersonnelIdAndStatut(personnelId, "detecte");
    }

    public void resoudreAnomalie(Long anomalieId, String commentaire, Person investigateur) {
        AnomalieDetection anomalie = anomalieDetectionRepository.findById(anomalieId)
                .orElseThrow(() -> new RuntimeException("Anomalie non trouvée"));

        anomalie.setStatut("resolu");
        anomalie.setInvestigatedBy(investigateur);
        anomalie.setInvestigationDate(LocalDateTime.now());
        anomalie.setResolutionComment(commentaire);

        anomalieDetectionRepository.save(anomalie);
    }

    // SUPPRIMER les méthodes problématiques suivantes si elles existent :
    // - getSalairePersonnel() qui retourne Double
    // - getSalaireMoyenPoste() qui retourne Double
    // - Toute méthode qui utilise Double au lieu de BigDecimal
}