package com.nphc.swe.service;

import com.nphc.swe.exception.UserCreationException;
import com.nphc.swe.model.UsersDataResponse;
import com.nphc.swe.model.dto.UserDTO;
import com.nphc.swe.persistence.service.UserService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@CommonsLog
public class UsersDataService {

    @Autowired
    private UserService userService;

    public List<UserDTO> upload(MultipartFile file) throws IOException {

        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        CsvToBean<UserDTO> csvToBean = new CsvToBeanBuilder(reader)
                .withType(UserDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<UserDTO> users = csvToBean.parse();
        log.info(format("Users parsed -> %s", users));

        return this.userService.saveAll(users);

    }

    public UsersDataResponse getUsers(BigDecimal minSalary,
                                      BigDecimal maxSalary,
                                      Integer offset,
                                      Integer limit) {

        return UsersDataResponse.builder()
                .results(this.userService.findByFilters(minSalary, maxSalary, offset, limit, "id"))
                .build();
    }

    public Optional<UserDTO> getUser(String id) {

        return this.userService.findById(id);
    }

    public UserDTO create(UserDTO user) {

        if (this.userService.findById(user.getId()).isPresent()) {
            throw new UserCreationException("Employee ID already exists");
        }

        if (this.userService.isUserExistsByLogin(user.getLogin())) {
            throw new UserCreationException("Employee login not unique");
        }

        return this.userService.save(user);

    }

    public UserDTO update(String id, UserDTO user) {

        Optional<UserDTO> existsUserById = this.userService.findById(user.getId());

        if (!existsUserById.isPresent()) {
            throw new UserCreationException("No such employee");
        }

        Optional<UserDTO> existsUserByLogin = this.userService.findByLogin(user.getLogin());

        if (existsUserByLogin
                .filter(u -> !u.getId().equals(user.getId()))
                .isPresent()) {
            throw new UserCreationException("Employee login not unique");
        }

        return this.userService.save(user);
    }

    public boolean delete(String id) {

        Optional<UserDTO> existsUserById = this.userService.findById(id);

        if (!existsUserById.isPresent()) {
            return false;
        }

        this.userService.delete(id);

        return true;

    }
}
