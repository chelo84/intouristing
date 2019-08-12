package com.intouristing.intouristing.service;

import com.intouristing.intouristing.model.entity.User;

/**
 * Created by Marcelo Lacroix on 12/08/19.
 */
public class AccountServiceImpl implements AccountService {
    private User user;

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(final User user) {
        this.user = user;
    }
}

