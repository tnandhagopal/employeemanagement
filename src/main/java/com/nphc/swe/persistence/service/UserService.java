package com.nphc.swe.persistence.service;

import com.nphc.swe.model.dto.UserDTO;
import com.nphc.swe.persistence.entity.User;
import com.nphc.swe.persistence.repo.UserRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@CommonsLog
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO save(UserDTO user) {
        return UserDTO.from(this.userRepository.save(UserDTO.to(user)));
    }

    @Transactional
    public List<UserDTO> saveAll(List<UserDTO> users) {

        Iterable<User> userIterable = () -> users
                .stream()
                .map(UserDTO::to)
                .map(userDTO -> {

                    if (userDTO.getCreatedBy() == null) {
                        userDTO.setCreatedBy("ADMIN");
                    }

                    if (userDTO.getCreatedAt() == null) {
                        userDTO.setCreatedAt(LocalDateTime.now());
                    }

                    return userDTO;
                })
                .iterator();

        return this.userRepository.saveAll(userIterable)
                .stream()
                .map(UserDTO::from)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> findById(String id) {
        return this.userRepository.findById(id).map(UserDTO::from);
    }

    public boolean isUserExistsByLogin(String login) {
        return this.userRepository.existsByLogin(login);
    }

    public Optional<UserDTO> findByLogin(String login) {
        return this.userRepository.findByLogin(login).map(UserDTO::from);
    }

    public void delete(String id) {
        this.userRepository.deleteById(id);
    }

    public List<UserDTO> findAll(Integer pageNo, Integer pageSize, String sortBy) {

        Sort sort = Sort.by(Sort.Direction.ASC, sortBy);

        if (pageSize < 1) {
            return this.userRepository.findAll(sort)
                    .stream()
                    .map(UserDTO::from)
                    .collect(Collectors.toList());
        }

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        Page<User> pagedResult = this.userRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent().stream().map(UserDTO::from).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public List<UserDTO> findByFilters(BigDecimal minSalary,
                                       BigDecimal maxSalary,
                                       Integer pageNo,
                                       Integer pageSize,
                                       String sortBy) {

        Sort sort = Sort.by(Sort.Direction.ASC, sortBy);

        if (pageSize < 1) {
            return this.userRepository.findBySalaryBetween(sort, minSalary, maxSalary)
                    .stream()
                    .map(UserDTO::from)
                    .collect(Collectors.toList());
        }

        Pageable paging = PageRequest.of(pageNo, pageSize, sort);

        Page<User> pagedResult = this.userRepository.findBySalaryBetween(paging, minSalary, maxSalary);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent()
                    .stream()
                    .map(UserDTO::from)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }


}
