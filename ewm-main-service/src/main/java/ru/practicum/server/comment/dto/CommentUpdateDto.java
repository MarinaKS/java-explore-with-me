package ru.practicum.server.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentUpdateDto {
    @NotNull
    @NotBlank
    private String text;
}
