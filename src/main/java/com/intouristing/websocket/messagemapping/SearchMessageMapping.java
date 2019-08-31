package com.intouristing.websocket.messagemapping;

/**
 * Created by Marcelo Lacroix on 18/08/2019.
 */
public interface SearchMessageMapping {

    String SEARCH = "/search";
    String QUEUE_SEARCH = "/queue" + SEARCH;

    String SEND_MESSAGE = "/accept-message";

}
