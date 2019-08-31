package com.intouristing.websocket.service;

import com.intouristing.model.entity.User;
import com.intouristing.repository.UserPositionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@Service
public class SearchWsService extends RootWsService {

    private final UserPositionRepository userPositionRepository;

    @Autowired
    public SearchWsService(UserPositionRepository userPositionRepository) {
        this.userPositionRepository = userPositionRepository;
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

}
