package ru.ssau.mobile.ssau_mobile_task3.model;

import java.io.Serializable;

/**
 * Created by Pavel on 14.12.2016.
 */

public class Category implements Serializable {
    private String name;
    private long id;

    public Category() {};

    public Category(long catId, String name) {
        id = catId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
