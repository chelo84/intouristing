package com.intouristing.websocket.service;

import com.intouristing.model.dto.UserPositionDTO;
import com.intouristing.model.entity.User;
import com.intouristing.model.entity.UserPosition;
import com.intouristing.repository.UserPositionRepository;
import com.intouristing.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.isNull;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@Service
@Transactional
public class SearchWsService extends RootWsService {

    private final UserPositionRepository userPositionRepository;
    private final UserRepository userRepository;

    @Autowired
    public SearchWsService(UserPositionRepository userPositionRepository, UserRepository userRepository) {
        this.userPositionRepository = userPositionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> search(Double radius) {
        User currentUser = super.getUser();

        double latitude = currentUser.getUserPosition().getLatitude(),
                longitude = currentUser.getUserPosition().getLongitude();

        double kmInLongitudeDegree = 111.320 * Math.cos(latitude / 180.0 * Math.PI),
                deltaLat = radius / 111.1,
                deltaLong = radius / kmInLongitudeDegree,
                minLat = latitude - deltaLat,
                maxLat = latitude + deltaLat,
                minLong = longitude - deltaLong,
                maxLong = longitude + deltaLong;

        return userPositionRepository.findAllUsersInRange(minLat, maxLat, minLong, maxLong, currentUser.getId());
    }

    public void updatePosition(UserPositionDTO userPositionDTO) {
        User user = super.getUser();
        UserPosition userPosition = userPositionRepository.findByUser(user)
                .orElseGet(() -> UserPosition.parseUserPosition(userPositionDTO));
        if (isNull(userPosition.getUser())) {
            user.setUserPosition(userPosition);
            userPosition.setUser(user);
            userRepository.save(user);
        }

        userPosition.setAccuracy(userPositionDTO.getAccuracy());
        userPosition.setHeading(userPositionDTO.getHeading());
        userPosition.setLatitude(userPositionDTO.getLatitude());
        userPosition.setLongitude(userPositionDTO.getLongitude());
        userPosition.setSpeed(userPositionDTO.getSpeed());

        userRepository.save(user);
    }
}
