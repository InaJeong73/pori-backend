package com.pori.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(unique = true)
    private String handle;

    private String displayName;

    @Enumerated(EnumType.STRING)
    private JobCategory jobCategory;

    @Column(name = "school_year")
    private Short year;
    private String schoolGroup;
    private String schoolName;

    @Column(nullable = false)
    private short privacyLevel = 1;

    @Column(columnDefinition = "TEXT")
    private String intro;

    private String avatarUrl;

    private boolean deleted = false;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @Builder
    public User(String email, String passwordHash, String handle, String displayName) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.handle = handle;
        this.displayName = displayName;
    }

    public void updateProfile(String handle, String displayName, JobCategory jobCategory,
                               Short year, String schoolGroup, String schoolName,
                               String intro, short privacyLevel) {
        if (handle != null) this.handle = handle;
        if (displayName != null) this.displayName = displayName;
        if (jobCategory != null) this.jobCategory = jobCategory;
        if (year != null) this.year = year;
        if (schoolGroup != null) this.schoolGroup = schoolGroup;
        if (schoolName != null) this.schoolName = schoolName;
        if (intro != null) this.intro = intro;
        this.privacyLevel = privacyLevel;
    }

    public void updateAvatar(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void softDelete() {
        this.deleted = true;
    }
}
