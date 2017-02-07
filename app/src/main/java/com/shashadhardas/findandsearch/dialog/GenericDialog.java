package com.shashadhardas.findandsearch.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shashadhardas.findandsearch.R;
import com.shashadhardas.findandsearch.adapter.AppListAdapter;
import com.shashadhardas.findandsearch.adapter.RecyclerViewClickListener;
import com.shashadhardas.findandsearch.adapter.ShareAdapter;
import com.shashadhardas.findandsearch.databinding.ShareDialogBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shashadhar on 16-09-2015.
 */
public class GenericDialog extends DialogFragment {

    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    public static String DIALOG_FOR_NETWORK_ERROR = "dialogForNetworkError";
    public static String DIALOG_FOR_FORGOT_PASSWORD = "dialogForForgotPassword";
    public static String DIALOG_FOR_USER_TYPE = "dialogForForUserType";
    public static String DIALOG_FOR_SHARE = "dialogForForShare";
    private String requestCode = null;
    public static String KEY_REQUEST_CODE = "requestCode";
    public static String KEY_NETWORK_ERROR_MESSAGE = "networkErrorMessage";
    String networkMessage;
    Bundle shareIntentBundle;

    public interface dialogListener {
        void onDialogActionDone(String requestCode, Bundle data, View v, GenericDialog dialog);
    }

    public static GenericDialog getInstanceForShare(Bundle dataBundle) {
        GenericDialog dialog;
        dialog = new GenericDialog();
        //Bundle bundle = new Bundle();
        dataBundle.putString(KEY_REQUEST_CODE, DIALOG_FOR_SHARE);
        dialog.setArguments(dataBundle);
        return dialog;
    }

    public dialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            try {
                 mListener = (dialogListener) activity;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ClassCastException e) {

            throw new ClassCastException(activity.toString()
                    + " must implement terms and conditions dialog");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null && !requestCode.equals(DIALOG_FOR_NETWORK_ERROR)) {
            mListener.onDialogActionDone(requestCode, null, null, GenericDialog.this);
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog);
        requestCode = getArguments().getString(KEY_REQUEST_CODE);
        if (requestCode.equals(DIALOG_FOR_NETWORK_ERROR)) {
            dialog.setTitle("Network Error");
            networkMessage = getArguments().getString(KEY_NETWORK_ERROR_MESSAGE);
        } else if (requestCode.equals(DIALOG_FOR_FORGOT_PASSWORD)) {
            dialog.setTitle("Reset password");
        } else if (requestCode.equals(DIALOG_FOR_USER_TYPE)) {
            dialog.setTitle("");
            dialog.setCanceledOnTouchOutside(false);
        } else if (requestCode.equals(DIALOG_FOR_SHARE)) {
            dialog.setTitle("Share");
            shareIntentBundle=getArguments();

        }

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        try {
            if (requestCode.equals(DIALOG_FOR_SHARE)) {
                rootView = setupDialogForShare(inflater, container);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new RelativeLayout(getActivity());

        }

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public static List<ResolveInfo> shareIntent(Context ct, Intent sharingIntent) {
        try {
            PackageManager pm = ct.getPackageManager();
            List<ResolveInfo> appList = pm.queryIntentActivities(sharingIntent, 0);
            //List<ResolveInfo> FilteredAppList = FilterApplist(appList);
            Collections.sort(appList, new ResolveInfo.DisplayNameComparator(pm));
            return appList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<ResolveInfo> FilterApplist(List<ResolveInfo> applist) {
        try {
            List<ResolveInfo> filteredApplist = new ArrayList<>();
            for (int i = 0; i < applist.size(); i++) {
                ResolveInfo ri = applist.get(i);
                String packageName = ri.activityInfo.packageName;
                String packageNameLower = null;
                if (packageName != null) {
                    packageNameLower = packageName.toLowerCase();
                }
                if (packageNameLower != null && (packageNameLower.contains("twitter")
                        || packageNameLower.contains("facebook")
                        || packageNameLower.contains("android.gm")
                        || packageNameLower.contains("whatsapp")
                        || packageNameLower.contains("email")
                        || packageNameLower.contains("plus")
                        || packageNameLower.contains("skype")
                        || packageNameLower.contains("messages")
                        || packageNameLower.contains("android.talk")
                        || packageNameLower.contains("sms")
                )) {
                    filteredApplist.add(ri);
                }


            }
            return filteredApplist;
        } catch (Exception e) {
            e.printStackTrace();
            return  applist;
        }

    }
    @NonNull
    private View setupDialogForShare(LayoutInflater inflater, ViewGroup container) {
        View rootView;
        final ShareDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.share_dialog, container, false);
        binding.appList.setLayoutManager(new LinearLayoutManager(getActivity()));
        final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareIntentBundle.getString("subject"));
        sharingIntent.putExtra(Intent.EXTRA_TEXT,shareIntentBundle.getString("body"));
        List<ResolveInfo> FilteredAppList=shareIntent(getActivity(),sharingIntent);


        final ShareAdapter adapter = new ShareAdapter(FilteredAppList, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    ResolveInfo launchable = ((ShareAdapter) binding.appList.getAdapter()).getItem(position);
                    ActivityInfo activity = launchable.activityInfo;
                    ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                            activity.name);
                    sharingIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    sharingIntent.setComponent(name);
                    startActivity(sharingIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }, getActivity().getPackageManager());
        binding.appList.setAdapter(adapter);
        return binding.getRoot();

    }




}
