package com.intouristing.intouristing.service.account;

import com.intouristing.intouristing.model.entity.Account;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
public interface AccountWsService {

    Account getAccount();

    void setAccount(Account account);

    Boolean isSearchCancelled();

    void setSearchCancelled(Boolean isSearchCancelled);

    Boolean isSearchFinished();

    void setSearchFinished(Boolean isSearchFinished);
}
