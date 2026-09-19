package com.welfare.user.application;

import com.welfare.common.exception.BusinessException;
import com.welfare.common.exception.ErrorCode;
import com.welfare.security.JwtTokenProvider;
import com.welfare.user.api.dto.EmailCheckResponse;
import com.welfare.user.api.dto.LoginRequest;
import com.welfare.user.api.dto.LoginResponse;
import com.welfare.user.api.dto.SignupRequest;
import com.welfare.user.api.dto.SignupResponse;
import com.welfare.user.domain.User;
import com.welfare.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        User user = User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName()
        );
        User saved = userRepository.save(user);
        return SignupResponse.builder()
                .userId(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public EmailCheckResponse checkEmail(String email) {
        return EmailCheckResponse.builder()
                .email(email)
                .duplicated(userRepository.existsByEmail(email))
                .build();
    }
}
