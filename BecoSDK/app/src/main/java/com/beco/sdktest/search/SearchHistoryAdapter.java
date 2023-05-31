package com.beco.sdktest.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beco.sdktest.R;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> searchHistoryList;
    private ClickListener mListener;
    private Context mContext;

    public interface ClickListener {
        void onSearchHistoryClick(String name);
    }

    public SearchHistoryAdapter(Context context, List<String> list, ClickListener listener) {

        mContext = context;
        searchHistoryList = list;
        mListener = listener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.exp_list_item, viewGroup, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (i == searchHistoryList.size() - 1)
            ((ItemViewHolder) viewHolder).bind(searchHistoryList.get(i), true);
        else
            ((ItemViewHolder) viewHolder).bind(searchHistoryList.get(i), false);

    }

    @Override
    public int getItemCount() {
        return searchHistoryList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        View item, view;
        TextView tvItem;
        TextView tvDescription;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            tvItem = itemView.findViewById(R.id.txt_title);
            tvDescription = itemView.findViewById(R.id.txt_description);
            view = itemView.findViewById(R.id.view);
        }

        private void bind(String data, boolean hideView) {
            String[] list = data.split(",");

            tvItem.setText(list[0]);
            tvDescription.setText(list[1]);
            if (hideView) {
                view.setVisibility(View.INVISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onSearchHistoryClick(list[0]);
                }
            });

        }
    }
}

