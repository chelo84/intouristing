package com.intouristing.websocket.messagemapping;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
public interface MessageMappings {

    String QUEUE = "/queue";

    interface Request {
        String REQUEST = "/request";
        String QUEUE_REQUEST = QUEUE + REQUEST;
        String ACCEPT_REQUEST = "/accept-request";
    }

    interface Search {
        String SEARCH = "/search";
        String QUEUE_SEARCH = QUEUE + SEARCH;
    }

    interface Chat {
        String MESSAGE = "/message";
        String QUEUE_MESSAGE = QUEUE + MESSAGE;
        String READ_MESSAGE = "/read-message";
        String QUEUE_READ_MESSAGE = QUEUE + READ_MESSAGE;
    }

}
