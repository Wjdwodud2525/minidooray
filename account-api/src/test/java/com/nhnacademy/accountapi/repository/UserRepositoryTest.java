package com.nhnacademy.accountapi.repository;

import com.nhnacademy.accountapi.entity.User;
import com.nhnacademy.accountapi.entity.UserStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    private User user;


    @BeforeEach
    void setUp(){
        user = new User(
                "test",
                "1234",
                "test@email.com",
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                UserStatus.ACTIVE
        );

    }

    @Test
    @DisplayName("유저 생성 테스트")
    void registerUserTest(){

        User result  = userRepository.save(user);

        assertAll(
                () -> assertEquals(user.getUserId(), result.getUserId()),
                () -> assertEquals(user.getEmail(), result.getEmail()),
                () -> assertEquals(user.getPassword(), result.getPassword()),
                () -> assertEquals(user.getCreatedAt(), result.getCreatedAt()),
                () -> assertEquals(user.getLastLoginAt(), result.getLastLoginAt()),
                () -> assertEquals(user.getStatus(), result.getStatus())
        );
    }

    @Test
    @DisplayName("모든 유저 조회 테스트 - 활성화된 유저 기준")
    void findAllByStatusTest(){

        List<User> resultUserList1 = userRepository.findAllByStatus(UserStatus.ACTIVE);
        assertEquals(0, resultUserList1.size());

        userRepository.save(user);

        List<User> resultUserList2 = userRepository.findAllByStatus(UserStatus.ACTIVE);
        assertEquals(1, resultUserList2.size());
    }

    @Test
    @DisplayName("유저 아이디 조회 테스트 - 활성화된 유저 기준")
    void findByIdAndStatusTest(){
        userRepository.save(user);
        User result = userRepository.findByIdAndStatus("test", UserStatus.ACTIVE).orElse(new User());

        assertAll(
                () -> assertEquals(user.getUserId(), result.getUserId()),
                () -> assertEquals(user.getEmail(), result.getEmail()),
                () -> assertEquals(user.getPassword(), result.getPassword()),
                () -> assertEquals(user.getCreatedAt(), result.getCreatedAt()),
                () -> assertEquals(user.getLastLoginAt(), result.getLastLoginAt()),
                () -> assertEquals(user.getStatus(), result.getStatus())
        );
    }

    @Test
    @DisplayName("유저 상태 업데이트 테스트")
    void updateStatusByUserIdTest(){
        userRepository.save(user);

        int colum = userRepository.updateStatusByUserId(user.getUserId(), UserStatus.DELETED);
        entityManager.flush();
        entityManager.clear();
        User chagnedStatusUser = userRepository.findById(user.getUserId()).get();
        assertAll(
                () -> assertEquals(1, colum),
                () -> assertEquals(UserStatus.DELETED, chagnedStatusUser.getStatus())
        );
    }

    @Test
    @DisplayName("마지막 로그인 시간 업데이트 테스트")
    void updateLastLoginAtByUserIdTest(){
        userRepository.save(user);

        ZonedDateTime loginAt = ZonedDateTime.of(
                2026, 5, 15,
                14, 30, 0, 0,
                ZoneId.of("Asia/Seoul")
        );
        userRepository.updateLastLoginAtByUserId(user.getUserId(), loginAt);
        entityManager.flush();
        entityManager.clear();
        User result = userRepository.findById(user.getUserId()).orElse(null);

        assertEquals(loginAt, result.getLastLoginAt().withZoneSameLocal(ZoneId.of("Asia/Seoul")));
    }

    @Test
    @DisplayName("유저 비밀번호, 이메일 업데이트 테스트")
    void updateUserByUserId(){
        userRepository.save(user);
        userRepository.updateUserByUserId(user.getUserId(), "update@email.con", "5678");
        entityManager.flush();
        entityManager.clear();
        User result = userRepository.findById(user.getUserId()).orElse(null);
        assertAll(
                () -> assertEquals("update@email.con", result.getEmail()),
                () -> assertEquals("5678", result.getPassword())
        );
    }


}
