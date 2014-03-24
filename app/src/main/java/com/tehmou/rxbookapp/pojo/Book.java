package com.tehmou.rxbookapp.pojo;

/**
 * Created by ttuo on 19/03/14.
 */
public class Book {
    final public String id;
    final public String name;
    final public String authorId;

    public Book(String id) {
        this.id = id;
        this.name = "How Rx will Change the World";
        this.authorId = "363167";
    }
}
