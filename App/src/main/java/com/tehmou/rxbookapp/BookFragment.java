package com.tehmou.rxbookapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tehmou.rxbookapp.data.DataStore;
import com.tehmou.rxbookapp.viewmodels.BookViewModel;

import rx.android.schedulers.AndroidSchedulers;
import rx.util.functions.Action1;

/**
 * Created by ttuo on 19/03/14.
 */
public class BookFragment extends Fragment {

    private BookViewModel bookViewModel;

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

        bookViewModel.subscribeToDataStore();

        final TextView bookNameTextView = (TextView) view.findViewById(R.id.book_name);
        bookViewModel.getBookName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        bookNameTextView.setText(s);
                    }
                });

        final TextView bookAuthorTextView = (TextView) view.findViewById(R.id.book_author);
        bookViewModel.getAuthorName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                bookAuthorTextView.setText(s);
            }
        });

        final TextView bookPriceTextView = (TextView) view.findViewById(R.id.book_price);
        bookViewModel.getBookPrice()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                bookPriceTextView.setText(s);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bookViewModel.unsubscribeFromDataStore();
    }
}
