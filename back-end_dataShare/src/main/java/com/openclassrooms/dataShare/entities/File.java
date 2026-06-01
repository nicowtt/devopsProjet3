package com.openclassrooms.dataShare.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fil_id_pk")
    private Long id;

    @UuidGenerator
    @Column(name = "fil_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "fil_name", nullable = false)
    private String name;

    @Column(name = "fil_size", nullable = false)
    private Long size;

    @Column(name = "fil_mime_type", nullable = false)
    private String mimeType;

    @CreationTimestamp
    @Column(name = "fil_created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "fil_expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "fil_password")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fil_usr_id_fk", nullable = true)
    private User owner;
}