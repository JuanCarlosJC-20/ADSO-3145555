package com.sena.cafeteria_crud.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import com.sena.cafeteria_crud.security.model.UserRole;
import com.sena.cafeteria_crud.security.model.Person;
import com.sena.cafeteria_crud.security.model.Role;
import com.sena.cafeteria_crud.security.model.User;

/**
 * Contrato de acceso a datos para el módulo Security
 * Aplica principios SOLID: Interface Segregation y Dependency Inversion
 * Maneja todas las operaciones de persistencia para Security
 */
@Repository
public interface ISecurityRepository extends JpaRepository<UserRole, Long> {
    
    // Operaciones para UserRole
    List<UserRole> findByUserId(Long userId);
    List<UserRole> findByRoleId(Long roleId);
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
    void deleteByUserId(Long userId);
    
    // Operaciones para User
    Optional<User> findUserById(Long id);
    List<User> findAllUsers();
    Optional<User> findUserByUsername(String username);
    boolean existsUserByUsername(String username);
    
    // Operaciones para Person
    Optional<Person> findPersonById(Long id);
    List<Person> findAllPersons();
    Optional<Person> findPersonByEmail(String email);
    
    // Operaciones para Role
    Optional<Role> findRoleById(Long id);
    List<Role> findAllRoles();
    Optional<Role> findRoleByName(String name);
    boolean existsRoleByName(String name);
}