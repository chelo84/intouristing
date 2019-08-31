package com.intouristing.websocket.messagemapping;

/**
 * Created by Marcelo Lacroix on 27/08/2019.
 */
public interface RequestMessageMapping {

    String REQUEST = "/request";
    String QUEUE_REQUEST = "/queue" + REQUEST;
    String ACCEPT_REQUEST = "/accept-request";

}
