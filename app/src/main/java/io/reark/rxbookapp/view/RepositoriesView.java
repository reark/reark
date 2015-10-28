package io.reark.rxbookapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import io.reark.rxbookapp.R;
import io.reark.rxbookapp.pojo.GitHubRepository;
import io.reark.rxbookapp.utils.TextWatcherObservable;
import io.reark.rxbookapp.viewmodels.RepositoriesViewModel;
import io.reark.rxbookapp.viewmodels.RepositoriesViewModel.ProgressStatus;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by ttuo on 06/01/15.
 */
public class RepositoriesView extends FrameLayout {

    private TextView statusText;

    private Observable<String> searchStringObservable;

    private RecyclerView repositoriesListView;

    private RepositoriesAdapter repositoriesAdapter;

    public RepositoriesView(Context context) {
        super(context, null);
    }

    public RepositoriesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        EditText editText = (EditText) findViewById(R.id.repositories_search);
        searchStringObservable = TextWatcherObservable.create(editText);

        statusText = (TextView) findViewById(R.id.repositories_status_text);

        repositoriesAdapter = new RepositoriesAdapter(Collections.emptyList());

        repositoriesListView = (RecyclerView) findViewById(R.id.repositories_list_view);
        repositoriesListView.setHasFixedSize(true);
        repositoriesListView.setLayoutManager(new LinearLayoutManager(getContext()));
        repositoriesListView.setAdapter(repositoriesAdapter);
    }

    private void setRepositories(@NonNull List<GitHubRepository> repositories) {
        Preconditions.checkNotNull(repositories, "Repository List Text cannot be null.");
        Preconditions.checkState(repositoriesAdapter != null, "List Adapter should not be null.");

        repositoriesAdapter.set(repositories);
    }

    private void setNetworkRequestStatus(@NonNull ProgressStatus networkRequestStatus) {
        Preconditions.checkNotNull(networkRequestStatus, "Network Request Status cannot be null.");

        String networkStatusText = "";
        switch (networkRequestStatus) {

            case LOADING:
                networkStatusText = "Loading..";
                break;
            case ERROR:
                networkStatusText = "Error occurred";
                break;
            case IDLE:
                networkStatusText = "";
                break;
        }
        setNetworkRequestStatusText(networkStatusText);
    }

    private void setNetworkRequestStatusText(@NonNull String networkRequestStatusText) {
        Preconditions.checkNotNull(networkRequestStatusText, "Network Status Text cannot be null.");
        Preconditions.checkState(statusText != null, "Status Text View should not be null.");

        statusText.setText(networkRequestStatusText);
    }


    public static class ViewBinder extends RxViewBinder {
        private RepositoriesView view;
        private RepositoriesViewModel viewModel;

        public ViewBinder(@NonNull final RepositoriesView view,
                          @NonNull final RepositoriesViewModel viewModel) {
            Preconditions.checkNotNull(view, "View cannot be null.");
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.view = view;
            this.viewModel = viewModel;
        }

        @Override
        protected void bindInternal(@NonNull final CompositeSubscription s) {
            s.add(viewModel.getRepositories()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setRepositories));
            s.add(viewModel.getNetworkRequestStatusText()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setNetworkRequestStatus));
            s.add(view.searchStringObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(viewModel::setSearchString));
            s.add(Observable.create(
                    subscriber -> {
                        view.repositoriesAdapter.setOnClickListener(
                                this::repositoriesAdapterOnClick);
                        subscriber.add(Subscriptions.create(() ->
                                view.repositoriesAdapter.setOnClickListener(null)));
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }

        private void repositoriesAdapterOnClick(View clickedView) {
            final int itemPosition = view.repositoriesListView.getChildAdapterPosition(clickedView);
            GitHubRepository repository = view.repositoriesAdapter.getItem(itemPosition);
            viewModel.selectRepository(repository);
        }
    }
}
