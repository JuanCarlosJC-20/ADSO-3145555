package com.sena.cafeteria_crud.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.cafeteria_crud.security.model.Role;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    List<Role> findByStateTrue();
    
    boolean existsByName(String name);
}
