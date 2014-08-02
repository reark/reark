package com.tehmou.rxbookapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tehmou.rxbookapp.data.DataStore;
import com.tehmou.rxbookapp.utils.SubscriptionUtils;
import com.tehmou.rxbookapp.viewmodels.BookViewModel;
import com.tehmou.rxbookapp.views.RxTextView;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 19/03/14.
 */
public class BookFragment extends Fragment {
    private BookViewModel bookViewModel;

    private RxTextView bookNameTextView;
    private RxTextView bookAuthorTextView;
    private RxTextView bookPriceTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookViewModel = new BookViewModel(DataStore.getInstance(), "436346");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookNameTextView = (RxTextView) getView().findViewById(R.id.book_name);
        bookAuthorTextView = (RxTextView) getView().findViewById(R.id.book_author);
        bookPriceTextView = (RxTextView) getView().findViewById(R.id.book_price);
    }

    @Override
    public void onResume() {
        super.onResume();
        bookViewModel.subscribeToDataStore();

        bookNameTextView.bindTo(bookViewModel.getBookName());
        bookAuthorTextView.bindTo(bookViewModel.getAuthorName());
        bookPriceTextView.bindTo(bookViewModel.getBookPrice());
    }

    @Override
    public void onPause() {
        super.onPause();
        bookViewModel.unsubscribeFromDataStore();
    }
}
