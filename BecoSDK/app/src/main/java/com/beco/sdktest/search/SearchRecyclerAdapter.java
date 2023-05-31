package com.beco.sdktest.search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beco.sdk.model.BEPoint;
import com.beco.sdktest.R;

import java.util.ArrayList;
import java.util.List;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder>
        implements Filterable {

    private static final String TAG = "SearchRecyclerAdapter";

    private List<BEPoint> searchList;
    private List<BEPoint> searchListFiltered;
    private OnClickListener onClickListener;
    private static int PAYLOAD_FAV = 0;


    SearchRecyclerAdapter(List<BEPoint> searchList, Context context, OnClickListener onClickListener) {
        this.searchList = searchList;
        this.searchListFiltered = searchList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SearchRecyclerAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.place_select_layout, viewGroup, false);
        return new SearchRecyclerAdapter.SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchRecyclerAdapter.SearchViewHolder searchViewHolder, int i) {
        final BEPoint point = searchListFiltered.get(i);
        searchViewHolder.name.setText(point.getName());
        searchViewHolder.description.setText(point.getFloorDescription());

        searchViewHolder.searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(point);
            }
        });
        searchViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onFavClick(searchViewHolder.getAdapterPosition());
            }
        });

    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Log.d(TAG,payloads.toString() + "payloads");
        if (!payloads.isEmpty()) {
//            if (payloads.get(0).equals(PAYLOAD_FAV)) {
//                if (searchList.get(position).isFav()) {
//                    holder.checkBox.setImageResource(R.drawable.star_selected);
//                } else {
//                    holder.checkBox.setImageResource(R.drawable.star_unselected);
//                }
//            }
            holder.checkBox.setImageResource(R.drawable.star_unselected);
        }
    }

    @Override
    public int getItemCount() {
        return searchListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    searchListFiltered = searchList;
                } else {
                    List<BEPoint> filteredList = new ArrayList<>();
                    for (BEPoint row : searchList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    searchListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = searchListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                searchListFiltered = (ArrayList<BEPoint>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        ImageView checkBox;
        LinearLayout searchLayout;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.txt_detail);
            name = itemView.findViewById(R.id.txt_place);
            checkBox = itemView.findViewById(R.id.check_fav);
            searchLayout = itemView.findViewById(R.id.search_layout);
        }
    }


    public interface OnClickListener {
        void onClick(BEPoint point);
        void onFavClick(int position);
    }
}
