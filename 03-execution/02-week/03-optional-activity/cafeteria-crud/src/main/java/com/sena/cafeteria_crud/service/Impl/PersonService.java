package com.sena.cafeteria_crud.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.dto.Request.personDto;
import com.sena.cafeteria_crud.dto.Response.personResponseDto;
import com.sena.cafeteria_crud.model.Person;
import com.sena.cafeteria_crud.repository.IPersonRepository;

@Service
public class PersonService {

    @Autowired
    private IPersonRepository IpersonRepository;

    // Método para obtener todas las personas

    public List<personResponseDto> getAllPersons() {
        
        return IpersonRepository.findAll().stream()
                .map(person -> new personResponseDto(
                        person.getId(),
                        person.getFirstName(),
                        person.getLastName(),
                        person.getEmail(),
                        person.getPhone()))
                .toList();
    }


    // Método para guardar una persona

    public boolean savaPerson(personDto personDto) {
   
        var person = new Person();
        person.setFirstName(personDto.getFirstName());
        person.setLastName(personDto.getLastName());
        person.setEmail(personDto.getEmail());
        person.setPhone(personDto.getPhone());
        IpersonRepository.save(person);
        return true;
    }

    // Método para eliminar una persona por ID

    public boolean deletepuerca(Long id){
        IpersonRepository.deleteById(id);
        return true;
    } 



   //Metodo para actilizar una persona por su ID
    public boolean updatperson(Long id, personDto personDto){
        var person = IpersonRepository.findById(id).orElseThrow();
        person.setFirstName(personDto.getFirstName());
        person.setLastName(personDto.getLastName());
        person.setEmail(personDto.getEmail());
        person.setPhone(personDto.getPhone());
        IpersonRepository.save(person);
        return true;
    }



       
}
