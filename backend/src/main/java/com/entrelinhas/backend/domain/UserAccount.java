package com.entrelinhas.backend.domain;

import com.entrelinhas.backend.domain.converter.UserTypeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = UserTypeConverter.class)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(length = 14)
    private String cpf;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 18)
    private String cnpj;

    @Column(name = "state_registration", length = 50)
    private String stateRegistration;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_service_id")
    private ServiceEntity mainService;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "availability")
    private Boolean availability;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "last_access_at")
    private LocalDateTime lastAccessAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (availability == null) {
            availability = Boolean.TRUE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
