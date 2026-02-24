package com.sena.cafeteria_crud.dto.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class rolResponseDto {

    private Long id;
    private String name;
    private String description;
    private Boolean state;

}
