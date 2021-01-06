package com.nphc.swe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nphc.swe.exception.UserCreationException;
import com.nphc.swe.exception.UserDeSerializerException;
import com.nphc.swe.model.UserUploadResponse;
import com.nphc.swe.model.UsersDataResponse;
import com.nphc.swe.model.dto.UserDTO;
import com.nphc.swe.service.UsersDataService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/users")
@CommonsLog
public class UsersController {

    @Autowired
    private UsersDataService usersDataService;

    private final BigDecimal DEFAULT_MIN_SALARY = BigDecimal.ZERO;
    private final BigDecimal DEFAULT_MAX_SALARY = BigDecimal.valueOf(4000.00);

    private final Integer DEFAULT_OFFSET = 0;
    private final Integer DEFAULT_LIMIT = 0;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @GetMapping
    public ResponseEntity<UsersDataResponse> getUsers(@RequestParam Optional<BigDecimal> minSalary,
                                                      @RequestParam Optional<BigDecimal> maxSalary,
                                                      @RequestParam Optional<Integer> offset,
                                                      @RequestParam Optional<Integer> limit) {

        UsersDataResponse users = usersDataService.getUsers(minSalary.orElse(DEFAULT_MIN_SALARY),
                maxSalary.orElse(DEFAULT_MAX_SALARY),
                offset.orElse(DEFAULT_OFFSET),
                limit.orElse(DEFAULT_LIMIT));

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") String id) {

        Optional<UserDTO> users = this.usersDataService.getUser(id);

        if (users.isPresent()) {
            return ResponseEntity.ok(users.get());
        } else {
            return new ResponseEntity<>(
                    null,
                    HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping
    public ResponseEntity<UserUploadResponse> create(@RequestBody String user) {

        log.debug(format("user=%s", user));
        UserDTO userDTO;

        try {
            userDTO = objectMapper.readValue(user, UserDTO.class);
            this.usersDataService.create(userDTO);
        } catch (UserDeSerializerException e) {
            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        } catch (UserCreationException e) {
            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message("Wrong input")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(
                UserUploadResponse.builder()
                        .message("Successfully created")
                        .build(),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserUploadResponse> update(@RequestBody String user,
                                                     @PathVariable("id") String id) {

        log.debug(format("user=%s", user));
        UserDTO userDTO;

        try {
            userDTO = objectMapper.readValue(user, UserDTO.class);
            this.usersDataService.update(id, userDTO);
        } catch (UserDeSerializerException e) {
            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        } catch (UserCreationException e) {
            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message("Wrong input")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(
                UserUploadResponse.builder()
                        .message("Successfully updated")
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<UserUploadResponse> upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message("File is empty")
                            .build(),
                    HttpStatus.NO_CONTENT);
        }

        try {
            this.usersDataService.upload(file);

            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message("Data created or uploaded")
                            .build(),
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error while parse file", e);
            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserUploadResponse> delete(@PathVariable("id") String id) {

        if (this.usersDataService.delete(id)) {

            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message("Successfully deleted")
                            .build(),
                    HttpStatus.OK);

        } else {
            return new ResponseEntity<>(
                    UserUploadResponse.builder()
                            .message("No such employee")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }

    }

}
