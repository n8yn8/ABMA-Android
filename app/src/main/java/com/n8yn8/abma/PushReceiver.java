package com.n8yn8.abma;

import com.backendless.push.BackendlessBroadcastReceiver;
import com.backendless.push.BackendlessPushService;

/**
 * Created by Nate on 3/16/17.
 */

public class PushReceiver extends BackendlessBroadcastReceiver {

    @Override
    public Class<? extends BackendlessPushService> getServiceClass() {
        return PushService.class;
    }
}
