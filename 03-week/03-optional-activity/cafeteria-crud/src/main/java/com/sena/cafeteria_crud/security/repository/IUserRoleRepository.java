package com.sena.cafeteria_crud.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.cafeteria_crud.security.model.UserRole;

@Repository
public interface IUserRoleRepository extends JpaRepository<UserRole, Long> {
    
    List<UserRole> findByUserId(Long userId);
    
    List<UserRole> findByRoleId(Long roleId);
    
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);
    
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
    
    void deleteByUserId(Long userId);
    
    List<UserRole> findByStateTrue();
}
