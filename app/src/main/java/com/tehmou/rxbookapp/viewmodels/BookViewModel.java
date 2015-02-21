package com.tehmou.rxbookapp.viewmodels;

import com.tehmou.rxbookapp.RxBookApp;
import com.tehmou.rxbookapp.data.DataStore;
import com.tehmou.rxbookapp.pojo.Author;
import com.tehmou.rxbookapp.pojo.Book;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 19/03/14.
 */
public class BookViewModel {
    final private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    DataStore dataStore;

    final private String bookId;

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

    public BookViewModel(final String bookId) {
        RxBookApp.getInstance().getGraph().inject(this);
        this.bookId = bookId;
    }

    public void subscribeToDataStore() {
        compositeSubscription.add(createBookNameObservable().subscribe(bookName));
        compositeSubscription.add(createAuthorNameObservable().subscribe(authorName));
        compositeSubscription.add(createBookPriceObservable().subscribe(bookPrice));
    }

    public void unsubscribeFromDataStore() {
        compositeSubscription.clear();
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
