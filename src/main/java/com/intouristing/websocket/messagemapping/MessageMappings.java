package com.intouristing.websocket.messagemapping;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
public interface MessageMappings {

    interface Request {
        String REQUEST = "/request";
        String QUEUE_REQUEST = "/queue" + REQUEST;
        String ACCEPT_REQUEST = "/accept-request";
    }

    interface Search {
        String SEARCH = "/search";
        String QUEUE_SEARCH = "/queue" + SEARCH;
    }

    interface Chat {
        String MESSAGE = "/message";
        String QUEUE_MESSAGE = "/queue" + MESSAGE;
        String READ_MESSAGE = "/read-message";
    }

}
