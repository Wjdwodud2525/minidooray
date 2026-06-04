package com.nhnacademy.accountapi.repository;

import com.nhnacademy.accountapi.entity.User;
import com.nhnacademy.accountapi.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findAllByStatus(UserStatus userStatus);
    Optional<User> findByIdAndStatus(String userId, UserStatus userStatus);
    boolean existsByIdAndStatus(String userId, UserStatus userStatus);

    @Modifying
    @Query("update User u set u.status = :userStatus where u.id = :userId")
    int updateStatusByUserId(
            @Param("userId") String userId,
            @Param("userStatus") UserStatus userStatus);
    @Modifying
    @Query("update User u set u.lastLoginAt = :loginAt where u.id = :userId")
    int updateLastLoginAtByUserId(
            @Param("userId") String userId,
            @Param("loginAt") ZonedDateTime loginAt);

    @Modifying
    @Query("update User u set u.email = :userEmail, u.password = :userPassword where u.id = :userId")
    int updateUserByUserId(
            @Param("userId") String userId,
            @Param("userEmail") String userEmail,
            @Param("userPassword") String userPassword);
    @Modifying
    @Query("update User u set u.status = 'DORMANT' where u.status = 'ACTIVE' and u.lastLoginAt <= :cutoff")
    int updateToDormant(@Param("cutoff") ZonedDateTime cutoff);
}
