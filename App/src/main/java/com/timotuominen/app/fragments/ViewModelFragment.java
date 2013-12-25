package com.timotuominen.app.fragments;

import com.timotuominen.app.utils.SubscriptionAnnotationUtils;

import roboguice.fragment.RoboFragment;

/**
 * Created by ttuo on 12/25/13.
 */
public class ViewModelFragment extends RoboFragment {
    @Override
    public void onDestroyView() {
        SubscriptionAnnotationUtils.unsubscribeAllAnnotated(this);
        super.onDestroyView();
    }
}
