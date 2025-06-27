package az.user.service.service;

import az.user.service.domain.dao.UserRepository;
import az.user.service.domain.entity.UserEntity;
import az.user.service.exception.AlreadyExistsException;
import az.user.service.exception.InvalidInputException;
import az.user.service.exception.NotFoundException;
import az.user.service.mapper.UserMapper;
import az.user.service.model.dto.AuthDto;
import az.user.service.model.dto.UserDto;
import az.user.service.model.enums.UserRole;
import az.user.service.model.enums.UserStatus;
import az.user.service.model.request.LoginUserRequest;
import az.user.service.model.request.RegisterUserRequest;
import az.user.service.model.request.VerifyOtpRequest;
import az.user.service.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @Transactional
    public AuthDto registerUser(RegisterUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("You can not use this email. Because email already exists");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidInputException("The passwords do not match");
        }

        UserEntity userEntity = userMapper.toEntity(request);
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setStatus(UserStatus.PENDING_VERIFICATION);
        userEntity.setRoles(Set.of(UserRole.ROLE_USER));
        userEntity.setOtpCode(OtpUtil.generateOtp());
        userEntity.setOtpExpiresAt(LocalDateTime.now().plusMinutes(OtpUtil.OTP_EXPIRE_MINUTES));

        UserEntity savedUser = userRepository.save(userEntity);
        UserDto userDto = userMapper.toDto(savedUser);

        log.info("User saved with id {}", savedUser.getId());
        return AuthDto.builder()
                .user(userDto)
                .token(null)
                .build();
    }

    @Transactional
    public String verifyOtp(VerifyOtpRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getOtpCode().equals(request.getOtpCode()) && user.getOtpExpiresAt().isAfter(LocalDateTime.now())) {
            user.setStatus(UserStatus.ACTIVE);
            user.setOtpCode(null);
            user.setOtpExpiresAt(null);
            userRepository.save(user);
            log.info("User {} verified successfully", user.getEmail());
            return jwtService.generateToken(user);
        } else {
            throw new InvalidInputException("Invalid OTP or OTP expired");
        }
    }

    public String login(LoginUserRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserEntity user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            log.info("User {} logged in successfully", user.getEmail());
            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new InvalidInputException("User is not active");
            }
            return jwtService.generateToken(user);
        } else {
            throw new InvalidInputException("Invalid credentials");
        }
    }
}
