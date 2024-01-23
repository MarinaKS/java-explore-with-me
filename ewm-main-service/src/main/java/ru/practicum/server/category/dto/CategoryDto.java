package ru.practicum.server.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank
    @NotNull
    @Size(max = 50)
    private String name;
}
