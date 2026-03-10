package com.sena.cafeteria_crud.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.security.dto.request.UserRoleRequestDTO;
import com.sena.cafeteria_crud.security.dto.response.UserRoleResponseDTO;
import com.sena.cafeteria_crud.security.model.Role;
import com.sena.cafeteria_crud.security.model.User;
import com.sena.cafeteria_crud.security.model.UserRole;
import com.sena.cafeteria_crud.security.repository.IRoleRepository;
import com.sena.cafeteria_crud.security.repository.IUserRepository;
import com.sena.cafeteria_crud.security.repository.IUserRoleRepository;

@Service
public class UserRoleService {

    @Autowired
    private IUserRoleRepository userRoleRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    public List<UserRoleResponseDTO> getAllUserRoles() {
        return userRoleRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public UserRoleResponseDTO getUserRoleById(Long id) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserRole no encontrado con id: " + id));
        return convertToResponseDTO(userRole);
    }

    public void guardarUserRole(UserRoleRequestDTO userRoleRequestDTO) {
        User user = userRepository.findById(userRoleRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userRoleRequestDTO.getUserId()));
        
        Role role = roleRepository.findById(userRoleRequestDTO.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + userRoleRequestDTO.getRoleId()));
        
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setState(userRoleRequestDTO.getState() != null ? userRoleRequestDTO.getState() : true);
        userRoleRepository.save(userRole);
    }

    public void eliminarUserRole(Long id) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserRole no encontrado con id: " + id));
        userRole.setState(false);
        userRoleRepository.save(userRole);
    }

    public void actualizarUserRole(Long id, UserRoleRequestDTO userRoleRequestDTO) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserRole no encontrado con id: " + id));
        
        if (userRoleRequestDTO.getUserId() != null) {
            User user = userRepository.findById(userRoleRequestDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userRoleRequestDTO.getUserId()));
            userRole.setUser(user);
        }
        
        if (userRoleRequestDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userRoleRequestDTO.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + userRoleRequestDTO.getRoleId()));
            userRole.setRole(role);
        }
        
        if (userRoleRequestDTO.getState() != null) {
            userRole.setState(userRoleRequestDTO.getState());
        }
        
        userRoleRepository.save(userRole);
    }

    private UserRoleResponseDTO convertToResponseDTO(UserRole userRole) {
        return new UserRoleResponseDTO(
                userRole.getId(),
                userRole.getState(),
                userRole.getUser().getId(),
                userRole.getUser().getUsername(),
                userRole.getRole().getId(),
                userRole.getRole().getName()
        );
    }
}
