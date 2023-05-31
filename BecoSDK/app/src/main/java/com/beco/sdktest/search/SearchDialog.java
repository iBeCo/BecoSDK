package com.beco.sdktest.search;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.beco.sdk.model.BEPoint;
import com.beco.sdktest.R;
import com.beco.sdktest.debug.DebugActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SearchDialog extends DialogFragment implements View.OnClickListener  {

    private List<BEPoint> mapPointList;
    private ImageView back;
    private AppCompatEditText searchText;
    private ImageView closeIcon;
    private CardView searchContainer;
    private ListView searchList;

    private SearchAdapter searchAdapter;
    private OnLocationSelected onLocationSelected;
    private boolean isFrom = true;

    public SearchDialog(List<BEPoint> results) {
        this.mapPointList = results;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

        if (getArguments() != null) {
            isFrom = getArguments().getBoolean("isFrom");
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onLocationSelected = (OnLocationSelected) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.full_screen_dialog, container, false);
        searchAdapter = new SearchAdapter(getContext(), new ArrayList<>());
        searchList = view.findViewById(R.id.search_list);
        back = view.findViewById(R.id.back);
        back.setOnClickListener(this);
        searchText = view.findViewById(R.id.search_text);
        closeIcon = view.findViewById(R.id.close_icon);
        closeIcon.setOnClickListener(this);
        searchContainer = view.findViewById(R.id.search_container);


        searchList.setAdapter(searchAdapter);
        searchList.setOnItemClickListener((parent, view1, position, id) -> {
            BEPoint item = (BEPoint) parent.getAdapter().getItem(position);
            if (onLocationSelected != null)
                onLocationSelected.onLocationSelected(isFrom, item);
            dismissAllowingStateLoss();
        });


        searchText = view.findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("_#sdk#ena#debug")){
                    switchToDebug();
                }else {
                    getResults(s.toString());
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchText.requestFocus();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void getResults(String text) {
        if (!text.trim().isEmpty()) {
            searchList.setVisibility(View.VISIBLE);
            ArrayList<BEPoint> values = new ArrayList<>();
            for (int i = 0; i < mapPointList.size(); i++) {
                if (mapPointList.get(i).toString().contains(text.toLowerCase())) {
                    values.add(mapPointList.get(i));
                }
            }
            searchAdapter.setOriginalData(values);
            searchAdapter.notifyDataSetChanged();
        } else {
            searchList.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                dismissAllowingStateLoss();
                break;
            case R.id.close_icon:
                searchText.setText("");
                break;
        }
    }

    public interface OnLocationSelected {
        void onLocationSelected(boolean isTo, BEPoint point);
    }

    private void switchToDebug(){
        Intent intent = new Intent();
        intent.setClass(Objects.requireNonNull(getActivity()), DebugActivity.class);
        startActivity(intent);
    }
}

