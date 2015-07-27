package com.tehmou.rxbookapp.view;

import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.utils.RxBinderUtil;
import com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel;
import com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.ProgressStatus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.internal.Preconditions;
import rx.android.widget.WidgetObservable;

/**
 * Created by ttuo on 06/01/15.
 */
public class RepositoriesView extends FrameLayout {

    private final RxBinderUtil rxBinderUtil = new RxBinderUtil(this);

    private TextView statusText;

    private Observable<String> searchStringObservable;

    private RepositoriesViewModel viewModel;

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
        searchStringObservable = WidgetObservable.text(editText)
                .map(onTextChangeEvent -> onTextChangeEvent.text().toString());

        statusText = (TextView) findViewById(R.id.repositories_status_text);

        repositoriesAdapter = new RepositoriesAdapter(Collections.emptyList());
        repositoriesAdapter.setOnClickListener(v -> {
            final int itemPosition = repositoriesListView.getChildAdapterPosition(v);
            GitHubRepository repository = repositoriesAdapter.getItem(itemPosition);
            viewModel.selectRepository(repository);
        });

        repositoriesListView = (RecyclerView) findViewById(R.id.repositories_list_view);
        repositoriesListView.setHasFixedSize(true);
        repositoriesListView.setLayoutManager(new LinearLayoutManager(getContext()));
        repositoriesListView.setAdapter(repositoriesAdapter);
    }

    public void setViewModel(@Nullable RepositoriesViewModel viewModel) {
        this.viewModel = viewModel;
        rxBinderUtil.clear();
        if (viewModel != null) {
            rxBinderUtil.bindProperty(viewModel.getRepositories(), this::setRepositories);
            rxBinderUtil.bindProperty(
                    viewModel.getNetworkRequestStatusText(), this::setNetworkRequestStatus);
            viewModel.setSearchStringObservable(searchStringObservable);
        }
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
}
