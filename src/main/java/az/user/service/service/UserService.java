package az.user.service.service;

import az.user.service.domain.dao.UserRepository;
import az.user.service.domain.entity.UserEntity;
import az.user.service.exception.InvalidInputException;
import az.user.service.exception.NotFoundException;
import az.user.service.mapper.UserMapper;
import az.user.service.model.dto.UserDto;
import az.user.service.model.enums.UserStatus;
import az.user.service.model.request.UpdateEmailRequest;
import az.user.service.model.request.UpdatePasswordRequest;
import az.user.service.model.request.UpdateUserRequest;
import az.user.service.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public List<UserDto> findAllUsers() {
        return userMapper.toDtos(userRepository.findAll());
    }

    public UserDto findUserByToken() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(UserService::notFound);
        return userMapper.toDto(entity);
    }

    public UserDto findUserByEmail(String email) {
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(UserService::notFound);
        return userMapper.toDto(entity);
    }

    @Transactional
    public UserDto updateUser(UpdateUserRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(UserService::notFound);
        entity.setName(request.getName());
        entity.setSurname(request.getSurname());
        UserEntity savedEntity = userRepository.save(entity);
        return userMapper.toDto(savedEntity);
    }

    @Transactional
    public String updateUserPassword(UpdatePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("email : {}", email);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidInputException("Passwords do not match");
        }
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(UserService::notFound);
        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(entity);
        return "Password successfully changed";
    }

    @Transactional
    public UserDto updateUserEmail(UpdateEmailRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(UserService::notFound);
        entity.setEmail(request.getEmail());
        entity.setStatus(UserStatus.PENDING_VERIFICATION);
        entity.setOtpCode(OtpUtil.generateOtp());
        entity.setOtpExpiresAt(LocalDateTime.now().plusMinutes(OtpUtil.OTP_EXPIRE_MINUTES));
        return userMapper.toDto(userRepository.save(entity));
    }

    private static NotFoundException notFound() {
        return new NotFoundException("User not found");
    }
}
