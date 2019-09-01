package com.intouristing.model.enumeration;

/**
 * Created by Marcelo Lacroix on 27/08/19.
 */
public enum RelationshipType {

    FRIENDSHIP(0),
    BLOCK(1);

    private Integer param;

    RelationshipType(Integer param) {
        this.param = param;
    }

    public static Integer get(String key) {
        for (RelationshipType n : RelationshipType.values()) {
            if (key.equals(n.name()))
                return n.param();
        }
        return null;
    }

    public Integer param() {
        return param;
    }

}