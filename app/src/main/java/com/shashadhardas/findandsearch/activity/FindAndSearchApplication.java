package com.shashadhardas.findandsearch.activity;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.shashadhardas.findandsearch.storage.AppsStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashadhar on 16-01-2017.
 */

public class FindAndSearchApplication extends Application {

    static FindAndSearchApplication instance;
    private ActivityLifeCycleCallBack activityLifeCycleCallback = new ActivityLifeCycleCallBack();
    public  static FindAndSearchApplication getInstance(){
        return  instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        registerActivityLifecycleCallbacks(activityLifeCycleCallback);
        AppsStore.getAppStore(this);

    }


   class  ActivityLifeCycleCallBack implements  Application.ActivityLifecycleCallbacks{

       private List<Activity> activities = new ArrayList<Activity>();
       @Override
       public void onActivityCreated(Activity activity, Bundle bundle) {
           activities.add(activity);
       }

       @Override
       public void onActivityStarted(Activity activity) {

       }

       @Override
       public void onActivityResumed(Activity activity) {

       }

       @Override
       public void onActivityPaused(Activity activity) {

       }

       @Override
       public void onActivityStopped(Activity activity) {

       }

       @Override
       public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

       }

       @Override
       public void onActivityDestroyed(Activity activity) {

       }

       public void clearBackstack(){
           for(Activity a : activities){
                   a.finish();
               }
           }

   }

    public void clearBackstack(){
        activityLifeCycleCallback.clearBackstack();
    }
}
