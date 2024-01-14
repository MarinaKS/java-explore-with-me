package ru.practicum.server.user.mapper;

import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
