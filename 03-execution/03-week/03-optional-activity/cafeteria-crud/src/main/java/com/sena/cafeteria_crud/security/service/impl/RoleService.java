package com.sena.cafeteria_crud.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.security.dto.request.RoleRequestDTO;
import com.sena.cafeteria_crud.security.dto.response.RoleResponseDTO;
import com.sena.cafeteria_crud.security.model.Role;
import com.sena.cafeteria_crud.security.repository.IRoleRepository;

@Service
public class RoleService {

    @Autowired
    private IRoleRepository roleRepository;

    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
        return convertToResponseDTO(role);
    }

    public void guardarRole(RoleRequestDTO roleRequestDTO) {
        Role role = new Role();
        role.setName(roleRequestDTO.getName());
        role.setDescription(roleRequestDTO.getDescription());
        role.setState(true);
        roleRepository.save(role);
    }

    public void eliminarRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
        role.setState(false);
        roleRepository.save(role);
    }

    public void actualizarRole(Long id, RoleRequestDTO roleRequestDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
        role.setName(roleRequestDTO.getName());
        role.setDescription(roleRequestDTO.getDescription());
        roleRepository.save(role);
    }

    private RoleResponseDTO convertToResponseDTO(Role role) {
        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getState()
        );
    }
}
