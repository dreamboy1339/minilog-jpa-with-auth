package com.asdf.minilog.service;

import com.asdf.minilog.dto.UserRequestDto;
import com.asdf.minilog.dto.UserResponseDto;
import com.asdf.minilog.entity.User;
import com.asdf.minilog.exception.UserNotFoundException;
import com.asdf.minilog.repository.UserRepository;
import com.asdf.minilog.util.EntityDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll().stream()
            .map(EntityDtoMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id).map(EntityDtoMapper::toDto);
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.findByUserName(userRequestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User user =
            User.builder()
                .userName(userRequestDto.getUsername())
                .password(userRequestDto.getPassword())
                .build();

        User savedUser = userRepository.save(user);
        return EntityDtoMapper.toDto(savedUser);
    }

    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto) {
        User user =
            userRepository
                .findById(userId)
                .orElseThrow(
                    () -> {
                        String message = String.format("User with id %d not found", userId);
                        return new UserNotFoundException(message);
                    });
        user.setUserName(userRequestDto.getUsername());
        user.setPassword(userRequestDto.getPassword());

        var updatedUser = userRepository.save(user);
        return EntityDtoMapper.toDto(updatedUser);
    }

    public void deleteUser(Long userId) {
        User user =
            userRepository
                .findById(userId)
                .orElseThrow(
                    () -> {
                        String message = String.format("User with id %d not found", userId);
                        return new UserNotFoundException(message);
                    });
        userRepository.deleteById(user.getId());
    }

    public UserResponseDto getUserByUsername(String username) {
        return null;
    }
}
