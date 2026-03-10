package com.sena.cafeteria_crud.security.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.sena.cafeteria_crud.security.dto.request.PersonRequestDTO;
import com.sena.cafeteria_crud.security.dto.request.RoleRequestDTO;
import com.sena.cafeteria_crud.security.dto.request.UserRequestDTO;
import com.sena.cafeteria_crud.security.dto.request.UserRoleRequestDTO;
import com.sena.cafeteria_crud.security.dto.response.PersonResponseDTO;
import com.sena.cafeteria_crud.security.dto.response.RoleResponseDTO;
import com.sena.cafeteria_crud.security.dto.response.UserResponseDTO;
import com.sena.cafeteria_crud.security.dto.response.UserRoleResponseDTO;

/**
 * Contrato de lógica de negocio para el módulo Security
 * Aplica principios SOLID: Single Responsibility y Dependency Inversion
 * Define todos los casos de uso del módulo de seguridad
 */
public interface ISecurityService {
    
    // Gestión de UserRole
    UserRoleResponseDTO createUserRole(UserRoleRequestDTO userRoleRequest);
    UserRoleResponseDTO updateUserRole(Long id, UserRoleRequestDTO userRoleRequest);
    Optional<UserRoleResponseDTO> getUserRoleById(Long id);
    List<UserRoleResponseDTO> getAllUserRoles();
    List<UserRoleResponseDTO> getUserRolesByUserId(Long userId);
    List<UserRoleResponseDTO> getUserRolesByRoleId(Long roleId);
    boolean deleteUserRole(Long id);
    
    // Gestión de User
    UserResponseDTO createUser(UserRequestDTO userRequest);
    UserResponseDTO updateUser(Long id, UserRequestDTO userRequest);
    Optional<UserResponseDTO> getUserById(Long id);
    Optional<UserResponseDTO> getUserByUsername(String username);
    List<UserResponseDTO> getAllUsers();
    boolean deleteUser(Long id);
    
    // Gestión de Person
    PersonResponseDTO createPerson(PersonRequestDTO personRequest);
    PersonResponseDTO updatePerson(Long id, PersonRequestDTO personRequest);
    Optional<PersonResponseDTO> getPersonById(Long id);
    Optional<PersonResponseDTO> getPersonByEmail(String email);
    List<PersonResponseDTO> getAllPersons();
    boolean deletePerson(Long id);
    
    // Gestión de Role
    RoleResponseDTO createRole(RoleRequestDTO roleRequest);
    RoleResponseDTO updateRole(Long id, RoleRequestDTO roleRequest);
    Optional<RoleResponseDTO> getRoleById(Long id);
    Optional<RoleResponseDTO> getRoleByName(String name);
    List<RoleResponseDTO> getAllRoles();
    boolean deleteRole(Long id);
    
    // Operaciones de autenticación y autorización
    boolean authenticateUser(String username, String password);
    List<String> getUserRoles(Long userId);
    boolean hasRole(Long userId, String roleName);
}