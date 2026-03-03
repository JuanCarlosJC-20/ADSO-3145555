package com.sena.cafeteria_crud.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.cafeteria_crud.security.dto.request.PersonRequestDTO;
import com.sena.cafeteria_crud.security.service.impl.PersonService;

@RestController
@RequestMapping("/api/security/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    // Método para registrar una persona
    @PostMapping 
    public String registerPerson(@Validated @RequestBody PersonRequestDTO personRequestDTO) {
        personService.savePerson(personRequestDTO);
        return "Persona registrada exitosamente";
    }

    // Método para obtener todas las personas
    @GetMapping
    public ResponseEntity<Object> getPersonAll() {
        var obtiene = personService.getAllPersons();
        return new ResponseEntity<>(obtiene, HttpStatus.OK);
    }

    // Método para eliminar una persona por ID
    @DeleteMapping("/{id}")
    public boolean deletePerson(@PathVariable Long id){
        personService.deletePerson(id);
        return true;
    }

    // Metodo para actualizar una persona por su ID
    @PutMapping("/{id}")
    public boolean updatePerson(@Validated @RequestBody PersonRequestDTO personRequestDTO, @PathVariable Long id){
        personService.updatePerson(id, personRequestDTO);
        return true;
    }
}