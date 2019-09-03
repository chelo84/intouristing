package com.intouristing.repository;

import com.intouristing.model.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
}