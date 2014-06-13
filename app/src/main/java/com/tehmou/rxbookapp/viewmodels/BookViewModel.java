package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.data.DataStore;
import com.tehmou.rxbookapp.pojo.Author;
import com.tehmou.rxbookapp.pojo.Book;
import com.tehmou.rxbookapp.utils.SubscriptionManager;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 19/03/14.
 */
public class BookViewModel {
    final private SubscriptionManager subscriptionManager = new SubscriptionManager();

    final private String bookId;
    final private DataStore dataStore;

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
        subscriptionManager.add(createBookNameObservable().subscribe(bookName));
        subscriptionManager.add(createAuthorNameObservable().subscribe(authorName));
        subscriptionManager.add(createBookPriceObservable().subscribe(bookPrice));
    }

    public void unsubscribeFromDataStore() {
        subscriptionManager.unsubscribeAll();
    }

    private Observable<String> createBookNameObservable() {
        return dataStore.getBook(bookId)
                .map(new Func1<Book, String>() {
                    @Override
                    public String call(Book book) {
                        return book.name;
                    }
                });
    }

    private Observable<String> createAuthorNameObservable() {
        return dataStore.getBook(bookId)
                .flatMap(new Func1<Book, Observable<Author>>() {
                    @Override
                    public Observable<Author> call(Book book) {
                        return dataStore.getAuthor(book.authorId);
                    }
                })
                .map(new Func1<Author, String>() {
                    @Override
                    public String call(Author author) {
                        return author.name;
                    }
                });
    }

    private Observable<String> createBookPriceObservable() {
        return dataStore.getBookPrice(bookId)
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "Price: " + integer + " EUR";
                    }
                });
    }
}
