package com.coocaa.remoteplatform.baseability.abilities.time.task;

import java.io.IOException;


public interface Callback {
    void onFailure(Call call, IOException e);

    void onSuccess(Call call, String msg) throws IOException;
}
