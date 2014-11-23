package com.tehmou.rxbookapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tehmou.rxbookapp.data.DataStore;
import com.tehmou.rxbookapp.viewmodels.BookViewModel;
import com.tehmou.rxbookapp.views.BookInfoView;

/**
 * Created by ttuo on 19/03/14.
 */
public class BookFragment extends Fragment {
    private BookViewModel bookViewModel;

    private BookInfoView bookInfoView;

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
        bookInfoView = (BookInfoView) view.findViewById(R.id.book_info_view);
        bookViewModel.subscribeToDataStore();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bookViewModel.unsubscribeFromDataStore();
    }

    @Override
    public void onResume() {
        super.onResume();
        bookInfoView.setViewModel(bookViewModel);
    }

    @Override
    public void onPause() {
        super.onPause();
        bookInfoView.setViewModel(null);
    }
}
