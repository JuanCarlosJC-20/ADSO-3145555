package com.sena.cafeteria_crud.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class userResponseDto {
    
    private Long id;
    private String username;
    private String password;
    private Boolean state;
}
