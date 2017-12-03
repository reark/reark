/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.shared.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reark.reark.utils.RxViewBinder;
import io.reark.rxgithubapp.shared.R;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.viewmodels.RepositoriesViewModel;
import io.reark.rxgithubapp.shared.viewmodels.RepositoriesViewModel.ProgressStatus;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

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
        searchStringObservable = RxTextView.textChanges(editText).map(CharSequence::toString);

        statusText = (TextView) findViewById(R.id.repositories_status_text);

        repositoriesAdapter = new RepositoriesAdapter(Collections.emptyList());

        repositoriesListView = (RecyclerView) findViewById(R.id.repositories_list_view);
        repositoriesListView.setHasFixedSize(true);
        repositoriesListView.setLayoutManager(new LinearLayoutManager(getContext()));
        repositoriesListView.setAdapter(repositoriesAdapter);
    }

    private void setRepositories(@NonNull final List<GitHubRepository> repositories) {
        checkNotNull(repositories);
        checkNotNull(repositoriesAdapter);

        repositoriesAdapter.set(repositories);
    }

    private void setNetworkRequestStatus(@NonNull final ProgressStatus networkRequestStatus) {
        checkNotNull(networkRequestStatus);

        setNetworkRequestStatusText(getLoadingStatusString(networkRequestStatus));
    }

    @NonNull
    private static String getLoadingStatusString(ProgressStatus networkRequestStatus) {
        switch (networkRequestStatus) {
            case LOADING:
                return "Loading..";
            case ERROR:
                return "Error occurred";
            case IDLE:
            default:
                return "";
        }
    }

    private void setNetworkRequestStatusText(@NonNull final String networkRequestStatusText) {
        checkNotNull(networkRequestStatusText);
        checkNotNull(statusText);

        statusText.setText(networkRequestStatusText);
    }

    public static class ViewBinder extends RxViewBinder {
        private final RepositoriesView view;
        private final RepositoriesViewModel viewModel;

        public ViewBinder(@NonNull final RepositoriesView view,
                          @NonNull final RepositoriesViewModel viewModel) {
            this.view = get(view);
            this.viewModel = get(viewModel);
        }

        @Override
        protected void bindInternal(@NonNull final CompositeDisposable s) {
            s.add(viewModel.getRepositories()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setRepositories));
            s.add(viewModel.getNetworkRequestStatusText()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setNetworkRequestStatus));
            s.add(view.searchStringObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(viewModel::setSearchString));
            s.add(Completable.fromAction(() -> view.repositoriesAdapter.setOnClickListener(this::repositoriesAdapterOnClick))
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
