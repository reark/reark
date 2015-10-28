package com.tehmou.rxbookapp.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tehmou.rxbookapp.R;
import com.tehmou.rxbookapp.pojo.GitHubRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pawel Polanski on 7/27/15.
 */
public class RepositoriesAdapter extends RecyclerView.Adapter<RepositoriesAdapter.ViewHolder> {

    private final List<GitHubRepository> gitHubRepositories = new ArrayList<>();

    private View.OnClickListener onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarImageView;

        public TextView titleTextView;

        public ViewHolder(View view, View.OnClickListener onClickListener) {
            super(view);
            avatarImageView = (ImageView) view.findViewById(R.id.avatar_image_view);
            titleTextView = (TextView) view.findViewById(R.id.repository_name);
            view.setOnClickListener(onClickListener);
        }

    }
    public RepositoriesAdapter(List<GitHubRepository> gitHubRepositories) {
        this.gitHubRepositories.addAll(gitHubRepositories);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public GitHubRepository getItem(int position) {
        return gitHubRepositories.get(position);
    }

    @Override
    public RepositoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.repository_item, parent, false);
        return new ViewHolder(v, onClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTextView.setText(gitHubRepositories.get(position).getName());
        Glide.with(holder.avatarImageView.getContext())
                .load(gitHubRepositories.get(position).getOwner().getAvatarUrl())
                .fitCenter()
                .into(holder.avatarImageView);
    }

    @Override
    public int getItemCount() {
        return gitHubRepositories.size();
    }

    public void set(List<GitHubRepository> gitHubRepositories) {
        this.gitHubRepositories.clear();
        this.gitHubRepositories.addAll(gitHubRepositories);

        notifyDataSetChanged();
    }

}
