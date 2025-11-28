package com.entrelinhas.backend.service;

import com.entrelinhas.backend.domain.UserAccount;
import com.entrelinhas.backend.domain.UserType;
import com.entrelinhas.backend.dto.auth.AuthResponse;
import com.entrelinhas.backend.dto.auth.LoginRequest;
import com.entrelinhas.backend.dto.auth.RegisterRequest;
import com.entrelinhas.backend.dto.user.UserSummary;
import com.entrelinhas.backend.repository.ServiceRepository;
import com.entrelinhas.backend.repository.UserAccountRepository;
import com.entrelinhas.backend.security.JwtService;
import com.entrelinhas.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        rejectIfDuplicate(request);

        validateUserTypePayload(request);

        UserAccount user = UserAccount.builder()
                .userType(request.getUserType())
                .fullName(request.getFullName())
                .cpf(request.getCpf())
                .birthDate(request.getBirthDate())
                .cnpj(request.getCnpj())
                .stateRegistration(request.getStateRegistration())
                .profileImageUrl(request.getProfileImageUrl())
                .description(request.getDescription())
                .email(request.getEmail())
                .phone(request.getPhone())
                .state(request.getState())
                .city(request.getCity())
                .mainService(request.getMainServiceId() != null ? serviceRepository.findById(request.getMainServiceId()).orElse(null) : null)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .availability(Boolean.TRUE)
                .emailVerified(false)
                .build();

        userAccountRepository.save(user);
        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal);
        return AuthResponse.builder()
                .accessToken(token)
                .user(UserSummary.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .userType(user.getUserType())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    private void rejectIfDuplicate(RegisterRequest request) {
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
        }
        if (StringUtils.isNotBlank(request.getPhone()) && userAccountRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado");
        }
        if (StringUtils.isNotBlank(request.getCpf()) && userAccountRepository.existsByCpf(request.getCpf())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado");
        }
        if (StringUtils.isNotBlank(request.getCnpj()) && userAccountRepository.existsByCnpj(request.getCnpj())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ já cadastrado");
        }
        if (StringUtils.isNotBlank(request.getStateRegistration()) && userAccountRepository.existsByStateRegistration(request.getStateRegistration())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inscrição estadual já cadastrada");
        }
    }

    private void validateUserTypePayload(RegisterRequest request) {
        UserType type = request.getUserType();
        if (type == UserType.FACCIONISTA) {
            if (StringUtils.isAnyBlank(request.getCpf()) || request.getBirthDate() == null) {
                throw new IllegalArgumentException("Faltam dados obrigatórios para faccionista");
            }
        } else if (type == UserType.COMPANY) {
            if (StringUtils.isAnyBlank(request.getCnpj(), request.getStateRegistration())) {
                throw new IllegalArgumentException("Faltam dados obrigatórios para empresa");
            }
        } else if (type == UserType.FACTION) {
            if (StringUtils.isBlank(request.getCnpj())) {
                throw new IllegalArgumentException("Faltam dados obrigatórios para facção");
            }
        }
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var auth = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        var authentication = authenticationManager.authenticate(auth);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UserAccount user = userAccountRepository.findByEmail(principal.getUsername()).orElseThrow();
        user.setLastAccessAt(LocalDateTime.now());
        String token = jwtService.generateToken(principal);
        return AuthResponse.builder()
                .accessToken(token)
                .user(UserSummary.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .userType(user.getUserType())
                        .email(user.getEmail())
                        .build())
                .build();
    }
}
