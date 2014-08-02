package com.tehmou.rxbookapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tehmou.rxbookapp.utils.RxBinderUtil;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ttuo on 02/08/14.
 */
public class RxTextView extends TextView {
    final private RxBinderUtil<RxTextView> binder =
            new RxBinderUtil<RxTextView>("RxTextView");

    public RxTextView(Context context) {
        super(context);
    }

    public RxTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RxTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    final public Func1<String, RxTextView> setText =
            new Func1<String, RxTextView>() {
                @Override
                public RxTextView call(String charSequence) {
                    setText(charSequence);
                    return RxTextView.this;
                }
            };

    public void bindTo(Observable<String> observable) {
        binder.bindProperty(observable, setText, "setText");
    }
}
