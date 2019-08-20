package com.intouristing.intouristing.service.websocket;

import com.intouristing.intouristing.model.entity.User;
import com.intouristing.intouristing.model.repository.UserPositionRepository;
import com.intouristing.intouristing.model.repository.UserRepository;
import com.intouristing.intouristing.service.account.AccountWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@Service
public class UserWsService extends RootWsService {

    private final UserRepository userRepository;
    private final AccountWsService accountWsService;
    private final UserPositionRepository userPositionRepository;

    @Autowired
    public UserWsService(UserRepository userRepository, AccountWsService accountWsService, UserPositionRepository userPositionRepository) {
        this.userRepository = userRepository;
        this.accountWsService = accountWsService;
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
                minLong = longitude + deltaLong,
                maxLong = longitude + deltaLong;

        return userPositionRepository.findAllUsersButCurrentInRange(currentUser.getId(), minLat, maxLat, minLong, maxLong);
    }

}
