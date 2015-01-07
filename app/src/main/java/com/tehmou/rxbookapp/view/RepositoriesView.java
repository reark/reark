package com.tehmou.rxbookapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.utils.RxBinderUtil;
import com.tehmou.rxbookapp.viewmodels.RepositoriesViewModel;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by ttuo on 06/01/15.
 */
public class RepositoriesView extends FrameLayout {
    final private RxBinderUtil rxBinderUtil = new RxBinderUtil(this);

    private ArrayAdapter listAdapter;
    private ListView listView;

    public RepositoriesView(Context context) {
        super(context, null);
    }

    public RepositoriesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.listView = (ListView) findViewById(R.id.repositories_list_view);
        listAdapter = new ArrayAdapter<GitHubRepository>(getContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);
    }

    public void setViewModel(RepositoriesViewModel viewModel) {
        rxBinderUtil.clear();
        if (viewModel != null) {
            rxBinderUtil.bindProperty(viewModel.getRepositories(), setRepositories);
        }
    }

    final private Action1<List<GitHubRepository>> setRepositories = new Action1<List<GitHubRepository>>() {
        @Override
        public void call(List<GitHubRepository> repositories) {
            listAdapter.clear();
            listAdapter.addAll(repositories);
        }
    };
}
