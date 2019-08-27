package com.intouristing.model.enumeration;

/**
 * Created by Marcelo Lacroix on 27/08/19.
 */
public enum RequestTypeEnum {

    FRIEND(0);

    private Integer param;

    RequestTypeEnum(Integer param) {
        this.param = param;
    }

    public static Integer get(String key) {
        for (RequestTypeEnum n : RequestTypeEnum.values()) {
            if (key.equals(n.name()))
                return n.param();
        }
        return null;
    }

    public Integer param() {
        return param;
    }
}
