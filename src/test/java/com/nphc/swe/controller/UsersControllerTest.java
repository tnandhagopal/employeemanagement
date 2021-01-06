package com.nphc.swe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nphc.swe.exception.UserCreationException;
import com.nphc.swe.model.UsersDataResponse;
import com.nphc.swe.model.dto.UserDTO;
import com.nphc.swe.service.UsersDataService;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsersController.class)
@CommonsLog
@Tag("unit")
@ActiveProfiles("test")
class UsersControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsersDataService usersDataService;

    private UserDTO user1, user2;

    private UsersDataResponse users;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();

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

        users = UsersDataResponse.builder()
                .results(Arrays.asList(user1, user2))
                .build();

    }

    @AfterEach
    void tearDown() {
        Mockito.clearInvocations(usersDataService);
        Mockito.reset(usersDataService);
    }

    @Nested
    class getUsers {

        @Test
        void withoutAnyParam() throws Exception {
            when(usersDataService.getUsers(any(), any(), any(), any())).thenReturn(users);

            String jsonExpected = "{\"results\":[{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"},{\"id\":\"e0002\",\"login\":\"rwesley\",\"name\":\"Ron Weasley\",\"salary\":19234.50,\"startDate\":\"2001-11-16\"}]}";

            mvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            verify(usersDataService, times(1)).getUsers(any(), any(), any(), any());
            verify(usersDataService, times(1)).getUsers(eq(BigDecimal.ZERO),
                    eq(BigDecimal.valueOf(4000.00)),
                    eq(0),
                    eq(0));


        }

        @Test
        void withAllParam() throws Exception {
            when(usersDataService.getUsers(any(), any(), any(), any())).thenReturn(users);

            String jsonExpected = "{\"results\":[{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"},{\"id\":\"e0002\",\"login\":\"rwesley\",\"name\":\"Ron Weasley\",\"salary\":19234.50,\"startDate\":\"2001-11-16\"}]}";

            mvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("minSalary", "100.0")
                    .param("maxSalary", "500.0")
                    .param("offset", "10")
                    .param("limit", "100")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            verify(usersDataService, times(1)).getUsers(any(), any(), any(), any());
            verify(usersDataService, times(1)).getUsers(eq(BigDecimal.valueOf(100.0)),
                    eq(BigDecimal.valueOf(500.0)),
                    eq(10),
                    eq(100));
        }

        @Disabled
        @Test
        void bad_parameters() throws Exception {
            when(usersDataService.getUsers(any(), any(), any(), any())).thenReturn(users);

            String jsonExpected = "{\"results\":[{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"},{\"id\":\"e0002\",\"login\":\"rwesley\",\"name\":\"Ron Weasley\",\"salary\":19234.50,\"startDate\":\"2001-11-16\"}]}";

            mvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("dfdf", "100.0")
                    .param("maxSalary", "500.0")
                    .param("offset", "10")
                    .param("limit", "100")
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            verify(usersDataService, times(1)).getUsers(any(), any(), any(), any());
            verify(usersDataService, times(1)).getUsers(eq(BigDecimal.valueOf(100.0)),
                    eq(BigDecimal.valueOf(500.0)),
                    eq(10),
                    eq(100));
        }
    }

    @Nested
    class upload {

        @Test
        void success() throws Exception {

            when(usersDataService.upload(any())).thenReturn(Arrays.asList(user1, user2));


            MockMultipartFile file = new MockMultipartFile("file",
                    "user_test_file_1.csv",
                    MediaType.TEXT_PLAIN_VALUE,
                    UsersControllerTest.class.getClassLoader().getResource("static/sample/user_test_file_1.csv").openStream());


            String jsonExpected = "{\"message\":\"Data created or uploaded\"}";

            mvc.perform(multipart("/users/upload")
                    .file(file))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

        }

        @Test
        void emptyFile() throws Exception {

            when(usersDataService.upload(any())).thenReturn(Arrays.asList(user1, user2));

            MockMultipartFile file = new MockMultipartFile("file",
                    "user_test_file_1.csv",
                    MediaType.TEXT_PLAIN_VALUE,
                    "".getBytes(StandardCharsets.UTF_8)
            );

            String jsonExpected = "{\"message\":\"File is empty\"}";

            mvc.perform(multipart("/users/upload")
                    .file(file))
                    .andExpect(status().isNoContent())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

        }

        @Test
        void exception() throws Exception {
            when(usersDataService.upload(any())).thenThrow(new RuntimeException("Error while load data"));

            MockMultipartFile file = new MockMultipartFile("file",
                    "user_test_file_1.csv",
                    MediaType.TEXT_PLAIN_VALUE,
                    UsersControllerTest.class.getClassLoader().getResource("static/sample/user_test_file_1.csv").openStream());

            String jsonExpected = "{\"message\":\"Error while load data\"}";

            mvc.perform(multipart("/users/upload")
                    .file(file))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));
        }
    }

    @Nested
    class create {

        @Test
        void successfully_created() throws Exception {

            String jsonExpected = "{\"message\":\"Successfully created\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));
            user1.setSalary(new BigDecimal("1234.0"));
            verify(usersDataService, times(1)).create(any());
            verify(usersDataService, times(1)).create(eq(user1));
        }

        @Test
        void invalid_date() throws Exception {

            String jsonExpected = "{\"message\":\"Invalid date\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"01-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            verify(usersDataService, times(0)).create(any());

        }

        @Test
        void invalid_salary() throws Exception {

            String jsonExpected = "{\"message\":\"Invalid salary\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":\"2001-11-16\",\"startDate\":\"2001-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            verify(usersDataService, times(0)).create(any());

        }

        @Test
        void employee_id_already_exists() throws Exception {

            when(usersDataService.create(any())).thenThrow(new UserCreationException("Employee ID already exists"));

            String jsonExpected = "{\"message\":\"Employee ID already exists\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            user1.setSalary(new BigDecimal("1234.0"));
            verify(usersDataService, times(1)).create(any());
            verify(usersDataService, times(1)).create(eq(user1));
        }

        @Test
        void employee_login_not_unique() throws Exception {

            when(usersDataService.create(any())).thenThrow(new UserCreationException("Employee login not unique"));

            String jsonExpected = "{\"message\":\"Employee login not unique\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            user1.setSalary(new BigDecimal("1234.0"));

            verify(usersDataService, times(1)).create(any());
            verify(usersDataService, times(1)).create(eq(user1));
        }
    }

    @Nested
    class update {

        @Test
        void successfully_updated() throws Exception {

            String jsonExpected = "{\"message\":\"Successfully updated\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(put("/users/e0001")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            user1.setSalary(new BigDecimal("1234.0"));
            verify(usersDataService, times(1)).update(anyString(), any());
            verify(usersDataService, times(1)).update(eq(user1.getId()), eq(user1));
        }

        @Test
        void invalid_date() throws Exception {

            String jsonExpected = "{\"message\":\"Invalid date\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"01-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(put("/users/e0001")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            verify(usersDataService, times(0)).update(anyString(), any());

        }

        @Test
        void invalid_salary() throws Exception {

            String jsonExpected = "{\"message\":\"Invalid salary\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":\"2001-11-16\",\"startDate\":\"2001-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(put("/users/e0001")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            verify(usersDataService, times(0)).update(anyString(), any());

        }

        @Test
        void employee_id_already_exists() throws Exception {

            when(usersDataService.update(anyString(), any())).thenThrow(new UserCreationException("No such employee"));

            String jsonExpected = "{\"message\":\"No such employee\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(put("/users/e0001")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            user1.setSalary(new BigDecimal("1234.0"));
            verify(usersDataService, times(1)).update(anyString(), any());
            verify(usersDataService, times(1)).update(eq(user1.getId()), eq(user1));
        }

        @Test
        void employee_login_not_unique() throws Exception {

            when(usersDataService.update(anyString(), any())).thenThrow(new UserCreationException("Employee login not unique"));

            String jsonExpected = "{\"message\":\"Employee login not unique\"}";

            log.info(format("jsonExpected=%s", jsonExpected));

            String jsonInput = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"}";

            log.info(format("JsonInput=%s", jsonInput));

            mvc.perform(put("/users/e0001")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            user1.setSalary(new BigDecimal("1234.0"));
            verify(usersDataService, times(1)).update(anyString(), any());
            verify(usersDataService, times(1)).update(eq(user1.getId()), eq(user1));
        }
    }

    @Nested
    class getUser {

        @Test
        void success() throws Exception {
            String jsonExpected = "{\"id\":\"e0001\",\"login\":\"hpotter\",\"name\":\"Harry Potter\",\"salary\":1234.00,\"startDate\":\"2001-11-16\"}";

            when(usersDataService.getUser(eq(user1.getId()))).thenReturn(Optional.of(user1));
            mvc.perform(get("/users/e0001")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            verify(usersDataService, times(1)).getUser(anyString());

        }

        @Test
        void fail() throws Exception {
            String jsonExpected = "";

            when(usersDataService.getUser(eq(user1.getId()))).thenReturn(Optional.empty());
            mvc.perform(get("/users/e0001")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(status().isBadRequest())
            ;

            verify(usersDataService, times(1)).getUser(anyString());

        }
    }

    @Nested
    class delete {

        @Test
        void success() throws Exception {
            String jsonExpected = "{\"message\":\"Successfully deleted\"}";

            when(usersDataService.delete(eq(user1.getId()))).thenReturn(true);
            mvc.perform(delete("/users/e0001")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));

            verify(usersDataService, times(1)).delete(anyString());

        }

        @Test
        void fail() throws Exception {
            String jsonExpected = "{\"message\":\"No such employee\"}";

            when(usersDataService.delete(eq(user1.getId()))).thenReturn(false);
            mvc.perform(delete("/users/e0001")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(jsonExpected));
            ;

            verify(usersDataService, times(1)).delete(anyString());

        }
    }
}