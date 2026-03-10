package com.sena.cafeteria_crud.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.cafeteria_crud.security.model.Person;

@Repository
public interface IPersonRepository extends JpaRepository<Person, Long> {
    
    Optional<Person> findByEmail(String email);
    
    List<Person> findByStateTrue();
    
    boolean existsByEmail(String email);
}
