package com.intouristing.intouristing.repository;

import com.intouristing.intouristing.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
