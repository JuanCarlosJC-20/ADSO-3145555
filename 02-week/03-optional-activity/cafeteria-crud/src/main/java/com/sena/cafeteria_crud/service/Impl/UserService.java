package com.sena.cafeteria_crud.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.dto.Request.userDto;
import com.sena.cafeteria_crud.dto.Response.userResponseDto;
import com.sena.cafeteria_crud.model.Person;
import com.sena.cafeteria_crud.model.User;
import com.sena.cafeteria_crud.repository.IPersonRepository;
import com.sena.cafeteria_crud.repository.IUserRepository;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IPersonRepository personRepository;


    //metodo para obtener todos los usuarios
    public List<userResponseDto> getAllUsers(){
        return userRepository.findAll().stream()
        .map(user -> new userResponseDto(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getState()
        )).toList();
    }

    // Método para crear un nuevo usuario

    public boolean crearUser(userDto userDto){
        // Buscar la persona en la base de datos
        Person person = personRepository.findById(userDto.getPersonId())
            .orElseThrow(() -> new RuntimeException("No se encontró la persona con id: " + userDto.getPersonId()));

        var user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setState(userDto.getState());
        user.setPerson(person); // Asignar el objeto Person real
        userRepository.save(user);
        return true;
    }

    // Método para eliminar un usuario por ID
    public boolean eliminarUser(long id){
        userRepository.deleteById(id);
        return true;
    }

    // Método para actualizar un usuario por ID
    public boolean actualizarUser(long id, userDto userDto){
        var user = userRepository.findById(id).orElseThrow();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setState(userDto.getState());
        userRepository.save(user);
        return true;
    }
}