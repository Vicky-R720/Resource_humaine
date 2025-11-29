package com.itu.gest_emp.service;

import com.itu.gest_emp.model.ContractType;
import com.itu.gest_emp.repository.ContractTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractTypeService {
    
    @Autowired
    private ContractTypeRepository contractTypeRepository;
    
    // Récupérer tous les types de contrat
    public List<ContractType> getAllContractTypes() {
        return contractTypeRepository.findAll();
    }
    
    // Récupérer un type de contrat par son ID
    public Optional<ContractType> getContractTypeById(Long id) {
        return contractTypeRepository.findById(id);
    }
    
    // Récupérer un type de contrat par son nom
    public Optional<ContractType> getContractTypeByName(String name) {
        return contractTypeRepository.findByName(name);
    }
    
    // Créer un nouveau type de contrat
    public ContractType createContractType(ContractType contractType) {
        return contractTypeRepository.save(contractType);
    }
    
    // Mettre à jour un type de contrat
    public ContractType updateContractType(Long id, ContractType contractTypeDetails) {
        Optional<ContractType> optionalContractType = contractTypeRepository.findById(id);
        if (optionalContractType.isPresent()) {
            ContractType contractType = optionalContractType.get();
            contractType.setName(contractTypeDetails.getName());
            return contractTypeRepository.save(contractType);
        } else {
            throw new RuntimeException("ContractType not found with id: " + id);
        }
    }
    
    // Supprimer un type de contrat
    public void deleteContractType(Long id) {
        if (contractTypeRepository.existsById(id)) {
            contractTypeRepository.deleteById(id);
        } else {
            throw new RuntimeException("ContractType not found with id: " + id);
        }
    }
    
    // Vérifier si un type de contrat existe
    public boolean existsByName(String name) {
        return contractTypeRepository.existsByName(name);
    }
}