package com.sena.cafeteria_crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.cafeteria_crud.dto.Response.rolResponseDto;
import com.sena.cafeteria_crud.service.Impl.RolService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/rol")
public class rolController {

    @Autowired
    private RolService rolService;

    // Método para obtener todos los roles
    @GetMapping
    public ResponseEntity<Object> getAllRoles() {
        var obtiene = rolService.getAllRoles();
        return ResponseEntity.ok(obtiene);
    }

    // Método para guardar un rol
    @PostMapping
    public String guardarRol(@Validated @RequestBody rolResponseDto rolResponseDto){
        rolService.guardarRol(rolResponseDto);
        return "Rol guardado exitosamente";
    }

    // Método para Eliminar un rol por ID
    @DeleteMapping("/{id}")
    public  boolean eliminarRol(@PathVariable Long id){
        rolService.eliminarRol(id);
        return true;
    }

    // Método para actualizar un rol por su ID
    @PutMapping("/{id}")
    public boolean actualizarRol(@PathVariable Long id, @Validated @RequestBody rolResponseDto rolResponseDto){
        rolService.actualizarRol(id, rolResponseDto);
        return true;
    }
 
    
}
