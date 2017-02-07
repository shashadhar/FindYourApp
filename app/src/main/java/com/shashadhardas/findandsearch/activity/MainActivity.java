package com.shashadhardas.findandsearch.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AndroidException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.shashadhardas.findandsearch.R;
import com.shashadhardas.findandsearch.adapter.AppListAdapter;
import com.shashadhardas.findandsearch.adapter.RecyclerViewClickListener;
import com.shashadhardas.findandsearch.database.FileStorage;
import com.shashadhardas.findandsearch.databinding.ActivityMainBinding;
import com.shashadhardas.findandsearch.dialog.GenericDialog;
import com.shashadhardas.findandsearch.storage.AppsStore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    MaterialSearchView searchView;
    AppListAdapter adapter;
    List<ResolveInfo> appList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleSearch();
        FileStorage.appContext=this;
        binding.appList.setLayoutManager(new LinearLayoutManager(this));
        PackageManager pm=getPackageManager();
        appList= AppsStore.getAppStore(this).getAppsList();
        List<ResolveInfo>apps=new ArrayList<>();
        apps.addAll(appList);
        adapter=new AppListAdapter(apps, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                ResolveInfo resolveInfo=adapter.getItem(position);
                switch (view.getId()){

                    case R.id.share:{
                     appShareLink(resolveInfo);
                    }
                    break;
                    case R.id.details:{
                        showAppDetails(resolveInfo.activityInfo.packageName);
                    }
                    break;
                    case R.id.open:{
                     openApp(resolveInfo);
                    }
                    break;
                    case R.id.uninstall:{
                        uninstallApp(resolveInfo.activityInfo.packageName);
                    }
                    break;
                   default:{
                       openApp(resolveInfo);
                   }
                }

            }
        },pm);
      binding.appList.setAdapter(adapter);
      setTitle(String.format("Total Apps (%s)", appList.size()));
    }

    public  void uninstallApp(String packageName){
        try {
            Uri packageURI = Uri.parse("package:"+packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            startActivity(uninstallIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void showShareDialog(String subject,String body) {
        try {
            Bundle data=new Bundle();
            data.putString("subject",subject);
            data.putString("body",body);
            final GenericDialog shareDialog = GenericDialog.getInstanceForShare(data);
            shareDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogSlideAnim);
            shareDialog.setCancelable(true);
            shareDialog.show(this.getSupportFragmentManager(), "Share dialog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void appShareLink(ResolveInfo resolveInfo){
        try {
            PackageManager pm=getPackageManager();
            showShareDialog((String)resolveInfo.activityInfo.loadLabel(pm),"https://play.google.com/store/apps/details?id=" + resolveInfo.activityInfo.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }


       /* try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }*/
    }
   public void openApp(ResolveInfo resolveInfo) {

       try {
           Intent intent = this.getPackageManager().getLaunchIntentForPackage(resolveInfo.activityInfo.packageName);
           if (intent != null) {
               // We found the activity now start the activity
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
           } else {
               ActivityInfo activity=resolveInfo.activityInfo;
               ComponentName name=new ComponentName(activity.applicationInfo.packageName,
                       activity.name);
               Intent i=new Intent(Intent.ACTION_MAIN);
               i.addCategory(Intent.CATEGORY_LAUNCHER);
               i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                       Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
               i.setComponent(name);
               startActivity(i);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }

   }

    public  void showAppDetails(String packageName) {

        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);

        }
    }

    private void handleSearch() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FilterApps(query);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FilterApps(newText);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                try {
                    adapter.updateData(appList);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void FilterApps(String query) {
        try {
            if(query!=null && !query.isEmpty()) {
                List<ResolveInfo> searchedApps = new ArrayList<ResolveInfo>();
                PackageManager pm = getPackageManager();
                for (ResolveInfo resolveInfo : appList) {
                    String label = (String) resolveInfo.activityInfo.loadLabel(pm);
                    label = label.toLowerCase();
                    if (label!=null && label.contains(query.toLowerCase())) {
                        searchedApps.add(resolveInfo);
                    }
                }
              if(searchedApps!=null && searchedApps.size()>0){
                  adapter.updateData(searchedApps);
                  adapter.notifyDataSetChanged();
              }

            }else if(query.isEmpty()){
                adapter.updateData(appList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.showVoice(true);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


}
