package com.nphc.swe.persistence.service;

import com.nphc.swe.model.dto.UserDTO;
import com.nphc.swe.persistence.entity.User;
import com.nphc.swe.persistence.repo.UserRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
