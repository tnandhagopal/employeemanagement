package com.nphc.swe.service;

import com.nphc.swe.model.dto.UserDTO;
import com.nphc.swe.persistence.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@Tag("unit")
@SpringBootTest(classes = UsersDataService.class)
class UsersDataServiceTest {

    @Autowired
    private UsersDataService usersDataService;

    @MockBean
    private UserService userService;

    private UserDTO user1, user2;

    @BeforeEach
    void setUp() {

        user1 = UserDTO.builder()
                .id("e0001")
                .login("hpotter")
                .name("Harry Potter")
                .salary(new BigDecimal("1234.00"))
                .startDate(LocalDate.of(2001, 11, 16))
                .build();

        user2 = UserDTO.builder()
                .id("e0002")
                .login("rwesley")
                .name("Ron Weasley")
                .salary(new BigDecimal("19234.50"))
                .startDate(LocalDate.of(2001, 11, 16))
                .build();


    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void upload() throws IOException {

        MockMultipartFile file = new MockMultipartFile("file", UsersDataServiceTest.class.getClassLoader().getResource("static/sample/user_test_file_1.csv").openStream());

        this.usersDataService.upload(file);

        Mockito.verify(userService, Mockito.times(10)).save(any());
        Mockito.verify(userService, Mockito.times(1)).save(eq(user1));
        Mockito.verify(userService, Mockito.times(1)).save(eq(user2));

    }
}