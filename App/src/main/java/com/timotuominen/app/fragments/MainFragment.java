package com.timotuominen.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timotuominen.app.R;
import com.timotuominen.app.utils.Unsubscribable;
import com.timotuominen.app.utils.ViewModelFragment;
import com.timotuominen.app.viewmodels.SampleViewModel;

import rx.util.functions.Action1;

/**
 * Created by tehmou on 12/28/13.
 */
public class MainFragment extends ViewModelFragment {

    @Unsubscribable
    private SampleViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new SampleViewModel();
        viewModel.getName().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                TextView textView = (TextView)view.findViewById(R.id.textView);
                textView.setText(s);
            }
        });
    }
}
