package com.tehmou.rxbookapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.view.RepositoryView;
import com.tehmou.rxbookapp.viewmodels.RepositoryViewModel;

/**
 * Created by ttuo on 06/04/15.
 */
public class RepositoryFragment extends Fragment {
    private RepositoryViewModel viewModel;
    private RepositoryView repositoryView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new RepositoryViewModel();
        viewModel.setRepositoryId(15491874);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.repository_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repositoryView = (RepositoryView) view.findViewById(R.id.repository_view);
        viewModel.subscribeToDataStore();
    }

    @Override
    public void onResume() {
        super.onResume();
        repositoryView.setViewModel(viewModel);
    }

    @Override
    public void onPause() {
        super.onPause();
        repositoryView.setViewModel(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.unsubscribeFromDataStore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.dispose();
        viewModel = null;
    }
}
