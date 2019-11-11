package com.intouristing.repository;

import com.intouristing.model.entity.User;
import com.intouristing.model.entity.UserPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by Marcelo Lacroix on 18/08/2019.
 */
public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {

    @Query(value = "select user from UserPosition userPosition " +
            "join userPosition.user user " +
            "left join user.requestsAsSender requestsAsSender " +
            "left join user.requestsAsDestination requestsAsDestination " +
            "where userPosition.latitude >= ?1 " +
            "and userPosition.latitude <= ?2 " +
            "and userPosition.longitude >= ?3 " +
            "and userPosition.longitude <= ?4 " +
            "and user.id <> ?5 " +
            "and ((requestsAsSender.id is null and requestsAsDestination.id is null) " +
            "or ((requestsAsSender.destination.id is null or requestsAsSender.destination.id <> ?5) " +
            "and (requestsAsDestination.sender.id is null or requestsAsDestination.sender.id <> ?5))) ")
    List<User> findAllUsersInRange(double minLat, double maxLat, double minLong, double maxLong, long userId);

    Optional<UserPosition> findByUser(User user);

    Optional<UserPosition> findByUserId(Long userId);

}
