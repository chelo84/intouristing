package com.intouristing.intouristing.service;

import com.intouristing.intouristing.security.Account;

/**
 * Created by Marcelo Lacroix on 12/08/19.
 */
public class AccountServiceImpl implements AccountService {
    private Account account;

    @Override
    public void setAccount(final Account accountCredentials) {
        this.account = accountCredentials;
    }

    @Override
    public Account getAccount() {
        return account;
    }
}

