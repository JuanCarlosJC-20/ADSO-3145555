package com.sena.cafeteria_crud.controller;


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
import com.sena.cafeteria_crud.dto.Request.personDto;
import com.sena.cafeteria_crud.service.Impl.PersonService;


@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    private PersonService personService;


    // Método para registrar una persona
    @PostMapping 
    public String  registerPerson(@Validated @RequestBody personDto personDto)
     {
        personService.savaPerson(personDto);
        return "Persona registrada exitosamente";
    }

    // Método para obtener todas las personas
    @GetMapping
    public ResponseEntity<Object> getPersonAll()
    {
        var obtiene = personService.getAllPersons();
        return new ResponseEntity<>(obtiene, HttpStatus.OK);
    }

    // Método para eliminar una persona por ID
    @DeleteMapping("/{id}")
    public boolean deletePerson(@PathVariable Long id){
        personService.deletepuerca(id);
        return true;
    }

    // Metodo para actilizar una persona por su ID
    @PutMapping("/{id}")
    public boolean updatePerson(@Validated @RequestBody personDto personDto, @PathVariable Long id){
        personService.updatperson(id, personDto);
        return true;
    }

   
    
}
