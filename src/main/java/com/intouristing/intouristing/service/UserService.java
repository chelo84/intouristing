package com.intouristing.intouristing.service;

import com.intouristing.intouristing.exceptions.NotFoundException;
import com.intouristing.intouristing.model.dto.UserDTO;
import com.intouristing.intouristing.model.entity.User;
import com.intouristing.intouristing.model.entity.UserPosition;
import com.intouristing.intouristing.model.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@Service
public class UserService extends RootService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User find(Long id) throws Exception {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class, id));
    }

    public User create(UserDTO userDTO) {
        User user = User
                .builder()
                .name(userDTO.getName())
                .lastName(userDTO.getLastName())
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

    public User setAvatarImage(Long id, MultipartFile multipartFile) throws Exception {
        User user = this.find(id);
        byte[] avatarImage = multipartFile.getBytes();
        user.setAvatarImage(avatarImage);
        return userRepository.save(user);
    }

    public byte[] getAvatarImage(Long id, HttpServletResponse response) throws Exception {
        return userRepository.findById(id).map(User::getAvatarImage).orElse(null);
    }
}
