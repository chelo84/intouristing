package com.intouristing.intouristing.model.repository;

import com.intouristing.intouristing.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
