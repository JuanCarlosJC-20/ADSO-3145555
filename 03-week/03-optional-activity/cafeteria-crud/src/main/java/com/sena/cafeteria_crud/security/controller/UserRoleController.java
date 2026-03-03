package com.sena.cafeteria_crud.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.cafeteria_crud.security.dto.request.UserRoleRequestDTO;
import com.sena.cafeteria_crud.security.service.impl.UserRoleService;

@RestController
@RequestMapping("/api/security/userRoles")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    // Método para obtener todos los user roles
    @GetMapping
    public ResponseEntity<Object> getAllUserRoles() {
        var obtiene = userRoleService.getAllUserRoles();
        return new ResponseEntity<>(obtiene, HttpStatus.OK);
    }

    // Método para guardar un user role
    @PostMapping
    public String guardarUserRole(@Validated @RequestBody UserRoleRequestDTO userRoleRequestDTO) {
        userRoleService.guardarUserRole(userRoleRequestDTO);
        return "UserRole guardado exitosamente";
    }

    // Método para eliminar un user role por ID
    @DeleteMapping("/{id}")
    public boolean eliminarUserRole(@PathVariable Long id) {
        userRoleService.eliminarUserRole(id);
        return true;
    }

    // Método para actualizar un user role por su ID
    @PutMapping("/{id}")
    public boolean actualizarUserRole(@PathVariable Long id, @Validated @RequestBody UserRoleRequestDTO userRoleRequestDTO) {
        userRoleService.actualizarUserRole(id, userRoleRequestDTO);
        return true;
    }
}