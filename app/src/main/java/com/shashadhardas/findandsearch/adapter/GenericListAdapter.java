package com.shashadhardas.findandsearch.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by shashadhar on 21-09-2016.
 */
public class GenericListAdapter<T> extends RecyclerView.Adapter<GenericListAdapter.ViewHolder> {
    List<T> data;
    RecyclerViewClickListener clickListener;
    MyViewHolder<T> holder;

   public interface  MyViewHolder<T>{
      View onCreateViewHolder(ViewGroup parent, int viewType);
      void onBindView(T item, GenericListAdapter.ViewHolder view, int position);

    }
   public GenericListAdapter(List<T> data, RecyclerViewClickListener listener, MyViewHolder<T> holder){
       this.data=data;
       this.clickListener=listener;
       this.holder=holder;
   }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private Map<Integer, View> mMapView;
        public RecyclerViewClickListener mListener;
        public ViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);
            mMapView = new HashMap<>();
            mMapView.put(0, v);
            mListener=listener;
        }

        public void initViewById(int id) {
            View view = (getView() != null ? getView().findViewById(id) : null);

            if (view != null)
                mMapView.put(id, view);
        }

        public View getView() {
            return getView(0);
        }

        public View getView(int id) {
            if (mMapView.containsKey(id))
                return mMapView.get(id);
            else
                initViewById(id);

            return mMapView.get(id);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder VH= new ViewHolder(holder.onCreateViewHolder(parent,viewType),clickListener);
        return   VH;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
             this.holder.onBindView(data.get(position),holder,position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public T getItem(int itemPosition){
        return data.get(itemPosition);
    }
}
