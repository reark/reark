package com.tehmou.rxbookapp.view;

import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.utils.RxBinderUtil;
import com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel;
import com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel.ProgressStatus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import rx.Observable;
import rx.android.internal.Preconditions;
import rx.android.widget.WidgetObservable;

/**
 * Created by ttuo on 06/01/15.
 */
public class RepositoriesView extends FrameLayout {

    private final RxBinderUtil rxBinderUtil = new RxBinderUtil(this);

    private ArrayAdapter<GitHubRepository> listAdapter;
    private TextView statusText;

    private Observable<String> searchStringObservable;

    private RepositoriesViewModel viewModel;

    public RepositoriesView(Context context) {
        super(context, null);
    }

    public RepositoriesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ListView listView = (ListView) findViewById(R.id.repositories_list_view);
        listAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (viewModel != null) {
                GitHubRepository repository = listAdapter.getItem(position);
                viewModel.selectRepository(repository);
            }
        });
        listView.setAdapter(listAdapter);

        EditText editText = (EditText) findViewById(R.id.repositories_search);
        searchStringObservable = WidgetObservable.text(editText)
                .map(onTextChangeEvent -> onTextChangeEvent.text().toString());

        statusText = (TextView) findViewById(R.id.repositories_status_text);
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
        Preconditions.checkState(listAdapter != null, "List Adapter should not be null.");

        listAdapter.clear();
        listAdapter.addAll(repositories);
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
