package com.tehmou.rxbookapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.utils.RxBinderUtil;
import com.tehmou.rxbookapp.viewmodels.BookViewModel;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by ttuo on 02/08/14.
 */
public class BookInfoView extends LinearLayout {
    final private RxBinderUtil rxBinderUtil = new RxBinderUtil(this);

    private TextView bookNameTextView;
    private TextView bookAuthorTextView;
    private TextView bookPriceTextView;

    public BookInfoView(Context context) {
        super(context);
    }

    public BookInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        bookNameTextView = (TextView) findViewById(R.id.book_name);
        bookAuthorTextView = (TextView) findViewById(R.id.book_author);
        bookPriceTextView = (TextView) findViewById(R.id.book_price);
    }

    public void setViewModel(BookViewModel viewModel) {
        rxBinderUtil.clear();
        if (viewModel != null) {
            rxBinderUtil.bindProperty(viewModel.getBookName(), setBookName);
            rxBinderUtil.bindProperty(viewModel.getBookPrice(), setBookPrice);
            rxBinderUtil.bindProperty(viewModel.getAuthorName(), setAuthorName);
        }
    }

    final private Action1<String> setBookName = new Action1<String>() {
        @Override
        public void call(String s) {
            bookNameTextView.setText(s);
        }
    };

    final private Action1<String> setBookPrice = new Action1<String>() {
        @Override
        public void call(String s) {
            bookPriceTextView.setText(s);
        }
    };

    final private Action1<String> setAuthorName = new Action1<String>() {
        @Override
        public void call(String s) {
            bookAuthorTextView.setText(s);
        }
    };
}
