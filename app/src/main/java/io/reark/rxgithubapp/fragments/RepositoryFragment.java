package io.reark.rxgithubapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import io.reark.rxgithubapp.R;
import io.reark.rxgithubapp.RxGitHubApp;
import io.reark.rxgithubapp.activities.MainActivity;
import io.reark.rxgithubapp.utils.ApplicationInstrumentation;
import io.reark.rxgithubapp.view.RepositoryView;
import io.reark.rxgithubapp.viewmodels.RepositoryViewModel;

/**
 * Created by ttuo on 06/04/15.
 */
public class RepositoryFragment extends Fragment {
    private RepositoryView.ViewBinder repositoryViewBinder;

    @Inject
    RepositoryViewModel viewModel;

    @Inject
    ApplicationInstrumentation mInstrumentation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RxGitHubApp.getInstance().getGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.repository_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repositoryViewBinder = new RepositoryView.ViewBinder(
                (RepositoryView) view.findViewById(R.id.repository_view),
                viewModel);
        viewModel.subscribeToDataStore();

        view.findViewById(R.id.repository_fragment_choose_repository_button)
                .setOnClickListener(e ->
                        ((MainActivity) getActivity()).chooseRepository());
    }

    @Override
    public void onResume() {
        super.onResume();
        repositoryViewBinder.bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        repositoryViewBinder.unbind();
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
        mInstrumentation.getLeakTracing().traceLeakage(this);
    }
}
