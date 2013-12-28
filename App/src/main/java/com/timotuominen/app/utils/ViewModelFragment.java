package com.timotuominen.app.utils;

import android.support.v4.app.Fragment;

import com.timotuominen.app.utils.SubscriptionAnnotationUtils;

/**
 * Created by tehmou on 12/25/13.
 */
public class ViewModelFragment extends Fragment {
    @Override
    public void onDestroyView() {
        SubscriptionAnnotationUtils.unsubscribeAllAnnotated(this);
        super.onDestroyView();
    }
}
