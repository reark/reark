package com.tehmou.rxbookapp.fragments;

import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.RxBookApp;
import com.tehmou.rxbookapp.activities.ChooseRepositoryActivity;
import com.tehmou.rxbookapp.utils.Instrumentation;
import com.tehmou.rxbookapp.view.RepositoriesView;
import com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

/**
 * Created by ttuo on 19/03/14.
 */
public class RepositoriesFragment extends Fragment {

    private RepositoriesView repositoriesView;

    @Inject
    RepositoriesViewModel repositoriesViewModel;

    @Inject
    Instrumentation instrumentation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBookApp.getInstance().getGraph().inject(this);

        repositoriesViewModel.getSelectRepository()
                .subscribe(repository ->
                        ((ChooseRepositoryActivity) getActivity())
                                .chooseRepository(repository.getId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.repositories_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repositoriesView = (RepositoriesView) view.findViewById(R.id.repositories_view);
        repositoriesViewModel.subscribeToDataStore();
    }

    @Override
    public void onResume() {
        super.onResume();
        repositoriesView.setViewModel(repositoriesViewModel);
    }

    @Override
    public void onPause() {
        super.onPause();
        repositoriesView.setViewModel(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        repositoriesViewModel.unsubscribeFromDataStore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        repositoriesViewModel.dispose();
        repositoriesViewModel = null;
        instrumentation.getLeakTracing().traceLeakage(this);
    }
}
