package com.beco.sdktest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beco.sdk.credentialprovider.BeCoCredentialProvider;
import com.beco.sdk.exception.BeaconNotFoundException;
import com.beco.sdk.exception.InvalidRouteRequestException;
import com.beco.sdk.exception.SiteNotSetException;
import com.beco.sdk.map.BEErrorCode;
import com.beco.sdk.map.BEMapFragment;
import com.beco.sdk.map.BERouteFloor;
import com.beco.sdk.map.BESiteCallback;
import com.beco.sdk.map.BEValidateCallback;
import com.beco.sdk.map.BeCo;
import com.beco.sdk.map.OnCurrentLocationChangeListener;
import com.beco.sdk.map.OnMapReadyCallback;
import com.beco.sdk.map.OnNavigationListener;
import com.beco.sdk.map.OnPointClickListener;
import com.beco.sdk.model.BEPoint;
import com.beco.sdk.model.BESite;
import com.beco.sdktest.adapter.MultiFloorAdapter;
import com.beco.sdktest.search.SearchActivity;
import com.beco.sdktest.utils.LayoutUtils;
import com.beco.sdktest.widget.BEMultiRouteView;
import com.beco.sdktest.widget.BEMultiSearchView;
import com.beco.sdktest.widget.BESearchBarView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.altbeacon.beacon.BeaconManager;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, OnPointClickListener, BESearchBarView.SearchBarListener,
        BEMultiSearchView.MultiSearchBarListener, OnCurrentLocationChangeListener,
        MultiFloorAdapter.MultiFloorClickListener,
        BEMultiRouteView.MultiRouteListener, OnNavigationListener {

    private static final String TAG = "MapActivity";
    private static final String SITE_ID = "hilite-mall";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BEMapFragment beMapFragment;
    private BeCo sdk;
    private List<BESite> beSiteList;
    private BESearchBarView searchBarView;
    private BEMultiSearchView multiSearchBarView;
    private BEMultiRouteView selectedRouteView;
    private String usageToken;


    private BottomSheetBehavior bottomSheetMarkerBehavior;
    private LinearLayout bottomSheetMarkerLayout;
    private TextView bottomSheetLocationName;
    private TextView bottomSheetLocationDescription;

    private BottomSheetBehavior bottomSheetRouteBehavior;
    private LinearLayout bottomSheetRouteLayout;
    private boolean animateFirstTime = true;
    private LinearLayout llBottomButtons;
    private RecyclerView rvMultipleFloor;
    private MultiFloorAdapter multiFloorAdapter;
    private TextView bottomSheetRouteLocationName;
    private TextView bottomSheetRouteLocationDescription;
    private LinearLayout rvMultipleFloorLayout;
    private List<BERouteFloor> floorModelList;
    private View multiFloorDivider;

    //private BEPoint destination;
    private boolean isRoutePlotted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        beMapFragment = (BEMapFragment) findViewById(R.id.beMap);


        usageToken = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("pref_api_host_key", "");

        if (usageToken.equals("")) {
            Log.d(TAG, "empty key setting the default key");
            usageToken = "6530be9d54dda44289b4817802680a4e538841df"; //Hi lite production
//            usageToken = "f6fe34607bd16a393895fbe0606b8dcb9397af83"; //2.5D Demo
            //usageToken = "264dc4482b0f0ff69294d6d6d9c549fad56b8fd4"; //Lulu Renjith
            //usageToken = "39fb8ab59cd9074c89dfb1d94c4e4dcad608e909"; //Albert
//            usageToken = "264dc4482b0f0ff69294d6d6d9c549fad56b8fd4"; //Issac Jio SIT
//            usageToken = "f6fe34607bd16a393895fbe0606b8dcb9397af83"; //YH
        }

        verifyBluetooth();
        checkPermission();
        init();
        addMultiFloorData();
    }


    private void init() {
        searchBarView = (BESearchBarView) findViewById(R.id.beCoSearchBar);
        searchBarView.setListener(this);
        multiSearchBarView = (BEMultiSearchView) findViewById(R.id.beCoMultiSearchBar);
        multiSearchBarView.setListener(this);
        multiSearchBarView.setLimit(5);
        selectedRouteView = (BEMultiRouteView) findViewById(R.id.beCoMultiRouteView);
        selectedRouteView.setListener(this);
        bottomSheetLocationName = findViewById(R.id.bottom_sheet_txt_place);
        TextView bottomSheetCancel = findViewById(R.id.btn_sheet_cancel);
        LinearLayout bottomSheetDirection = findViewById(R.id.btn_sheet_directions);
        bottomSheetLocationDescription = findViewById(R.id.bottom_sheet_txt_description);
        bottomSheetMarkerLayout = findViewById(R.id.marker_bottom_sheet);
        bottomSheetMarkerBehavior = BottomSheetBehavior.from(bottomSheetMarkerLayout);


        bottomSheetMarkerBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_COLLAPSED) {
                    searchBarView.showDirectionImage();
                    searchBarView.hideCloseImage();
                    searchBarView.setPoint(null);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        bottomSheetCancel.setOnClickListener(v -> {
            closeBottomMarkerSheet();
        });

        bottomSheetDirection.setOnClickListener(v -> {
            onNavigateButtonClick();
        });


        bottomSheetRouteLayout = findViewById(R.id.route_bottom_sheet);
        bottomSheetRouteBehavior = BottomSheetBehavior.from(bottomSheetRouteLayout);
        bottomSheetRouteLocationName = findViewById(R.id.txt_place_on_route);
        bottomSheetRouteLocationDescription = findViewById(R.id.txt_detail_on_route);

        llBottomButtons = findViewById(R.id.llBottomButtons);
        TextView bottomSheetRouteCancel = findViewById(R.id.route_bottom_sheet_cancel);
        TextView bottomSheetRoutePreview = findViewById(R.id.btn_sheet_preview);
        LinearLayout bottomSheetRouteStart = findViewById(R.id.btn_sheet_start);

        bottomSheetRouteCancel.setOnClickListener(v -> {
            multiSearchBarView.clearAndHide();
            selectedRouteView.hide();
            onBackButtonClick();
        });

        bottomSheetRoutePreview.setOnClickListener(v -> {
            beMapFragment.preview();

            //Hide the bottom sheet
            llBottomButtons.setVisibility(View.GONE);
            bottomSheetRouteLayout.setVisibility(View.GONE);

            multiSearchBarView.hide();

            selectedRouteView.hide();

        });

        bottomSheetRouteStart.setOnClickListener(v -> {
            Log.d(TAG,"bottomSheetRouteStart");
            
            try {
                beMapFragment.navigate();
                //Hide the bottom sheet
                llBottomButtons.setVisibility(View.GONE);
                bottomSheetRouteLayout.setVisibility(View.GONE);
                multiSearchBarView.hide();
                selectedRouteView.hide();
            } catch (BeaconNotFoundException e) {
                e.printStackTrace();
                Log.d(TAG,"error- No beacons on the current route");
            }
        });

    }

    private void addMultiFloorData() {
        floorModelList = new ArrayList<>();
        rvMultipleFloor = findViewById(R.id.rvMultipleFloor);
        multiFloorDivider = findViewById(R.id.multiFloorDivider);
        rvMultipleFloorLayout = findViewById(R.id.rvMultipleFloorLayout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        multiFloorAdapter = new MultiFloorAdapter(floorModelList, this, this);
        rvMultipleFloor.setLayoutManager(layoutManager);
        rvMultipleFloor.setAdapter(multiFloorAdapter);
        multiFloorAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        multiSearchBarView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy in Main activity");
        if (beMapFragment != null) {
            beMapFragment.onDestroy();
        }
        multiSearchBarView.onDestroy();
        super.onDestroy();
    }


    @Override
    public void onMapLoaded() {
        //Init your views here
        Log.d(TAG, "onMapLoaded");
        try {
            App.getInstance().setPoints(beMapFragment.getPoints());
        } catch (SiteNotSetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady() {
        Log.d(TAG, "onMapReady");
        initSDK();
    }

    private void initSDK() {
        sdk = BeCo.init();
//        String licenseKey = "d5a1c252-c09d-4931-baf2-7b6fdff7520b"; //Staging
        String licenseKey = "6ba68d21-0acc-40d4-b177-235cb3f0705a"; //Production
        sdk.configure(this, new BeCoCredentialProvider(usageToken, licenseKey), new BEValidateCallback() {
            @Override
            public void onError(BEErrorCode errorCode) {
                Log.d(TAG, "error- The application is not authorized to use the Beco SDK Platform");
            }

            @Override
            public void onLoadFinished() {
                Log.d(TAG, "success");
                getSite();
            }
        });
    }

    private void getSite() {
        sdk.getSites(null, new BESiteCallback() {
            @Override
            public void onError(BEErrorCode status) {
                Log.d(TAG, "error");
            }

            @Override
            public void onLoadFinished(List<BESite> sites) {
                Log.d(TAG, "got sites");
                beSiteList = sites;
                Log.d(TAG, beSiteList.toString());
                initMap();
            }
        });
    }

    private void initMap() {
        for (BESite site: beSiteList){
            if(site.getIdentifier().equals(SITE_ID)){
                beMapFragment.setSite(site);
                beMapFragment.setOnPointClickListener(this);
                beMapFragment.setOnCurrentLocationChangeListener(this);
                beMapFragment.setOnNavigationListener(this);
            }
        }

//        beMapFragment.setOnReRouteClickListener(this);
    }

    @Override
    public void onMapError(BEErrorCode code) {
        Log.d(TAG, "onMapError");
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION));
                builder.show();
            } else {
                beMapFragment.getMapAsync(this, this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "coarse location permission granted");
                beMapFragment.getMapAsync(this, this);
            } else {
                beMapFragment.getMapAsync(this, this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }

                });
                builder.show();
            }
        }
    }

    private void verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(dialog -> {
                    //finish();
                    //System.exit(0);
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(dialog -> {
                //finish();
                //System.exit(0);
            });
            builder.show();

        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit?");
        builder.setMessage("Are you sure you want to exit?.");
        builder.setNegativeButton(android.R.string.no, null);
        builder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
            finish();
            System.gc();
            System.exit(0);
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "location- result code" + requestCode);
        if (requestCode == 101) {
            if (data != null) {
                BEPoint destination = App.getInstance().getWayPoint(1);
                beMapFragment.selectPoint(destination);
                Log.d(TAG, "Select Point");
                showBottomMarkerSheet(destination);
            }
        } else {
            //TODO fix issue when come via map click
            multiSearchBarView.setData(requestCode, App.getInstance().getWayPoint(requestCode));
            if (App.getInstance().getWayPointCount() == 2) {
                showRoute(App.getInstance().getWayPoint(0),App.getInstance().getWayPoint(1),null);
            }

        }

    }

    private void showRoute(BEPoint start, BEPoint end, List<BEPoint> wayPoints) {
        if (App.getInstance().getWayPointCount() >= 2) {
            try {
                floorModelList = beMapFragment.getRoute(start, end,wayPoints);
                if (floorModelList != null) {
                    Log.d(TAG, floorModelList.toString());
                    if (floorModelList.size() == 1) {
                        rvMultipleFloorLayout.setVisibility(View.GONE);
                        multiFloorDivider.setVisibility(View.GONE);
                        Log.d(TAG, "one");
                    } else {
                        rvMultipleFloorLayout.setVisibility(View.VISIBLE);
                        multiFloorDivider.setVisibility(View.VISIBLE);
                        multiFloorAdapter.updateList(floorModelList, 0);
                        Log.d(TAG, ">" + floorModelList.size());
                    }
                    bottomSheetRouteLocationName.setText(App.getInstance().getWayPoint(1).getName());
                    bottomSheetRouteLocationDescription.setText(App.getInstance().getWayPoint(1).getFloorDescription());
                    beMapFragment.showRouteOnFloor(floorModelList.get(0).getFloorId());
                    isRoutePlotted = true;
                    showBottomRouteSheet();
                }

            } catch (InvalidRouteRequestException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCloseButtonClick(View view) {
        closeBottomMarkerSheet();
        Log.d(TAG, "onCloseButtonClick");
    }

    @Override
    public void onNavigateButtonClick() {
        searchBarView.setVisibility(View.GONE);
        multiSearchBarView.show(searchBarView.getPoint());
        bottomSheetMarkerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Log.d(TAG, "onNavigateButtonClick");
        if (App.getInstance().getWayPointCount() == 2) {
            showRoute(App.getInstance().getWayPoint(0),App.getInstance().getWayPoint(1),null);
        }
    }


    @Override
    public void onSearchClick(View view) {
        Log.d(TAG, "onSearchClick");
        Intent intent = new Intent(MapActivity.this, SearchActivity.class);
        intent.putExtra("location", "");
        intent.putExtra("position", 1);
        startActivityForResult(intent, 101);

    }

    @Override
    public void onPointClick(BEPoint point) {
        if (!isRoutePlotted) {
            App.getInstance().addToWayPoint(point, 1);
            showBottomMarkerSheet(point);
        }
    }

    private void closeBottomMarkerSheet() {
        searchBarView.setPoint(null);
        searchBarView.hideCloseImage();
        searchBarView.showDirectionImage();
        bottomSheetMarkerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        beMapFragment.clearPoint();

        if (multiSearchBarView.hasCurrentLocation()) {
            App.getInstance().clearAllExceptCurrentPoint();
        } else {
            App.getInstance().clearAllPoint();
        }

    }

    private void showBottomMarkerSheet(BEPoint point) {
        searchBarView.setPoint(point);
        bottomSheetLocationName.setText(point.getName());
        bottomSheetLocationDescription.setText(point.getFloorDescription());
        searchBarView.showCloseImage();
        searchBarView.hideDirectionImage();
        bottomSheetMarkerLayout.setVisibility(View.VISIBLE);
        bottomSheetMarkerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void showBottomRouteSheet() {
        bottomSheetRouteLayout.setVisibility(View.VISIBLE);
        bottomSheetRouteBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (animateFirstTime)
            animateBottomSheetClosing(0);

        llBottomButtons.setVisibility(View.VISIBLE);
    }

    private void animateBottomSheetClosing(final int a) {
        animateFirstTime = false;
        if (a < 50) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomSheetRouteBehavior.setPeekHeight((int) LayoutUtils.dp2px(MapActivity.this, 200 - a));
                    animateBottomSheetClosing(a + 1);
                }
            }, 70);
        }
    }

    @Override
    public void onBackButtonClick() {
        searchBarView.setVisibility(View.VISIBLE);
        llBottomButtons.setVisibility(View.GONE);
        bottomSheetRouteLayout.setVisibility(View.GONE);
        if (searchBarView.getPoint() == null) {
            bottomSheetMarkerLayout.setVisibility(View.GONE);
        } else {
            bottomSheetMarkerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        beMapFragment.reset();
        isRoutePlotted = false;
        if (multiSearchBarView.hasCurrentLocation()) {
            App.getInstance().clearInterMediatePoints();
        } else {
            App.getInstance().clearWayPoints();
        }

    }

    @Override
    public void resetRouteRequest() {
        Log.d(TAG, "resetRouteRequest");
        beMapFragment.reset();
        App.getInstance().clearInterMediatePoints();
        showRoute(App.getInstance().getWayPoint(0),App.getInstance().getWayPoint(1),null);
    }


    private void startSearchActivityForResult(int requestCode) {
        Intent intent = new Intent(MapActivity.this, SearchActivity.class);
        intent.putExtra("position", requestCode);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onStopAddButtonClick(int position) {
        Log.d(TAG, "onSearchClick----" + position);
        startSearchActivityForResult(position);
    }

    @Override
    public void onRouteDone(String startLoc, String interStop, String lastLoc) {
        Log.d(TAG, "getWayPointCount----" + App.getInstance().getWayPointCount());
        beMapFragment.reset();
        multiSearchBarView.hide();
        selectedRouteView.show(startLoc, interStop, lastLoc);
        List<BEPoint> wa = new ArrayList<>();
        wa.add(App.getInstance().getWayPoint(1));
        showRoute(App.getInstance().getWayPoint(0),App.getInstance().getWayPoint(2),wa);

    }

    @Override
    public void onCurrentLocation(BEPoint point) {
        App.getInstance().addToWayPoint(point, 0);
        multiSearchBarView.setCurrentLocation(point);
    }

    @Override
    public void onFloorClick(int position) {
        beMapFragment.showRouteOnFloor(floorModelList.get(position).getFloorId());
    }

    @Override
    public void onReselectRoute() {
        multiSearchBarView.visible();
        selectedRouteView.hide();
    }

    @Override
    public void onBackClick() {
        selectedRouteView.hide();
        searchBarView.setVisibility(View.VISIBLE);
        llBottomButtons.setVisibility(View.GONE);
        bottomSheetRouteLayout.setVisibility(View.GONE);
        if (searchBarView.getPoint() == null) {
            bottomSheetMarkerLayout.setVisibility(View.GONE);
        } else {
            bottomSheetMarkerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onNavigationEnd() {
        Log.d(TAG, "onNavigationEnd----");
        searchBarView.setVisibility(View.VISIBLE);
        closeBottomMarkerSheet();
        isRoutePlotted = false;
        multiSearchBarView.clearAndHide();
    }

    @Override
    public void onPreviewEnd() {
        showBottomRouteSheet();

        if(App.getInstance().getWayPointCount() > 2){
            selectedRouteView.visible();
        }else {
            multiSearchBarView.visible();
        }
        if(floorModelList != null){
            beMapFragment.showRouteOnFloor(floorModelList.get(0).getFloorId());
            if (floorModelList.size() > 1) {
                multiFloorAdapter.setPosition(0);
            }

        }

        Log.d(TAG, "onPreviewEnd----");
    }
}
