package com.nphc.swe.persistence.service;

import com.nphc.swe.model.dto.UserDTO;
import com.nphc.swe.persistence.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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
@Tag("unit")
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    private UserService userService;

    private UserDTO user1, user2, user3, user4;


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

        user3 = UserDTO.builder()
                .id("e0003")
                .login("ssnape")
                .name("Severus Snape")
                .salary(new BigDecimal("4000.0"))
                .startDate(LocalDate.of(2001, 11, 16))
                .createdAt(LocalDateTime.now())
                .createdBy("ADMIN")
                .build();

        user4 = UserDTO.builder()
                .id("e0004")
                .login("rhagrid")
                .name("Rubeus Hagrid")
                .salary(new BigDecimal("3999.999"))
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

        UserDTO user3_actual = this.userService.save(user3);
        assertThat(this.userRepository.findById(user3.getId())).contains(UserDTO.to(user3));
        assertThat(user3_actual).isEqualTo(user3);

        UserDTO user4_actual = this.userService.save(user4);
        assertThat(this.userRepository.findById(user4.getId())).contains(UserDTO.to(user4));
        assertThat(user4_actual).isEqualTo(user4);
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

    @Test
    void findAll() {
        save();
        assertThat(this.userService.findAll(0, 2, "id")).containsExactly(user1, user2);
        assertThat(this.userService.findAll(1, 2, "id")).containsExactly(user3, user4);

        assertThat(this.userService.findAll(1, 0, "id")).containsExactly(user1, user2, user3, user4);


    }


    @Test
    void findByFilters() {
        save();
        assertThat(this.userService.findByFilters(BigDecimal.ZERO, BigDecimal.valueOf(4000), 0, 2, "id")).containsExactly(user1, user3);
        assertThat(this.userService.findByFilters(BigDecimal.ZERO, BigDecimal.valueOf(4000), 1, 2, "id")).containsExactly(user4);

        assertThat(this.userService.findByFilters(BigDecimal.ZERO, BigDecimal.valueOf(4000), 1, 0, "id")).containsExactly(user1, user3, user4);

    }

}