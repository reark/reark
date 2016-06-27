package io.reark.rxgithubapp.shared.data;

import android.support.annotation.NonNull;

import io.reark.reark.data.DataStreamNotification;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;
import io.reark.rxgithubapp.shared.pojo.UserSettings;
import rx.Observable;

/**
 * Created by ttuo on 27/06/16.
 */
public class DataFunctions {
    public interface GetUserSettings {
        @NonNull
        Observable<UserSettings> call();
    }

    public interface SetUserSettings {
        void call(@NonNull UserSettings userSettings);
    }

    public interface GetGitHubRepository {
        @NonNull
        Observable<GitHubRepository> call(int repositoryId);
    }

    public interface FetchAndGetGitHubRepository extends GetGitHubRepository {

    }

    public interface GetGitHubRepositorySearch {
        @NonNull
        Observable<DataStreamNotification<GitHubRepositorySearch>> call(@NonNull String search);
    }
}
