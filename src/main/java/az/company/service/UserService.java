package az.company.service;

import az.company.model.dto.UserResponse;
import az.company.model.dto.request.UpdateUserRequest;
import az.company.model.dto.request.UserRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUserById(UUID id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    void deleteUser(UUID id);
}
