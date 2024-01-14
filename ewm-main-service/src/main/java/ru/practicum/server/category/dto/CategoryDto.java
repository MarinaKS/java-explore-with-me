package ru.practicum.server.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank
    @NotNull
    private String name;
}
