package ru.practicum.server.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.info("Емейл уже используется: {}", userDto.getEmail());
            throw new ValidationException("Пользователь с таким емейл уже есть");
        }
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Добавлен пользователь с id {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (ids != null && ids.size() > 0) {
            return userRepository.findAllByIdIn(ids, page).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll(page).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("Пользователь с id {} был удален", userId);
        userRepository.deleteById(userId);
    }
}
