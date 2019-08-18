package com.intouristing.intouristing.service.account;

import com.intouristing.intouristing.security.Account;
import org.springframework.stereotype.Service;

/**
 * Created by Marcelo Lacroix on 12/08/19.
 */
@Service
public class AccountServiceImpl implements AccountService, AccountWsService {

    private Account account;

    private Boolean searchCancelled;

    private Boolean searchFinished;

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void setAccount(final Account account) {
        this.account = account;
    }

    @Override
    public Boolean isSearchCancelled() {
        return searchCancelled;
    }

    @Override
    public void setSearchCancelled(Boolean isSearchCancelled) {
        this.searchCancelled = isSearchCancelled;
    }

    @Override
    public Boolean isSearchFinished() {
        return searchFinished;
    }

    @Override
    public void setSearchFinished(Boolean isSearchFinished) {
        this.searchFinished = isSearchFinished;
    }
}

