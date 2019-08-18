package com.intouristing.intouristing.service.websocket;

import com.intouristing.intouristing.model.entity.User;
import com.intouristing.intouristing.model.entity.UserSearchControl;
import com.intouristing.intouristing.model.repository.UserRepository;
import com.intouristing.intouristing.service.account.AccountWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@Service
public class UserWsService extends RootWsService {

    private final UserRepository userRepository;
    private final AccountWsService accountWsService;

    @Autowired
    public UserWsService(UserRepository userRepository, AccountWsService accountWsService) {
        this.userRepository = userRepository;
        this.accountWsService = accountWsService;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<User> search(Integer count) {
        User user = super.getUser();
        if (count == 1) {
            this.resetUserSearchControl(user);
        }

        List<User> usersFound = new ArrayList<>();

        return usersFound;
    }

    @Transactional(propagation = Propagation.NEVER)
    public User cancelSearch() {
        User user = super.getUser();
        user.getUserSearchControl().setCancelledAt(LocalDateTime.now());
        userRepository.save(user);

        accountWsService.setSearchCancelled(true);

        return user;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserSearchControl resetUserSearchControl(User user) {
        UserSearchControl userSearchControl;
        if (isNull(user.getUserSearchControl())) {
            this.createUserSearchControl(user);
        }

        userSearchControl = user.getUserSearchControl();
        userSearchControl.setCancelledAt(null);
        userSearchControl.setFinishedAt(null);
        userSearchControl.setStartedAt(LocalDateTime.now());
        userRepository.save(user);

        accountWsService.setSearchCancelled(false);
        accountWsService.setSearchFinished(false);

        return user.getUserSearchControl();
    }

    private void createUserSearchControl(User user) {
        user.setUserSearchControl(UserSearchControl
                .builder()
                .user(user)
                .build());
        userRepository.save(user);
    }
}
