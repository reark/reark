package com.timotuominen.app.viewmodels;

import com.timotuominen.app.data.DataLayer;
import com.timotuominen.app.utils.SubscriptionManager;
import com.timotuominen.app.utils.ViewModel;
import com.timotuominen.app.utils.Unsubscribable;
import com.timotuominen.app.utils.ViewModelValue;
import com.timotuominen.app.utils.ViewModelValueWritable;

import rx.util.functions.Action1;

/**
 * Created by tehmou on 12/25/13.
 */
public class SampleViewModel extends ViewModel {
    final private SubscriptionManager subscriptionManager = new SubscriptionManager();

    @Unsubscribable
    final protected ViewModelValueWritable<String> name = new ViewModelValueWritable<String>();

    public SampleViewModel() {
        name.setValue("Initial Value");

        subscriptionManager.add(
                DataLayer.getInstance().getIntervalNumberStream()
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                name.setValue("Counting: " + aLong);
                            }
                        }));
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        subscriptionManager.unsubscribe();
    }

    public ViewModelValue<String> getName() {
        return name;
    }
}
