package com.tehmou.rxbookapp.data;

import com.tehmou.rxbookapp.pojo.Author;
import com.tehmou.rxbookapp.pojo.Book;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ttuo on 19/03/14.
 */
public class DataStore {
    static private DataStore instance;

    static public DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public DataStore() { }

    public Observable<Book> getBook(String bookId) {
        return Observable.just(new Book(bookId)).delay(700, TimeUnit.MILLISECONDS);
    }

    public Observable<Author> getAuthor(String authorId) {
        return Observable.just(new Author(authorId)).delay(5, TimeUnit.SECONDS);
    }

    public Observable<Integer> getBookPrice(String bookId) {
        return Observable.interval(1, TimeUnit.SECONDS).map(new Func1<Long, Integer>() {
            @Override
            public Integer call(Long aLong) {
                return Math.round(100 + aLong * aLong);
            }
        });
    }
}
