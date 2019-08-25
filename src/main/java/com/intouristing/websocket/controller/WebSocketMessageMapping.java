package com.intouristing.websocket.controller;

/**
 * Created by Marcelo Lacroix on 18/08/2019.
 */
public interface WebSocketMessageMapping {

    String SEARCH = "/search";
    String QUEUE_SEARCH = "/queue" + SEARCH;
    String SEND_MESSAGE = "/send-message";

}
