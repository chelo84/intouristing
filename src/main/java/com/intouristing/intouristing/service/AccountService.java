package com.intouristing.intouristing.service;

import com.intouristing.intouristing.security.Account;

/**
 * Created by Marcelo Lacroix on 12/08/19.
 */
public interface AccountService {

    Account getAccount();

    void setAccount(Account account);

}
