package com.shashadhardas.findandsearch.adapter;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.shashadhardas.findandsearch.R;
import com.shashadhardas.findandsearch.databinding.ShareRowBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Shashadhar on 23-10-2016.
 */
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private List<ResolveInfo> mDataset;
    RecyclerViewClickListener clickListener;
    int lastPos = -1;
    PackageManager pm;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public ShareRowBinding binding;

        public ViewHolder(ShareRowBinding v) {
            super(v.getRoot());
            try {
            binding=v;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public ShareAdapter(List<ResolveInfo> dataset,
                        RecyclerViewClickListener clickListener, PackageManager pm) {
        try {
            mDataset = dataset;
            this.clickListener = clickListener;
            this.pm =pm;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateData(List<ResolveInfo> news) {
        try {
            List<ResolveInfo> infoList=new ArrayList<>();
            infoList.addAll(news);
            this.mDataset.clear();
            this.mDataset.addAll(infoList);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ShareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            // create a new view
            ShareRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.share_row, parent, false);
            ViewHolder vh = new ViewHolder(binding);
            return vh;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            final ResolveInfo info = mDataset.get(position);
            holder.binding.shopName.setText(info.activityInfo.loadLabel(pm).toString());
            holder.binding.image.setImageDrawable(info.activityInfo.loadIcon(pm));
            holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(v,position);
                }
            });

            }catch(Exception e){
                e.printStackTrace();
            }
        }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
 public ResolveInfo getItem(int position){
     try {
         if(mDataset!=null && mDataset.size()>0){
             return  mDataset.get(position);
         }
     } catch (Exception e) {
         e.printStackTrace();
     }
     return  null;
 }

}
