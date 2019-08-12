package com.intouristing.intouristing.service;

import com.intouristing.intouristing.model.entity.User;

/**
 * Created by Marcelo Lacroix on 12/08/19.
 */
public interface AccountService {

    User getUser();

    void setUser(User user);

}
