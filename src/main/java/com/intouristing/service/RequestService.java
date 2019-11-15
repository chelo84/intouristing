package com.intouristing.service;

import com.intouristing.model.entity.Request;
import com.intouristing.repository.RequestRepository;
import com.intouristing.service.account.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RequestService extends RootService {

    final RequestRepository requestRepository;
    final AccountService accountService;

    public RequestService(RequestRepository requestRepository,
                          AccountService accountService) {
        this.requestRepository = requestRepository;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true, propagation = Propagation.NEVER)
    public List<Request> findAll() {
        return requestRepository.findAllPendingByDestinationIdOrSenderId(
                accountService.getAccount().getId()
        );
    }

    @Transactional(readOnly = true, propagation = Propagation.NEVER)
    public long countAll() {
        return requestRepository.countAllPendingByDestinationIdOrSenderId(
                accountService.getAccount().getId()
        );
    }
}
