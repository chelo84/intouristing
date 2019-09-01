package com.intouristing.repository.mongo;

import com.intouristing.model.entity.mongo.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Marcelo Lacroix on 01/09/2019.
 */
public interface MessageRepository extends MongoRepository<Message, ObjectId> {
}
