package com.intouristing.repository;

import com.intouristing.model.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("select request from Request request " +
            "where (request.sender.id = ?1 " +
            "   and request.destination.id = ?2) " +
            "or (request.sender.id = ?2 " +
            "   and request.destination.id = ?1) ")
    Optional<Request> findByBothUsers(Long user1, Long user2);

    @Query("select request from Request request " +
            "where (request.sender.id = ?1 " +
            "or request.destination.id = ?1)" +
            "and request.acceptedAt is null " +
            "and request.declinedAt is null ")
    List<Request> findAllPendingByDestinationIdOrSenderId(Long userId);

    @Query("select count(request.id) from Request request " +
            "where (request.sender.id = ?1 " +
            "or request.destination.id = ?1) " +
            "and request.acceptedAt is null " +
            "and request.declinedAt is null ")
    long countAllPendingByDestinationIdOrSenderId(Long userId);
}
