package com.sena.cafeteria_crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.cafeteria_crud.model.UserRole;

@Repository
public interface IUserRoleRepository  extends JpaRepository<UserRole, Long>{

}
