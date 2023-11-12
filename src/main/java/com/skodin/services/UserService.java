package com.skodin.services;

import com.skodin.exceptions.NotFoundException;
import com.skodin.models.UserEntity;
import com.skodin.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Transient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public static UserEntity getCurrentUser(){
        return (UserEntity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }


    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public UserEntity findById(Long aLong) {
        return userRepository.findById(aLong).orElseThrow(
                () -> new NotFoundException(String.format("User with id %d not found", aLong)));
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
    public UserEntity update(Long id, UserEntity user){
        UserEntity byId = findById(id);
        user.setId(byId.getId());
        user.setActivationCode(byId.getActivationCode());
        return saveAndFlush(user);
    }

    @Transactional
    public void deleteById(Long aLong) {
        findById(aLong);    // just check

        userRepository.deleteById(aLong);
    }
}
