package io.reark.rxbookapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reark.rxbookapp.R;
import io.reark.rxbookapp.RxBookApp;
import io.reark.rxbookapp.activities.ChooseRepositoryActivity;
import io.reark.rxbookapp.utils.ApplicationInstrumentation;
import io.reark.rxbookapp.view.RepositoriesView;
import io.reark.rxbookapp.viewmodels.RepositoriesViewModel;

import javax.inject.Inject;

/**
 * Created by ttuo on 19/03/14.
 */
public class RepositoriesFragment extends Fragment {
    private RepositoriesView.ViewBinder repositoriesViewBinder;

    @Inject
    RepositoriesViewModel repositoriesViewModel;

    @Inject
    ApplicationInstrumentation mInstrumentation;

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
        repositoriesViewBinder = new RepositoriesView.ViewBinder(
                (RepositoriesView) view.findViewById(R.id.repositories_view),
                repositoriesViewModel);
        repositoriesViewModel.subscribeToDataStore();
    }

    @Override
    public void onResume() {
        super.onResume();
        repositoriesViewBinder.bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        repositoriesViewBinder.unbind();
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
        mInstrumentation.getLeakTracing().traceLeakage(this);
    }
}
