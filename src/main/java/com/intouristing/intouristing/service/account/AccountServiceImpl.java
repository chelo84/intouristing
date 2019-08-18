package com.intouristing.intouristing.service.account;

import com.intouristing.intouristing.security.Account;
import org.springframework.stereotype.Service;

/**
 * Created by Marcelo Lacroix on 12/08/19.
 */
@Service
public class AccountServiceImpl implements AccountService, AccountWsService {

    private Account account;

    private Boolean isSearchCancelled;

    private Boolean isSearchFinished;

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
        return isSearchCancelled;
    }

    @Override
    public void setIsSearchCancelled(Boolean isSearchCancelled) {
        this.isSearchCancelled = isSearchCancelled;
    }

    @Override
    public Boolean isSearchFinished() {
        return isSearchFinished;
    }

    @Override
    public void setIsSearchFinished(Boolean isSearchFinished) {
        this.isSearchFinished = isSearchFinished;
    }
}

