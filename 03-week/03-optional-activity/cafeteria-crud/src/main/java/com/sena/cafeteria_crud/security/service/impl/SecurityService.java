package com.sena.cafeteria_crud.security.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.security.dto.request.PersonRequestDTO;
import com.sena.cafeteria_crud.security.dto.request.RoleRequestDTO;
import com.sena.cafeteria_crud.security.dto.request.UserRequestDTO;
import com.sena.cafeteria_crud.security.dto.request.UserRoleRequestDTO;
import com.sena.cafeteria_crud.security.dto.response.PersonResponseDTO;
import com.sena.cafeteria_crud.security.dto.response.RoleResponseDTO;
import com.sena.cafeteria_crud.security.dto.response.UserResponseDTO;
import com.sena.cafeteria_crud.security.dto.response.UserRoleResponseDTO;
import com.sena.cafeteria_crud.security.model.Role;
import com.sena.cafeteria_crud.security.model.User;
import com.sena.cafeteria_crud.security.model.UserRole;
import com.sena.cafeteria_crud.security.repository.ISecurityRepository;
import com.sena.cafeteria_crud.security.repository.IUserRepository;
import com.sena.cafeteria_crud.security.repository.IRoleRepository;
import com.sena.cafeteria_crud.security.service.interfaces.ISecurityService;

/**
 * Implementación de lógica de negocio para el módulo Security
 * Aplica principios SOLID: Single Responsibility y Dependency Inversion
 * Maneja autenticación, autorización y gestión de usuarios
 */
@Service
public class SecurityService implements ISecurityService {

    @Autowired
    private ISecurityRepository securityRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    // ===== GESTIÓN DE USER ROLE =====
    
    @Override
    public UserRoleResponseDTO createUserRole(UserRoleRequestDTO userRoleRequest) {
        User user = userRepository.findById(userRoleRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userRoleRequest.getUserId()));
        Role role = roleRepository.findById(userRoleRequest.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + userRoleRequest.getRoleId()));
        
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setState(userRoleRequest.getState() != null ? userRoleRequest.getState() : true);
        
        UserRole savedUserRole = securityRepository.save(userRole);
        return convertToResponseDTO(savedUserRole);
    }

    @Override
    public UserRoleResponseDTO updateUserRole(Long id, UserRoleRequestDTO userRoleRequest) {
        UserRole userRole = securityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserRole no encontrado con id: " + id));
        
        if (userRoleRequest.getUserId() != null) {
            User user = userRepository.findById(userRoleRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userRoleRequest.getUserId()));
            userRole.setUser(user);
        }
        
        if (userRoleRequest.getRoleId() != null) {
            Role role = roleRepository.findById(userRoleRequest.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + userRoleRequest.getRoleId()));
            userRole.setRole(role);
        }
        
        if (userRoleRequest.getState() != null) {
            userRole.setState(userRoleRequest.getState());
        }
        
        UserRole updatedUserRole = securityRepository.save(userRole);
        return convertToResponseDTO(updatedUserRole);
    }

    @Override
    public Optional<UserRoleResponseDTO> getUserRoleById(Long id) {
        return securityRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    @Override
    public List<UserRoleResponseDTO> getAllUserRoles() {
        return securityRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<UserRoleResponseDTO> getUserRolesByUserId(Long userId) {
        return securityRepository.findByUserId(userId).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<UserRoleResponseDTO> getUserRolesByRoleId(Long roleId) {
        return securityRepository.findByRoleId(roleId).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public boolean deleteUserRole(Long id) {
        if (securityRepository.existsById(id)) {
            securityRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ===== GESTIÓN DE USER =====
    
    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequest) {
        // Implementar según tu modelo User y DTO
        // Esta es una implementación base que debes adaptar
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequest) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public Optional<UserResponseDTO> getUserById(Long id) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public Optional<UserResponseDTO> getUserByUsername(String username) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public boolean deleteUser(Long id) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    // ===== GESTIÓN DE PERSON =====
    
    @Override
    public PersonResponseDTO createPerson(PersonRequestDTO personRequest) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public PersonResponseDTO updatePerson(Long id, PersonRequestDTO personRequest) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public Optional<PersonResponseDTO> getPersonById(Long id) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public Optional<PersonResponseDTO> getPersonByEmail(String email) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public List<PersonResponseDTO> getAllPersons() {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public boolean deletePerson(Long id) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    // ===== GESTIÓN DE ROLE =====
    
    @Override
    public RoleResponseDTO createRole(RoleRequestDTO roleRequest) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO roleRequest) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public Optional<RoleResponseDTO> getRoleById(Long id) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public Optional<RoleResponseDTO> getRoleByName(String name) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public boolean deleteRole(Long id) {
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    // ===== AUTENTICACIÓN Y AUTORIZACIÓN =====
    
    @Override
    public boolean authenticateUser(String username, String password) {
        // Implementar lógica de autenticación
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        // Implementar obtención de roles del usuario
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    @Override
    public boolean hasRole(Long userId, String roleName) {
        // Implementar verificación de rol
        throw new UnsupportedOperationException("Método pendiente de implementación");
    }

    // ===== MÉTODOS PRIVADOS =====
    
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