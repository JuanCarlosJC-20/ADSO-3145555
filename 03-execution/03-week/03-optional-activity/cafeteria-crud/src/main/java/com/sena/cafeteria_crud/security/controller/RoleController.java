package com.sena.cafeteria_crud.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.cafeteria_crud.security.dto.request.RoleRequestDTO;
import com.sena.cafeteria_crud.security.service.impl.RoleService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/security/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // Método para obtener todos los roles
    @GetMapping
    public ResponseEntity<Object> getAllRoles() {
        var obtiene = roleService.getAllRoles();
        return ResponseEntity.ok(obtiene);
    }

    // Método para guardar un rol
    @PostMapping
    public String guardarRole(@Validated @RequestBody RoleRequestDTO roleRequestDTO){
        roleService.guardarRole(roleRequestDTO);
        return "Rol guardado exitosamente";
    }

    // Método para Eliminar un rol por ID
    @DeleteMapping("/{id}")
    public boolean eliminarRole(@PathVariable Long id){
        roleService.eliminarRole(id);
        return true;
    }

    // Método para actualizar un rol por su ID
    @PutMapping("/{id}")
    public boolean actualizarRole(@PathVariable Long id, @Validated @RequestBody RoleRequestDTO roleRequestDTO){
        roleService.actualizarRole(id, roleRequestDTO);
        return true;
    }
}