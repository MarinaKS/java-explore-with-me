package ru.practicum.server.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.category.dto.CategoryDto;
import ru.practicum.server.category.mapper.CategoryMapper;
import ru.practicum.server.category.model.Category;
import ru.practicum.server.category.repository.CategoryRepository;
import ru.practicum.server.event.repository.EventRepository;
import ru.practicum.server.exception.ObjectNotFoundException;
import ru.practicum.server.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ValidationException("Такое название категории уже существует");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Transactional
    @Override
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ValidationException("Категория не пуста");
        }
        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Нет категории с таким id"));
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ValidationException("Имя категории уже занято");
        }
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Категория не найдена")));
    }
}
