package com.sena.cafeteria_crud.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.dto.Request.userRolDto;
import com.sena.cafeteria_crud.dto.Response.userRolResponseDto;
import com.sena.cafeteria_crud.model.Role;
import com.sena.cafeteria_crud.model.User;
import com.sena.cafeteria_crud.model.UserRole;
import com.sena.cafeteria_crud.repository.IRolRepository;
import com.sena.cafeteria_crud.repository.IUserRepository;
import com.sena.cafeteria_crud.repository.IUserRoleRepository;

@Service
public class UserRoleService {

    @Autowired
    private IUserRoleRepository userRoleRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRolRepository rolRepository;

    // Método para obtener todos los user roles
    public List<userRolResponseDto> getAllUserRoles() {
        return userRoleRepository.findAll().stream()
                .map(userRole -> new userRolResponseDto(
                        userRole.getId(),
                        userRole.getState(),
                        userRole.getUser().getId(),
                        userRole.getUser().getUsername(),
                        userRole.getRole().getId(),
                        userRole.getRole().getName()))
                .toList();
    }

    // Método para guardar un user role
    public boolean guardarUserRole(userRolDto userRolDto) {
        User user = userRepository.findById(userRolDto.getUserId())
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario con id: " + userRolDto.getUserId()));

        Role role = rolRepository.findById(userRolDto.getRoleId())
                .orElseThrow(() -> new RuntimeException("No se encontró el rol con id: " + userRolDto.getRoleId()));

        var userRole = new UserRole();
        userRole.setState(userRolDto.getState() != null ? userRolDto.getState() : true);
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
        return true;
    }

    // Método para eliminar un user role por ID
    public boolean eliminarUserRole(Long id) {
        userRoleRepository.deleteById(id);
        return true;
    }

    // Método para actualizar un user role por su ID
    public boolean actualizarUserRole(Long id, userRolDto userRolDto) {
        var userRole = userRoleRepository.findById(id).orElseThrow();

        User user = userRepository.findById(userRolDto.getUserId())
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario con id: " + userRolDto.getUserId()));

        Role role = rolRepository.findById(userRolDto.getRoleId())
                .orElseThrow(() -> new RuntimeException("No se encontró el rol con id: " + userRolDto.getRoleId()));

        userRole.setState(userRolDto.getState() != null ? userRolDto.getState() : userRole.getState());
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
        return true;
    }

}
