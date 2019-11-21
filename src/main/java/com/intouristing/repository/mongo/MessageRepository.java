package com.intouristing.repository.mongo;

import com.intouristing.model.entity.mongo.Message;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
public interface MessageRepository extends MongoRepository<Message, ObjectId> {

    List<Message> findAllByChatGroup(Long chatGroupId, Pageable pageable);

    List<Message> findAllByPrivateChat_FirstUserAndPrivateChat_SecondUser(
            Long firstUser,
            Long secondUser,
            Pageable pageable
    );

    Message findFirstByPrivateChat_FirstUserAndPrivateChat_SecondUserOrderBySentAtAsc(
            Long firstUser,
            Long secondUser
    );

}
