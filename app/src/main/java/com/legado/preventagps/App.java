package com.legado.preventagps;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.legado.preventagps.util.UpdateHelper;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseRemoteConfig remoteConfig=FirebaseRemoteConfig.getInstance();
        Map<String,Object> defaultValues= new HashMap<>();
        defaultValues.put(UpdateHelper.KEY_UPDATE_ENABLE,false);
        defaultValues.put(UpdateHelper.KEY_UPDATE_VERSION,"2.0");
        defaultValues.put(UpdateHelper.KEY_UPDATE_URL,"http://gps-inka.com/preventagps.apk");

        remoteConfig.setDefaults(defaultValues);
        remoteConfig.fetch(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    remoteConfig.activate();

                }
            }
        });
    }
}
