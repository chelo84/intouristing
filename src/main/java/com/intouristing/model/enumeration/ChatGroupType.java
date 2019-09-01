package com.intouristing.model.enumeration;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
public enum ChatGroupType {

    PRIVATE(0),
    GROUP_PRIVATE(1),
    GROUP_PUBLIC(2);

    private Integer param;

    ChatGroupType(Integer param) {
        this.param = param;
    }

    public static Integer get(String key) {
        for (ChatGroupType n : ChatGroupType.values()) {
            if (key.equals(n.name()))
                return n.param();
        }
        return null;
    }

    public Integer param() {
        return param;
    }
}
