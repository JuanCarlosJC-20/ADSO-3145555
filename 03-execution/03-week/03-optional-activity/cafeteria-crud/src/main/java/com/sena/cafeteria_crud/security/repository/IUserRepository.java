package com.sena.cafeteria_crud.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.cafeteria_crud.security.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    List<User> findByStateTrue();
    
    boolean existsByUsername(String username);
}
