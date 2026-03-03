package com.sena.cafeteria_crud.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.security.dto.request.PersonRequestDTO;
import com.sena.cafeteria_crud.security.dto.response.PersonResponseDTO;
import com.sena.cafeteria_crud.security.model.Person;
import com.sena.cafeteria_crud.security.repository.IPersonRepository;

@Service
public class PersonService {

    @Autowired
    private IPersonRepository personRepository;

    public List<PersonResponseDTO> getAllPersons() {
        return personRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public PersonResponseDTO getPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con id: " + id));
        return convertToResponseDTO(person);
    }

    public void savePerson(PersonRequestDTO personRequestDTO) {
        Person person = new Person();
        person.setFirstName(personRequestDTO.getFirstName());
        person.setLastName(personRequestDTO.getLastName());
        person.setEmail(personRequestDTO.getEmail());
        person.setPhone(personRequestDTO.getPhone());
        person.setState(true);
        personRepository.save(person);
    }

    public void deletePerson(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con id: " + id));
        person.setState(false);
        personRepository.save(person);
    }

    public void updatePerson(Long id, PersonRequestDTO personRequestDTO) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con id: " + id));
        person.setFirstName(personRequestDTO.getFirstName());
        person.setLastName(personRequestDTO.getLastName());
        person.setEmail(personRequestDTO.getEmail());
        person.setPhone(personRequestDTO.getPhone());
        personRepository.save(person);
    }

    private PersonResponseDTO convertToResponseDTO(Person person) {
        return new PersonResponseDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getEmail(),
                person.getPhone(),
                person.getState()
        );
    }
}
