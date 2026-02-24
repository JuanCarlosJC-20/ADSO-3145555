package com.sena.cafeteria_crud.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.dto.Response.rolResponseDto;
import com.sena.cafeteria_crud.model.Role;
import com.sena.cafeteria_crud.repository.IRolRepository;

@Service
public class RolService {

    @Autowired
    private IRolRepository rolRepository;

        // Método para obtener todos los roles
       public List<rolResponseDto> getAllRoles(){
        return rolRepository.findAll().stream()
        .map(rol -> new rolResponseDto(
            rol.getId(), 
            rol.getName(),
            rol.getDescription(),
            rol.getState())).toList();
    }

    // Método para eliminar un rol por ID
    public boolean eliminarRol(Long id){
        rolRepository.deleteById(id);
        return true;
    }

    // Método para actualizar un rol por su ID
    public boolean actualizarRol(Long id, rolResponseDto rolResponseDto){
        var rol = rolRepository.findById(id).orElseThrow();
        rol.setName(rolResponseDto.getName());
        rol.setDescription(rolResponseDto.getDescription());
        rol.setState(rolResponseDto.getState());
        rolRepository.save(rol);
        return true;
    }

    // Método para guardar un rol
    public boolean guardarRol(rolResponseDto rolResponseDto){
        var rol = new Role();
        rol.setName(rolResponseDto.getName());
        rol.setDescription(rolResponseDto.getDescription());
        rol.setState(rolResponseDto.getState());
        rolRepository.save(rol);
        return true;
    }
}

