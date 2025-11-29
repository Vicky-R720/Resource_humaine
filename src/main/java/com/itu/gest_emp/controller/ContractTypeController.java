package com.itu.gest_emp.controller;

import com.itu.gest_emp.model.ContractType;
import com.itu.gest_emp.service.ContractTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contract-types")
public class ContractTypeController {
    
    @Autowired
    private ContractTypeService contractTypeService;
    
    // GET - Récupérer tous les types de contrat
    @GetMapping
    public ResponseEntity<List<ContractType>> getAllContractTypes() {
        try {
            List<ContractType> contractTypes = contractTypeService.getAllContractTypes();
            if (contractTypes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(contractTypes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // GET - Récupérer un type de contrat par son ID
    @GetMapping("/{id}")
    public ResponseEntity<ContractType> getContractTypeById(@PathVariable("id") Long id) {
        Optional<ContractType> contractTypeData = contractTypeService.getContractTypeById(id);
        return contractTypeData.map(contractType -> new ResponseEntity<>(contractType, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // GET - Récupérer un type de contrat par son nom
    @GetMapping("/name/{name}")
    public ResponseEntity<ContractType> getContractTypeByName(@PathVariable("name") String name) {
        Optional<ContractType> contractTypeData = contractTypeService.getContractTypeByName(name);
        return contractTypeData.map(contractType -> new ResponseEntity<>(contractType, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // POST - Créer un nouveau type de contrat
    @PostMapping
    public ResponseEntity<ContractType> createContractType(@RequestBody ContractType contractType) {
        try {
            // Vérifier si le nom existe déjà
            if (contractTypeService.existsByName(contractType.getName())) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
            
            ContractType newContractType = contractTypeService.createContractType(contractType);
            return new ResponseEntity<>(newContractType, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // PUT - Mettre à jour un type de contrat
    @PutMapping("/{id}")
    public ResponseEntity<ContractType> updateContractType(@PathVariable("id") Long id, @RequestBody ContractType contractType) {
        try {
            ContractType updatedContractType = contractTypeService.updateContractType(id, contractType);
            return new ResponseEntity<>(updatedContractType, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // DELETE - Supprimer un type de contrat
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteContractType(@PathVariable("id") Long id) {
        try {
            contractTypeService.deleteContractType(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}