package io.reark.rxgithubapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;

import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import io.reark.rxgithubapp.R;
import io.reark.rxgithubapp.pojo.GitHubRepository;
import io.reark.rxgithubapp.utils.glide.SerialTarget;
import io.reark.rxgithubapp.viewmodels.RepositoryViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ttuo on 06/04/15.
 */
public class RepositoryView extends FrameLayout {
    private TextView titleTextView;
    private TextView stargazersTextView;
    private TextView forksTextView;
    private ImageView avatarImageView;

    @NonNull
    private final SerialTarget<GlideDrawable> request = new SerialTarget<>();

    public RepositoryView(Context context) {
        super(context);
    }

    public RepositoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleTextView = (TextView) findViewById(R.id.widget_layout_title);
        stargazersTextView = (TextView) findViewById(R.id.widget_layout_stargazers);
        forksTextView = (TextView) findViewById(R.id.widget_layout_forks);
        avatarImageView = (ImageView) findViewById(R.id.widget_avatar_image_view);
    }

    private void setRepository(@NonNull GitHubRepository repository) {
        Preconditions.checkNotNull(repository, "Repository cannot be null.");

        titleTextView.setText(repository.getName());
        stargazersTextView.setText("stars: " + repository.getStargazersCount());
        forksTextView.setText("forks: " + repository.getForksCount());
        request.set(Glide.with(getContext())
                .load(repository.getOwner().getAvatarUrl())
                .fitCenter()
                .crossFade()
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(avatarImageView));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        request.onDestroy();
    }

    public static class ViewBinder extends RxViewBinder {
        RepositoryView view;
        RepositoryViewModel viewModel;

        public ViewBinder(@NonNull final RepositoryView view,
                          @NonNull final RepositoryViewModel viewModel) {
            Preconditions.checkNotNull(view, "View cannot be null.");
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.view = view;
            this.viewModel = viewModel;
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription s) {
            s.add(viewModel.getRepository()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setRepository));
        }
    }
}
