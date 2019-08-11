package com.intouristing.intouristing.service;

import com.intouristing.intouristing.exceptions.NotFoundException;
import com.intouristing.intouristing.model.dto.UserDTO;
import com.intouristing.intouristing.model.entity.User;
import com.intouristing.intouristing.model.entity.UserPosition;
import com.intouristing.intouristing.model.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@Service
public class UserService {

    private final
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User find(Long id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class, id));
    }

    public User create(UserDTO userDTO) {
        User user = User
                .builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .userPosition(UserPosition.parseUserPosition(userDTO.getUserPosition()))
                .build();
        user.getUserPosition().setUser(user);
        String password = new BCryptPasswordEncoder().encode(userDTO.getPassword());
        user.setPassword(password);
        userRepository.save(user);

        return user;
    }
}
