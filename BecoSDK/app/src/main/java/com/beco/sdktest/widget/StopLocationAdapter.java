package com.beco.sdktest.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beco.sdktest.R;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

public class StopLocationAdapter extends RecyclerView.Adapter<StopLocationAdapter.MyViewHolder>
        implements DraggableItemAdapter<StopLocationAdapter.MyViewHolder> {

    private StopDataProvider mProvider;
    private OnItemClickListener mListener;

    public StopLocationAdapter(StopDataProvider provider, OnItemClickListener listener) {
        mProvider = provider;
        mListener = listener;
        setHasStableIds(true);
    }

    public interface OnItemClickListener {
        void onStopClick(int position);

        void onClearItem(int position);

        void onMinItemCountReached();

        void onMinItemCountExceed();

        void onStartCurrentLocation();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_location_stops, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        if (mProvider.getCount() > 2) {
            mListener.onMinItemCountExceed();
            if (mProvider.getItem(i).isCurrentLoc())
                myViewHolder.imgLocation.setImageResource(R.drawable.ic_current_loc);
            else
                myViewHolder.imgLocation.setVisibility(View.GONE);

            myViewHolder.tvStopsChar.setVisibility(View.VISIBLE);

            if (mProvider.getItem(i).isCurrentLoc())
                showStopChar(myViewHolder, 0);
            else
                showStopChar(myViewHolder, i);

            showCloseButton(myViewHolder, i);
        } else {
            mListener.onMinItemCountReached();
            myViewHolder.imgLocation.setVisibility(View.VISIBLE);
            myViewHolder.tvStopsChar.setVisibility(View.GONE);
            myViewHolder.imgClose.setVisibility(View.GONE);

            if (i == 0) {
                myViewHolder.imgLocation.setImageResource(R.drawable.ic_stops_bg);
            } else {
                myViewHolder.imgLocation.setImageResource(R.drawable.ic_marker_red);
            }

            if (mProvider.getItem(i).isCurrentLoc())
                myViewHolder.imgLocation.setImageResource(R.drawable.ic_current_loc);
        }

        myViewHolder.etStops.setText(mProvider.getItem(i).getmText());


    }



    private void showCloseButton(MyViewHolder myViewHolder, int posi) {
        if (mProvider.getItem(posi).getmText().equals("")) {
            myViewHolder.imgClose.setVisibility(View.GONE);
        } else {
            myViewHolder.imgClose.setVisibility(View.VISIBLE);
        }
    }

    private void showStopChar(MyViewHolder myViewHolder, int posi) {
        switch (posi) {
            case 1:
                myViewHolder.tvStopsChar.setText("A");
                break;
            case 2:
                myViewHolder.tvStopsChar.setText("B");
                break;
            case 3:
                myViewHolder.tvStopsChar.setText("C");
                break;
            case 4:
                myViewHolder.tvStopsChar.setText("D");
                break;
            case 5:
                myViewHolder.tvStopsChar.setText("E");
                break;
            case 6:
                myViewHolder.tvStopsChar.setText("F");
                break;
            case 7:
                myViewHolder.tvStopsChar.setText("G");
                break;
            case 8:
                myViewHolder.tvStopsChar.setText("H");
                break;
            case 9:
                myViewHolder.tvStopsChar.setText("I");
                break;
            default:
                myViewHolder.tvStopsChar.setText("");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getCount() == 0 ? 0 : mProvider.getItem(position).getmId();
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        return false;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        mProvider.moveItem(fromPosition, toPosition);
//        notifyItemMoved(fromPosition, toPosition);
        notifyDataSetChanged();
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    public class MyViewHolder extends AbstractDraggableItemViewHolder {

        TextView tvStopsChar;
        ImageView imgLocation, imgDragBar, imgClose;
        EditText etStops;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvStopsChar = itemView.findViewById(R.id.tvStopsChar);
            imgLocation = itemView.findViewById(R.id.imgLocation);
            imgDragBar = itemView.findViewById(R.id.imgDragBar);
            imgClose = itemView.findViewById(R.id.imgClose);
            etStops = itemView.findViewById(R.id.etStops);

            etStops.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onStopClick(getAdapterPosition());
                }
            });

            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClearItem(getAdapterPosition());
                }
            });

            etStops.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (getAdapterPosition() == 0) {
                        mListener.onStartCurrentLocation();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }
}