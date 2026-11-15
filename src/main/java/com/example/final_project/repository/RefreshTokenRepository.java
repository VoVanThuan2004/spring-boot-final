package com.example.final_project.repository;

import com.example.final_project.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshToken r set r.revoked = true, r.revokedAt = :now " +
            "where r.user.id = :userId and r.revoked = false")
    int revokeAllByUserId(@Param("userId") int userId, @Param("now") Instant now);

    long deleteByExpiresAtBefore(Instant cutoff);
}
