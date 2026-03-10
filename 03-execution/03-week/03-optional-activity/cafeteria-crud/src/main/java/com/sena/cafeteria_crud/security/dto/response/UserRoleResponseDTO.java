package com.sena.cafeteria_crud.security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponseDTO {

    private Long id;
    private Boolean state;
    private Long userId;
    private String username;
    private Long roleId;
    private String roleName;

}