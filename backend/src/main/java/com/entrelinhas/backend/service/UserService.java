package com.entrelinhas.backend.service;

import com.entrelinhas.backend.domain.UserAccount;
import com.entrelinhas.backend.domain.ServiceRequestStatus;
import com.entrelinhas.backend.domain.UserType;
import com.entrelinhas.backend.dto.user.AvailabilityUpdateRequest;
import com.entrelinhas.backend.dto.user.UserProfileResponse;
import com.entrelinhas.backend.dto.user.UserSearchResponse;
import com.entrelinhas.backend.dto.user.UserUpdateRequest;
import com.entrelinhas.backend.repository.ReviewRepository;
import com.entrelinhas.backend.repository.ServiceRequestRepository;
import com.entrelinhas.backend.repository.ServiceRepository;
import com.entrelinhas.backend.repository.UserAccountRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final ReviewRepository reviewRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long id) {
        UserAccount user = loadActiveUser(id);
        Double avg = reviewRepository.calculateAverage(user);
        return UserProfileResponse.builder()
                .id(user.getId())
                .userType(user.getUserType())
                .fullName(user.getFullName())
                .cpf(user.getCpf())
                .birthDate(user.getBirthDate())
                .cnpj(user.getCnpj())
                .stateRegistration(user.getStateRegistration())
                .profileImageUrl(user.getProfileImageUrl())
                .description(user.getDescription())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .phone(user.getPhone())
                .state(user.getState())
                .city(user.getCity())
                .mainService(user.getMainService() != null ? user.getMainService().getName() : null)
                .mainServiceId(user.getMainService() != null ? user.getMainService().getId() : null)
                .availability(user.getAvailability())
                .averageRating(avg != null ? avg : 0.0)
                .serviceCount(calculateServiceCount(user))
                .reviewCount(reviewRepository.countByReceiver(user))
                .lastAccessAt(user.getLastAccessAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponse> search(String name, UserType userType, String state, String city, String mainService, Boolean availability) {
        Specification<UserAccount> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%"));
            }
            if (userType != null) {
                predicates.add(cb.equal(root.get("userType"), userType));
            }
            if (state != null && !state.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("state")), state.toLowerCase()));
            }
            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
            }
            if (availability != null) {
                predicates.add(cb.equal(root.get("availability"), availability));
            }
            predicates.add(cb.isNull(root.get("deletedAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<UserAccount> users = userAccountRepository.findAll(specification);
        return users.stream()
                .filter(user -> filterByService(user, mainService))
                .map(user -> {
                    Double avg = reviewRepository.calculateAverage(user);
                    return UserSearchResponse.builder()
                            .id(user.getId())
                            .fullName(user.getFullName())
                            .userType(user.getUserType())
                            .description(user.getDescription())
                            .state(user.getState())
                            .city(user.getCity())
                            .mainService(user.getMainService() != null ? user.getMainService().getName() : null)
                            .availability(user.getAvailability())
                            .averageRating(avg != null ? avg : 0.0)
                            .serviceCount(calculateServiceCount(user))
                            .reviewCount(reviewRepository.countByReceiver(user))
                            .build();
                })
                .toList();
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserUpdateRequest request) {
        UserAccount user = loadActiveUser(userId);
        rejectDuplicatesOnUpdate(request, user);
        applyUserTypeRules(user, request);

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setDescription(request.getDescription());
        user.setAvailability(request.getAvailability() != null ? request.getAvailability() : user.getAvailability());
        if (request.getMainServiceId() != null) {
            user.setMainService(serviceRepository.findById(request.getMainServiceId()).orElse(null));
        }
        user.setCpf(request.getCpf());
        user.setBirthDate(request.getBirthDate());
        user.setCnpj(request.getCnpj());
        user.setStateRegistration(request.getStateRegistration());
        userAccountRepository.save(user);
        return getProfile(user.getId());
    }

    @Transactional
    public void deleteAccount(Long userId) {
        UserAccount user = loadActiveUser(userId);
        user.setDeletedAt(java.time.LocalDateTime.now());
    }

    private boolean filterByService(UserAccount user, String mainService) {
        if (mainService == null || mainService.isBlank()) {
            return true;
        }
        return user.getMainService() != null && user.getMainService().getName().equalsIgnoreCase(mainService);
    }

    @Transactional
    public void updateAvailability(Long userId, AvailabilityUpdateRequest request) {
        UserAccount user = loadActiveUser(userId);
        user.setAvailability(request.getAvailability());
    }

    private void rejectDuplicatesOnUpdate(UserUpdateRequest request, UserAccount current) {
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equalsIgnoreCase(current.getEmail())
                && userAccountRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
        }
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equalsIgnoreCase(defaultString(current.getPhone()))
                && userAccountRepository.existsByPhone(request.getPhone())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone já cadastrado");
        }
        if (StringUtils.hasText(request.getCpf()) && !request.getCpf().equalsIgnoreCase(defaultString(current.getCpf()))
                && userAccountRepository.existsByCpf(request.getCpf())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado");
        }
        if (StringUtils.hasText(request.getCnpj()) && !request.getCnpj().equalsIgnoreCase(defaultString(current.getCnpj()))
                && userAccountRepository.existsByCnpj(request.getCnpj())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ já cadastrado");
        }
        if (StringUtils.hasText(request.getStateRegistration())
                && !request.getStateRegistration().equalsIgnoreCase(defaultString(current.getStateRegistration()))
                && userAccountRepository.existsByStateRegistration(request.getStateRegistration())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inscrição estadual já cadastrada");
        }
    }

    private void applyUserTypeRules(UserAccount user, UserUpdateRequest request) {
        UserType type = user.getUserType();
        if (type == UserType.FACCIONISTA) {
            if (!StringUtils.hasText(request.getCpf()) || request.getBirthDate() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF e data de nascimento são obrigatórios");
            }
        }
        if (type == UserType.COMPANY) {
            if (!StringUtils.hasText(request.getCnpj()) || !StringUtils.hasText(request.getStateRegistration())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ e inscrição estadual são obrigatórios");
            }
        }
        if (type == UserType.FACTION) {
            if (!StringUtils.hasText(request.getCnpj())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ é obrigatório para facção");
            }
        }
    }

    private UserAccount loadActiveUser(Long userId) {
        UserAccount user = userAccountRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if (user.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        return user;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private long calculateServiceCount(UserAccount user) {
        List<ServiceRequestStatus> finalStatuses = Stream.of(ServiceRequestStatus.values())
                .filter(ServiceRequestStatus::isFinalState)
                .toList();
        return switch (user.getUserType()) {
            case COMPANY -> serviceRequestRepository.countByCompanyAndStatusIn(user, finalStatuses);
            case FACTION -> serviceRequestRepository.countByFactionAndStatusIn(user, finalStatuses);
            default -> 0L;
        };
    }
}
