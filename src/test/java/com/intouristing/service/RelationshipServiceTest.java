package com.intouristing.service;

import com.intouristing.Application;
import com.intouristing.model.dto.UserDTO;
import com.intouristing.model.entity.Relationship;
import com.intouristing.model.entity.User;
import com.intouristing.model.key.RelationshipId;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Marcelo Lacroix on 28/08/19.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RelationshipServiceTest {

    @Autowired
    UtilService utilService;
    @Autowired
    UserService userService;
    @Autowired
    RelationshipService relationshipService;

    @Test
    public void shouldCreateRelationshipOfTypeFriendship() {
        UserDTO userDTO1 = utilService.buildUserDTO();
        UserDTO userDTO2 = utilService.buildAnotherUserDTO();

        User user1 = userService.create(userDTO1);
        User user2 = userService.create(userDTO2);

        Relationship friendship = relationshipService.createFriendship(user1, user2);

        assertNotNull(friendship);
        assertNotNull(friendship.getFirstUser());
        assertNotNull(friendship.getSecondUser());
    }

    @Test
    public void shouldCreateRelationshipIdWithSecondUserIdBeingGreaterThanFirstUserId() {
        UserDTO userDTO1 = utilService.buildUserDTO();
        UserDTO userDTO2 = utilService.buildAnotherUserDTO();

        User user1 = userService.create(userDTO1);
        User user2 = userService.create(userDTO2);

        RelationshipId relationshipId = relationshipService.createRelationshipId(user1.getId(), user2.getId());

        assertTrue(relationshipId.getFirstUser() < relationshipId.getSecondUser());
    }
}
