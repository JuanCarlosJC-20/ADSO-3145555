package com.sena.cafeteria_crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.cafeteria_crud.dto.Request.userDto;
import com.sena.cafeteria_crud.service.Impl.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/api/Users")
public class UserController {

    @Autowired
    private UserService userService;

    // Método para obtener todos los usuarios
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        var obtiene = userService.getAllUsers();
        return new ResponseEntity<>(obtiene, HttpStatus.OK);
    }

    // Método para crear un nuevo usuario
    @PostMapping
    public String crearUser(@Validated @RequestBody userDto userDto){
        userService.crearUser(userDto);
        return "Usuario creado exitosamente";
    }

    // Método para eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public boolean eliminarUser(@PathVariable long id){
        userService.eliminarUser(id);
        return true;
    }

    // Método para actualizar un usuario por ID
    @PutMapping("/{id}")
    public boolean actualizarUser(@PathVariable long id, @Validated @RequestBody userDto userDto){
        userService.actualizarUser(id, userDto);
        return true;
    }

    
}
