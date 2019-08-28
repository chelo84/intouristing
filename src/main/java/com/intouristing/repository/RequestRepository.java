package com.intouristing.repository;

import com.intouristing.model.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
public interface RequestRepository extends JpaRepository<Request, Long> {
}
