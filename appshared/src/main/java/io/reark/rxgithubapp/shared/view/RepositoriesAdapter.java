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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.reark.rxgithubapp.shared.R;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.view.RepositoriesAdapter.RepositoryViewHolder;

import static android.view.View.OnClickListener;

public class RepositoriesAdapter extends Adapter<RepositoryViewHolder> {

    private final List<GitHubRepository> gitHubRepositories = new ArrayList<>(10);

    private OnClickListener onClickListener;

    public RepositoriesAdapter(List<GitHubRepository> gitHubRepositories) {
        this.gitHubRepositories.addAll(gitHubRepositories);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public GitHubRepository getItem(int position) {
        return gitHubRepositories.get(position);
    }

    @Override
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.repository_item, parent, false);
        return new RepositoryViewHolder(v, onClickListener);
    }

    @Override
    public void onBindViewHolder(RepositoryViewHolder holder, int position) {
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

    public static class RepositoryViewHolder extends ViewHolder {

        @NonNull
        public final ImageView avatarImageView;

        @NonNull
        public final TextView titleTextView;

        public RepositoryViewHolder(View view, OnClickListener onClickListener) {
            super(view);
            avatarImageView = (ImageView) view.findViewById(R.id.avatar_image_view);
            titleTextView = (TextView) view.findViewById(R.id.repository_name);
            view.setOnClickListener(onClickListener);
        }
    }
}
