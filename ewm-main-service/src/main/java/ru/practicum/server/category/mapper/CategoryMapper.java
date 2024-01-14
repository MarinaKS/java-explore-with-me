package ru.practicum.server.category.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.server.category.dto.CategoryDto;
import ru.practicum.server.category.model.Category;

@Service
public class CategoryMapper {
    public static Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
