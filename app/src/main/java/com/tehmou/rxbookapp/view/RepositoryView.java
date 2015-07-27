package com.tehmou.rxbookapp.view;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.pojo.GitHubRepository;
import com.tehmou.rxbookapp.utils.RxBinderUtil;
import com.tehmou.rxbookapp.viewmodels.RepositoryViewModel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import rx.android.internal.Preconditions;

/**
 * Created by ttuo on 06/04/15.
 */
public class RepositoryView extends FrameLayout {
    private final RxBinderUtil rxBinderUtil = new RxBinderUtil(this);

    private TextView titleTextView;
    private TextView stargazersTextView;
    private TextView forksTextView;
    private ImageView avatarImageView;

    @Nullable
    private Target<GlideDrawable> request;

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

    public void setViewModel(@Nullable RepositoryViewModel viewModel) {
        rxBinderUtil.clear();
        if (viewModel != null) {
            rxBinderUtil.bindProperty(viewModel.getRepository(), this::setRepository);
        }
    }

    private void setRepository(@NonNull GitHubRepository repository) {
        Preconditions.checkNotNull(repository, "Repository cannot be null.");

        titleTextView.setText(repository.getName());
        stargazersTextView.setText("stars: " + repository.getStargazersCount());
        forksTextView.setText("forks: " + repository.getForksCount());
        request = Glide.with(getContext())
                .load(repository.getOwner().getAvatarUrl())
                .fitCenter()
                .crossFade()
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(avatarImageView);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (request != null) {
            request.onDestroy();
        }
    }
}
