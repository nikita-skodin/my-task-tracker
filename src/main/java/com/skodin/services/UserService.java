package com.skodin.services;

import com.skodin.exceptions.ForbiddenException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.UserEntity;
import com.skodin.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public static UserEntity getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return (UserEntity) principal;
    }


    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public UserEntity findById(Long aLong) {
        return userRepository.findById(aLong).orElseThrow(
                () -> new NotFoundException(String.format("User with id %d is not found", aLong)));
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserEntity> findByActivationCode(String code) {
        return userRepository.findByActivationCode(code);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public <S extends UserEntity> S saveAndFlush(S entity) {
        return userRepository.saveAndFlush(entity);
    }

    @Transactional
    public UserEntity updateEnable(UserEntity user) {
        user.setActivationCode(null);
        saveAndFlush(user);
        return user;
    }

    @Transactional
    public UserEntity update(Long id, UserEntity user) {
        UserEntity userFromDB = findById(id);
        checkUsersRules(userFromDB);

        userFromDB.setUsername(user.getUsername());
        userFromDB.setEmail(user.getEmail());

        if (user.getPassword() != null) {

            userFromDB.setPassword(new BCryptPasswordEncoder()
                    .encode(user.getPassword()));
        }

        if (user.getActivationCode() == null) {
            userFromDB.setActivationCode(null);
        }

        System.err.println(userFromDB);
        return saveAndFlush(userFromDB);
    }

    @Transactional
    public void deleteById(Long aLong) {
        UserEntity user = findById(aLong);// just check
        checkUsersRules(user);
        userRepository.deleteById(aLong);
    }

    private void checkUsersRules(UserEntity user) {
        if (!user.getId().equals(UserService.getCurrentUser().getId())) {
            throw new ForbiddenException("FORBIDDEN");
        }
    }
}
