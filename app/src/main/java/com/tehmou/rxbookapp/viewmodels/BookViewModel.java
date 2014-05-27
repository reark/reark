package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.data.DataStore;
import com.tehmou.rxbookapp.pojo.Author;
import com.tehmou.rxbookapp.pojo.Book;
import com.tehmou.rxbookapp.utils.SubscriptionManager;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

/**
 * Created by ttuo on 19/03/14.
 */
public class BookViewModel {
    final private String bookId;
    final private DataStore dataStore;
    final private SubscriptionManager subscriptionManager = new SubscriptionManager();

    final private Subject<String, String> bookName = BehaviorSubject.create("Loading name..");
    final private Subject<String, String> bookPrice = BehaviorSubject.create("Loading book price..");
    final private Subject<String, String> authorName = BehaviorSubject.create("Loading author name..");

    public Observable<String> getBookName() {
        return bookName;
    }
    public Observable<String> getBookPrice() {
        return bookPrice;
    }
    public Observable<String> getAuthorName() {
        return authorName;
    }

    public BookViewModel(final DataStore dataStore, final String bookId) {
        this.bookId = bookId;
        this.dataStore = dataStore;
    }

    public void subscribeToDataStore() {
        dataStore.getBook(bookId)
                .flatMap(new Func1<Book, Observable<Author>>() {
                    @Override
                    public Observable<Author> call(Book book) {
                        bookName.onNext(book.name);
                        return dataStore.getAuthor(book.authorId);
                    }
                })
                .subscribe(new Action1<Author>() {
                    @Override
                    public void call(Author author) {
                        authorName.onNext(author.name);
                    }
                });
        dataStore.getBookPrice(bookId)
                .map(new rx.functions.Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "Price: " + integer + " EUR";
                    }
                })
                .subscribe(bookPrice);
    }

    public void unsubscribeFromDataStore() {
        subscriptionManager.unsubscribeAll();
    }

}
