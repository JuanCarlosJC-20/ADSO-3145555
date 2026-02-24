package com.sena.cafeteria_crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sena.cafeteria_crud.model.Role;

public interface IRolRepository  extends JpaRepository<Role, Long>{

}
