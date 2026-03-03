package com.sena.cafeteria_crud.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.security.dto.request.UserRequestDTO;
import com.sena.cafeteria_crud.security.dto.response.PersonResponseDTO;
import com.sena.cafeteria_crud.security.dto.response.UserResponseDTO;
import com.sena.cafeteria_crud.security.model.Person;
import com.sena.cafeteria_crud.security.model.User;
import com.sena.cafeteria_crud.security.repository.IPersonRepository;
import com.sena.cafeteria_crud.security.repository.IUserRepository;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IPersonRepository personRepository;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        return convertToResponseDTO(user);
    }

    public void crearUser(UserRequestDTO userRequestDTO) {
        Person person = personRepository.findById(userRequestDTO.getPersonId())
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con id: " + userRequestDTO.getPersonId()));
        
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(userRequestDTO.getPassword());
        user.setState(userRequestDTO.getState() != null ? userRequestDTO.getState() : true);
        user.setPerson(person);
        userRepository.save(user);
    }

    public void eliminarUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        user.setState(false);
        userRepository.save(user);
    }

    public void actualizarUser(Long id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        if (userRequestDTO.getPersonId() != null) {
            Person person = personRepository.findById(userRequestDTO.getPersonId())
                    .orElseThrow(() -> new RuntimeException("Persona no encontrada con id: " + userRequestDTO.getPersonId()));
            user.setPerson(person);
        }
        
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(userRequestDTO.getPassword());
        if (userRequestDTO.getState() != null) {
            user.setState(userRequestDTO.getState());
        }
        userRepository.save(user);
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        PersonResponseDTO personDTO = null;
        if (user.getPerson() != null) {
            Person person = user.getPerson();
            personDTO = new PersonResponseDTO(
                    person.getId(),
                    person.getFirstName(),
                    person.getLastName(),
                    person.getEmail(),
                    person.getPhone(),
                    person.getState()
            );
        }
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getState(),
                personDTO
        );
    }
}
