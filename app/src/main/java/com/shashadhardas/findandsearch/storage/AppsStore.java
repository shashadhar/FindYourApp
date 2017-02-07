package com.shashadhardas.findandsearch.storage;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shashadhar on 10-01-2017.
 */

public class AppsStore {

    private  Context context;
    public  static String fileName="allAppsFile";
    static AppsStore instance;

   static List<ResolveInfo> appLists;


    private  AppsStore(Context context){
        this.context=context;
        appLists=getAllInstalledApps();
        PackageManager pm=context.getPackageManager();
        Collections.sort(appLists, new ResolveInfo.DisplayNameComparator(pm));
    }
    private  AppsStore(){

    }
    public  static AppsStore getAppStore(Context context){
        if(instance==null){
            return  new AppsStore(context);
        }else{
            return  instance;
        }

    }



    public  List<ResolveInfo> getAppsList(){
        try {
            if(appLists!=null && appLists.size()>0){
                return  appLists;
            }else{
                return getAllInstalledApps();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
     return  new ArrayList<>();

    }

    private List<ResolveInfo>  getAllInstalledApps(){
        try {
            PackageManager p= context.getPackageManager();
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities( mainIntent, 0);
            List<ResolveInfo>filteredPackageList=new ArrayList<>();
            if(pkgAppsList!=null) {
                for (ResolveInfo resolveInfo : pkgAppsList) {
                    if (!isSystemPackage(resolveInfo)) {
                        filteredPackageList.add(resolveInfo);
                    }
                }
            }
            if(filteredPackageList!=null && filteredPackageList.size()>0){
                return  filteredPackageList;
            }
            return  new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  new ArrayList<>();
    }





    private boolean isSystemPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? false : false;
    }


}
