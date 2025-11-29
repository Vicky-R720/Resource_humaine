package com.itu.gest_emp.service;

import com.itu.gest_emp.model.Matching;
import com.itu.gest_emp.model.Person;
import com.itu.gest_emp.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {
    
    @Autowired
    private PersonRepository personRepository;
    
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
    
    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }
    
    public Person savePerson(Person person) {
        return personRepository.save(person);
    }
    
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
    
    public List<Person> searchByFullName(String searchTerm) {
        return personRepository.findByFullNameContainingIgnoreCase(searchTerm);
    }
    
    public List<Person> getAllPersonsSorted() {
        return personRepository.findByOrderByNomAsc();
    }
    public Person createPerson(Person person) {
    return personRepository.save(person);
}

public List<Person> filterPersons(Integer ageMin, Integer ageMax, String experience, 
                                String diploma, String location) {
    List<Person> allPersons = personRepository.findAll();
    
    return allPersons.stream()
            .filter(person -> filterByAge(person, ageMin, ageMax))
            .filter(person -> filterByLocation(person, location))
            .collect(Collectors.toList());
}
public Person findByContact(String contact) {
        return personRepository.findByContact(contact);
    }


    public Person findById(Long id) {
        return personRepository.findById(id).orElse(null);
    }

private boolean filterByAge(Person person, Integer ageMin, Integer ageMax) {
    if (ageMin == null && ageMax == null) {
        return true;
    }
    
    int age = person.getAge();
    
    if (ageMin != null && age < ageMin) {
        return false;
    }
    
    if (ageMax != null && age > ageMax) {
        return false;
    }
    
    return true;
}

private boolean filterByLocation(Person person, String location) {
    if (location == null || location.isEmpty()) {
        return true;
    }

    String adresse = person.getAdresse();
    if (adresse == null) {
        return false;
    }

    return adresse.toLowerCase().contains(location.toLowerCase());
}
}