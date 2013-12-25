package com.timotuominen.app.viewmodels;

import com.timotuominen.app.utils.AbstractViewModel;
import com.timotuominen.app.utils.Unsubscribable;
import com.timotuominen.app.utils.ViewModelValue;

/**
 * Created by ttuo on 12/25/13.
 */
public class SampleViewModel extends AbstractViewModel {
    @Unsubscribable
    final protected ViewModelValue<String> name = new ViewModelValue<String>();
}
