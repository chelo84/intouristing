package com.intouristing.intouristing.controller.websocket;

/**
 * Created by Marcelo Lacroix on 18/08/2019.
 */
public interface WebSocketMessageMapping {

    public static final String SEARCH = "/search";
    public static final String QUEUE_SEARCH = "/queue" + SEARCH;
    public static final String SEARCH_CANCEL = "/search/cancel";

}
