package com.intouristing.repository;

import com.intouristing.model.entity.PrivateChat;
import com.intouristing.model.key.PrivateChatId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
public interface PrivateChatRepository extends JpaRepository<PrivateChat, PrivateChatId> {
}
