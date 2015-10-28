package io.reark.rxbookapp.pojo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import io.reark.reark.utils.Preconditions;

/**
 * Created by Pawel Polanski on 7/27/15.
 */
public class GitHubOwner {

    @Nullable
    @SerializedName("avatar_url")
    final private String avatarUrl;

    @SuppressWarnings("NullableProblems")
    public GitHubOwner(@NonNull String avatarUrl) {
        Preconditions.checkNotNull(avatarUrl, "Avatar cannot be null");

        this.avatarUrl = avatarUrl;
    }

    public GitHubOwner() {
        this("");
    }

    @NonNull
    public String getAvatarUrl() {
        return avatarUrl == null ? "" : avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GitHubOwner)) {
            return false;
        }

        GitHubOwner that = (GitHubOwner) o;

        return !(avatarUrl != null ? !avatarUrl.equals(that.avatarUrl) : that.avatarUrl != null);

    }

    @Override
    public int hashCode() {
        return avatarUrl != null ? avatarUrl.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GitHubOwner{");
        sb.append("avatarUrl='").append(avatarUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
