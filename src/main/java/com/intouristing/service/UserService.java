package com.intouristing.service;

import com.intouristing.exceptions.NotFoundException;
import com.intouristing.model.dto.UserDTO;
import com.intouristing.model.entity.User;
import com.intouristing.model.entity.UserPosition;
import com.intouristing.repository.UserPositionRepository;
import com.intouristing.repository.UserRepository;
import com.intouristing.service.account.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@Service
public class UserService extends RootService {

    private final UserRepository userRepository;
    private final UserPositionRepository userPositionRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountService accountService;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserPositionRepository userPositionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       AccountService accountService) {
        this.userRepository = userRepository;
        this.userPositionRepository = userPositionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class, id));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User create(UserDTO userDTO) {
        User user = User
                .builder()
                .name(userDTO.getName())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .createdAt(LocalDateTime.now())
                .build();
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user = userRepository.save(user);

        user.setUserPosition(
                UserPosition.parseUserPosition(
                        userDTO.getUserPosition()
                )
        );
        user.getUserPosition().setUser(user);
        userPositionRepository.save(user.getUserPosition());

        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User update(UserDTO userDTO) {
        var user = userRepository.findById(
                accountService.getAccount().getId()
        ).get();
        user.setName(userDTO.getName());
        user.setLastName(userDTO.getLastName());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User setAvatarImage(Long id, MultipartFile multipartFile) throws IOException {
        User user = this.find(id);
        byte[] avatarImage = multipartFile.getBytes();
        user.setAvatarImage(avatarImage);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true, propagation = Propagation.NEVER)
    public byte[] getAvatarImage(Long id) {
        return userRepository.findById(id)
                .map(User::getAvatarImage)
                .orElse(null);
    }

    @Transactional(readOnly = true, propagation = Propagation.NEVER)
    public Boolean verifyUsername(String username) {
        return userRepository.findByUsername(
                username.trim().toLowerCase()
        ).isPresent();
    }

    @Transactional(readOnly = true, propagation = Propagation.NEVER)
    public Boolean verifyEmail(String email) {
        return userRepository.findByEmail(
                email.trim().toLowerCase()
        ).isPresent();
    }

    @Transactional(readOnly = true, propagation = Propagation.NEVER)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).get();
    }
}
