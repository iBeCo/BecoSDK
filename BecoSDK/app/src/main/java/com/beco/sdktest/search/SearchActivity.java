package com.beco.sdktest.search;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beco.sdk.model.BEPoint;
import com.beco.sdktest.App;
import com.beco.sdktest.R;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, SearchRecyclerAdapter.OnClickListener, SearchHistoryAdapter.ClickListener {

    private static final String TAG = "SearchActivity";

    SearchHistoryAdapter listAdapter;
    ImageView img_back, img_mic;
    EditText edtSearch;
    LinearLayout linear_favorite;
    RecyclerView rvSearch, rvHistory;
    SearchRecyclerAdapter searchAdapter;
    private List<BEPoint> mapPointList;
    private int position;

    public static int PAYLOAD_FAV = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initialize();
        mapPointList = App.getInstance().getPoints();
        Intent i = getIntent();
        String location = i.getStringExtra("location");
        position = i.getIntExtra("position",0);

        addSearchData();


        listAdapter = new SearchHistoryAdapter(this, SearchHistory.loadList(this), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvHistory.setLayoutManager(mLayoutManager);
        rvHistory.setAdapter(listAdapter);


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    rvSearch.setVisibility(View.GONE);
                    img_mic.setImageResource(R.drawable.mic_icon);
                    img_mic.setTag("mic");
                } else {
                    rvSearch.setVisibility(View.VISIBLE);
                    img_mic.setImageResource(R.drawable.ic_close);
                    img_mic.setTag("clear");
                }
                searchAdapter.getFilter().filter(s.toString());


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        edtSearch.setText(location);

    }

    private void addSearchData() {
        searchAdapter = new SearchRecyclerAdapter(mapPointList, this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvSearch.setLayoutManager(mLayoutManager);
        rvSearch.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void initialize() {
        img_back = findViewById(R.id.img_back);
        img_mic = findViewById(R.id.img_mic);
        edtSearch = findViewById(R.id.edt_search);
        linear_favorite = findViewById(R.id.linear_favorite);
        rvSearch = findViewById(R.id.rv_search);
        rvHistory = findViewById(R.id.rvHistory);

        if (edtSearch.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        img_back.setOnClickListener(this);
        img_mic.setOnClickListener(this);
        edtSearch.setOnClickListener(this);
        linear_favorite.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.linear_favorite:
//                Intent intent = new Intent(SearchActivity.this, FavoriteActivity.class);
//                startActivityForResult(intent, 101);
                break;
            case R.id.edt_search:
               /* bg_linear.setVisibility(View.GONE);
                rvSearch.setVisibility(View.VISIBLE);*/

                break;

            case R.id.img_mic:
                if(img_mic.getTag()=="mic"){

                }else{
                    edtSearch.setText("");
                }


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == 0) {
                if (data != null) {
                    Intent intent = new Intent();
                    intent.putExtra("location", data.getStringExtra("location"));
                    intent.putExtra("detail", data.getStringExtra("detail"));
                    setResult(0, intent);
                    finish();
                }
            }

        }
    }

    @Override
    public void onClick(BEPoint point) {
        SearchHistory.addList(point.getName() +","+point.getFloorDescription(),this);
        Intent intent = new Intent();
        App.getInstance().addToWayPoint(point,position);
        setResult(0, intent);
        finish();
    }

    @Override
    public void onFavClick(int posi) {
        //TODO fix
//        if (searchList.get(posi).isFav()) {
//
//            showConfirmRemoveDialog(posi);
//
//        } else {
//            searchList.get(posi).setFav(!searchList.get(posi).isFav());
//            searchAdapter.notifyItemChanged(posi, PAYLOAD_FAV);
//            showEnterNameDialog(searchList.get(posi).getPlace());
//        }
    }

    private void showEnterNameDialog(String presentName) {
        final Dialog dialog = new Dialog(SearchActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_name);

        TextView cancelButton = (TextView) dialog.findViewById(R.id.btnCancel);
        TextView doneButton = (TextView) dialog.findViewById(R.id.btnDone);
        final EditText etName = (EditText) dialog.findViewById(R.id.etName);
        etName.setText(presentName);
        ImageView clear = (ImageView) dialog.findViewById(R.id.imgClear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etName.setText("");
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*ToDo */

                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //TODO Uncomment
//    private void showConfirmRemoveDialog(final int posi) {
//        final Dialog dialog = new Dialog(SearchActivity.this);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setContentView(R.layout.dialog_remove_fav);
//
//        TextView cancelButton = (TextView) dialog.findViewById(R.id.btnCancel);
//        TextView doneButton = (TextView) dialog.findViewById(R.id.btnDone);
//
//        doneButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                /*ToDo */
//                searchList.get(posi).setFav(!searchList.get(posi).isFav());
//                searchAdapter.notifyItemChanged(posi, PAYLOAD_FAV);
//                dialog.dismiss();
//            }
//        });
//
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

    @Override
    public void onSearchHistoryClick(String name) {
        BEPoint point  = App.getInstance().findPoint(name);
        if(point != null){
            Intent intent = new Intent();
            App.getInstance().addToWayPoint(point,position);
            setResult(0, intent);
            finish();
        }else {
            //TODO remove history .. need testing
            Toast.makeText(this,"Place not exist",Toast.LENGTH_SHORT).show();
        }
        //

    }

}

