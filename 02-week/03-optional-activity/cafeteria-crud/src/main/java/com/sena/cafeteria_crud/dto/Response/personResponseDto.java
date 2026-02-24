package com.sena.cafeteria_crud.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class personResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

}
