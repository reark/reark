package com.tehmou.rxbookapp.view;

import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.utils.RxBinderUtil;
import com.tehmou.rxbookapp.utils.TextWatcherObservable;
import com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.List;

import rx.Observable;

/**
 * Created by ttuo on 06/01/15.
 */
public class RepositoriesView extends FrameLayout {
    private static final String TAG = RepositoriesView.class.getSimpleName();
    private final RxBinderUtil rxBinderUtil = new RxBinderUtil(this);

    private ArrayAdapter<GitHubRepository> listAdapter;
    private ListView listView;
    private EditText editText;
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

        listView = (ListView) findViewById(R.id.repositories_list_view);
        listAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (viewModel != null) {
                GitHubRepository repository = listAdapter.getItem(position);
                viewModel.selectRepository(repository);
            }
        });
        listView.setAdapter(listAdapter);

        this.editText = (EditText) findViewById(R.id.repositories_search);
        searchStringObservable = TextWatcherObservable.create(editText);
    }

    public void setViewModel(RepositoriesViewModel viewModel) {
        this.viewModel = viewModel;
        rxBinderUtil.clear();
        if (viewModel != null) {
            rxBinderUtil.bindProperty(viewModel.getRepositories(), this::setRepositories);
            viewModel.setSearchStringObservable(searchStringObservable);
        }
    }

    private void setRepositories(List<GitHubRepository> repositories) {
        listAdapter.clear();
        listAdapter.addAll(repositories);
    }
}
