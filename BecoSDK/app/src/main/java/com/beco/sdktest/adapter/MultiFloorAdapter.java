package com.beco.sdktest.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beco.sdk.map.BERouteFloor;
import com.beco.sdktest.R;

import java.util.List;

public class MultiFloorAdapter extends RecyclerView.Adapter<MultiFloorAdapter.MultiFloorViewHolder> {

    private static final String TAG = "MultiFloorAdapter";

    private List<BERouteFloor> multiFloorModelList;
    private Context context;
    private MultiFloorClickListener mListener;
    private int position;

    public interface MultiFloorClickListener {
        void onFloorClick(int position);
    }

    public MultiFloorAdapter(List<BERouteFloor> multiFloorModelList, Context context, MultiFloorClickListener listener) {
        this.multiFloorModelList = multiFloorModelList;
        this.context = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public MultiFloorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_multi_floor, viewGroup, false);
        return new MultiFloorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiFloorViewHolder favoriteViewHolder, int i) {

        BERouteFloor multiFloorModel = multiFloorModelList.get(i);
        favoriteViewHolder.tvFloorCount.setText(multiFloorModel.getLabel());

        if(position == i){
            favoriteViewHolder.tvFloorCount.setTextColor(context.getResources().getColor(R.color.white));
            favoriteViewHolder.tvFloorCount.setBackgroundResource(R.drawable.bg_multi_floor_fill);
        }else{
            favoriteViewHolder.tvFloorCount.setTextColor(context.getResources().getColor(R.color.blue));
            favoriteViewHolder.tvFloorCount.setBackgroundResource(R.drawable.bg_multi_floor_outline);
        }



    }

    @Override
    public int getItemCount() {
        return multiFloorModelList.size();
    }

    public void updateList(List<BERouteFloor> multiFloorModelList, int position){
        this.multiFloorModelList = multiFloorModelList;
        this.position = position;
        notifyDataSetChanged();
    }

    public void setPosition(int position){
        this.position = position;
        notifyDataSetChanged();
    }



    class MultiFloorViewHolder extends RecyclerView.ViewHolder {
        TextView tvFloorCount;

        MultiFloorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFloorCount = itemView.findViewById(R.id.tvFloorCount);

            itemView.setOnClickListener(view -> {
                Log.d(TAG,position+"----"+getAdapterPosition());
                if(position != getAdapterPosition() ){
                    setPosition(getAdapterPosition());
                    mListener.onFloorClick(position);
                }

            });
        }
    }
}