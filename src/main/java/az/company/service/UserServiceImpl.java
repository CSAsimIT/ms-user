package az.company.service;

import az.company.domain.dao.UserRepository;
import az.company.domain.entity.User;
import az.company.event.UserCreatedEvent;
import az.company.event.UserUpdatedEvent;
import az.company.exception.AlreadyExistsException;
import az.company.exception.NotFoundException;
import az.company.exception.UserInactiveException;
import az.company.kafka.UserKafkaProducer;
import az.company.mapper.UserMapper;
import az.company.model.dto.UserResponse;
import az.company.model.dto.request.UpdateUserRequest;
import az.company.model.dto.request.UserRequest;
import az.company.model.enums.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserKafkaProducer producer;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("User with this email already exists");
        }

        User user = userMapper.toEntity(request);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        UserCreatedEvent createdEvent = UserCreatedEvent.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
        producer.publishUserCreatedEvent(createdEvent);
        log.info("User created: {}", user.getEmail());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> notFoundException(id));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserInactiveException("User with id " + id + " is inactive");
        }
        log.info("User found: {}", user.getEmail());
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll()
                .stream()
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .toList();
        log.info("Users found: {}", users.size());
        return userMapper.toResponseList(users);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> notFoundException(id));
        if (oldUser.getStatus() != UserStatus.ACTIVE) {
            throw new UserInactiveException("User with id " + id + " is inactive");
        }
        User newUser = userMapper.toEntity(request, oldUser);
        if (request.getEmail() != null && !request.getEmail().equals(oldUser.getEmail())) {
            newUser.setStatus(UserStatus.PENDING_VERIFICATION);
        }
        User savedUser = userRepository.save(newUser);
        log.info("User updated: {}", savedUser.getEmail());
        UserUpdatedEvent updatedEvent = UserUpdatedEvent.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .surname(savedUser.getSurname())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .build();
        producer.publishUserUpdatedEvent(updatedEvent);
        log.info("User updated event created: {}", savedUser.getEmail());
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        log.info("User deleted: {}", user.getEmail());
    }

    private NotFoundException notFoundException(UUID id) {
        return new NotFoundException("User with id " + id + " not found");
    }
}
