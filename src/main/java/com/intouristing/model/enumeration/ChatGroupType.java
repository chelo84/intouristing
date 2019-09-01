package com.intouristing.model.enumeration;

/**
 * Created by Marcelo Lacroix on 31/08/2019.
 */
public enum ChatGroupType {

    GROUP_PRIVATE(0),
    GROUP_PUBLIC(1);

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
