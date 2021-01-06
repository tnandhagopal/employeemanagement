package com.nphc.swe.persistence.service;

import com.nphc.swe.model.dto.UserDTO;
import com.nphc.swe.persistence.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@DataJpaTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    private UserService userService;

    private UserDTO user1, user2;


    @BeforeEach
    void setUp() {
        this.userService = new UserService(this.userRepository);
        user1 = UserDTO.builder()
                .id("e0001")
                .login("hpotter")
                .name("Harry Potter")
                .salary(new BigDecimal("1234.00"))
                .startDate(LocalDate.of(2016, 11, 1))
                .createdAt(LocalDateTime.now())
                .createdBy("ADMIN")
                .build();

        user2 = UserDTO.builder()
                .id("e0002")
                .login("rwesley")
                .name("Ron Weasley")
                .salary(new BigDecimal("19234.50"))
                .startDate(LocalDate.of(2001, 11, 16))
                .createdAt(LocalDateTime.now())
                .createdBy("ADMIN")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void save() {

        UserDTO user1_actual = this.userService.save(user1);

        assertThat(this.userRepository.findById(user1.getId())).contains(UserDTO.to(user1));

        assertThat(user1_actual).isEqualTo(user1);

        UserDTO user2_actual = this.userService.save(user2);

        assertThat(this.userRepository.findById(user2.getId())).contains(UserDTO.to(user2));

        assertThat(user2_actual).isEqualTo(user2);
    }


    @Test
    void findById() {
        save();
        assertThat(userService.findById(user1.getId())).contains(user1);
        assertThat(userService.findById(user2.getId())).contains(user2);
        assertThat(userService.findById("notexits")).isEmpty();
    }

    @Test
    void isUserExistsByLogin() {
        save();
        assertTrue(userService.isUserExistsByLogin(user1.getLogin()));
        assertTrue(userService.isUserExistsByLogin(user2.getLogin()));
        assertFalse(userService.isUserExistsByLogin("wrong"));
    }

    @Test
    void findByLogin() {
        save();
        assertThat(userService.findByLogin(user1.getLogin())).contains(user1);
        assertThat(userService.findByLogin(user2.getLogin())).contains(user2);
        assertThat(userService.findByLogin("notexits")).isEmpty();
    }

    @Test
    void delete() {
        save();
        assertThat(userService.findById(user1.getId())).contains(user1);
        assertThat(userService.findById(user2.getId())).contains(user2);

        userService.delete(user1.getId());
        assertThat(userService.findById(user1.getId())).isEmpty();

        userService.delete(user2.getId());
        assertThat(userService.findById(user2.getId())).isEmpty();

    }

}