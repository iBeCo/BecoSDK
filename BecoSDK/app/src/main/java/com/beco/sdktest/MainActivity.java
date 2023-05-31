package com.beco.sdktest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.beco.sdk.map.OnReRouteClickListener;
import com.beco.sdk.model.BEPoint;
import com.beco.sdk.model.BESite;
import com.beco.sdktest.search.SearchDialog;
import com.beco.sdktest.widget.BESearchBarView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.altbeacon.beacon.BeaconManager;

import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnPointClickListener,
        OnCurrentLocationChangeListener, OnNavigationListener, OnReRouteClickListener,
        View.OnClickListener, SearchDialog.OnLocationSelected, BESearchBarView.SearchBarListener {


    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BEMapFragment beMapFragment;
    private BESearchBarView searchBarView;
    private BeCo sdk;
    private List<BESite> beSiteList;
    private int[] iconColorConfig;
    //CardView searchView;
    ConstraintLayout bottomSheet;
    ConstraintLayout directionView;
    //EditText searchText;
    AppCompatTextView pointName;
    AppCompatEditText toText;
    AppCompatEditText fromText;
    TextView floorDescription;
    Button cancel;
    Button goButton;
    Button startButton;
    Button startPreviewButton;
    BEPoint source;
    BEPoint destination;
    Group startCancel;
    //ImageView closeIcon;
    Group multiFloorGroup;
    AppCompatImageView backButton;
    private boolean isRoutePlotted;
    List<BERouteFloor> routeFloors;
    TextView firstFloor;
    TextView secondFloor;
    TextView thirdFloor;
    String usageToken;
    String licenseKey = "d5a1c252-c09d-4931-baf2-7b6fdff7520b";


    private BottomSheetBehavior sheetBehavior;
    private boolean isSearch = true;
    private int currentFloor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        beMapFragment = (BEMapFragment) findViewById(R.id.beMap);
        //searchView = (CardView) findViewById(R.id.becoSearchBarCardView);
        searchBarView = (BESearchBarView) findViewById(R.id.beCoSearchBar);
        directionView = (ConstraintLayout) findViewById(R.id.direction_view);
        bottomSheet = (ConstraintLayout) findViewById(R.id.bottom_sheet);
        //searchText = (EditText) findViewById(R.id.becoSearchBarEditText);
        pointName = (AppCompatTextView) findViewById(R.id.location_name);

        floorDescription = (TextView) findViewById(R.id.floor);
        //closeIcon = (ImageView) findViewById(R.id.becoSearchBarCloseButton);
        firstFloor = (TextView) findViewById(R.id.floor_zero);
        secondFloor = (TextView) findViewById(R.id.floor_one);
        thirdFloor = (TextView) findViewById(R.id.floor_two);

        cancel = (Button) findViewById(R.id.cancel);
        startButton = (Button) findViewById(R.id.start);
        startPreviewButton = (Button) findViewById(R.id.show_preview);
        startCancel = (Group) findViewById(R.id.start_cancel);
        multiFloorGroup = (Group) findViewById(R.id.multi_floor_group);
        goButton = (Button) findViewById(R.id.go_button);
        backButton = (AppCompatImageView) findViewById(R.id.back_button);
        toText = (AppCompatEditText) findViewById(R.id.toText);
        fromText = (AppCompatEditText) findViewById(R.id.fromText);


        getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
                Window.PROGRESS_VISIBILITY_ON);
        //searchText.setOnClickListener(this);
        usageToken = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("pref_api_host_key","");

        if(usageToken.equals("")){
            Log.d(TAG,"empty key setting the default key");
            usageToken = "db3ebfe2e039f56059e7ed069ee09ff4bc6e5e35";
        }


        verifyBluetooth();


        checkPermission();
        bottomSheet.setVisibility(View.GONE);

        cancel.setOnClickListener(this);
        //closeIcon.setOnClickListener(this);
        firstFloor.setOnClickListener(this);
        secondFloor.setOnClickListener(this);
        thirdFloor.setOnClickListener(this);
        goButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        fromText.setOnClickListener(this);
        startButton.setOnClickListener(this);
        startPreviewButton.setOnClickListener(this);
        searchBarView.setListener(this);
    }


    private void checkPermission(){
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
            }else {
                beMapFragment.getMapAsync(this,this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "coarse location permission granted");
                beMapFragment.getMapAsync(this,this);
            } else {
                beMapFragment.getMapAsync(this,this);
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
    private void navigate(){
        if(source != null && destination != null){
            try {
                routeFloors = beMapFragment.getRoute(source,destination,null);
                if(routeFloors != null){
                    currentFloor = 0;
                    firstFloor.setText(routeFloors.get(0).getLabel());
                    if(routeFloors.size() > 1){
                        secondFloor.setText(routeFloors.get(1).getLabel());
                    }
                    if(routeFloors.size() > 2){
                        thirdFloor.setText(routeFloors.get(2).getLabel());
                    }
                    beMapFragment.showRouteOnFloor(routeFloors.get(0).getFloorId());
                    isRoutePlotted =  true;
                    showRouteHelper();
                }

            } catch (InvalidRouteRequestException e) {
                e.printStackTrace();
            }
        }
    }

    void showSearchView() {
        //searchText.setPoint("");
        //closeIcon.setVisibility(View.INVISIBLE);
        //searchView.setVisibility(View.VISIBLE);
        directionView.setVisibility(View.GONE);
        bottomSheet.setVisibility(View.GONE);
        isSearch = true;
        //destination = null;
        //source =  null;
        beMapFragment.reset();
        toText.setText("");
        //fromText.setPoint("");
        isRoutePlotted = false;
    }

    private void showDirections() {
        //searchView.setVisibility(View.GONE);
        directionView.setVisibility(View.VISIBLE);
        bottomSheet.setVisibility(View.GONE);
        if(source != null){
            if(destination != null && (source.getId() == destination.getId())){
                showErrorDialog("source and destination cannot be the same");
            }else {
                fromText.setText(source.getName());
                navigate();
            }
        }else {
            fromText.setHint("Select Source");
        }
        toText.setText(destination.getName());
    }

    private void showRouteHelper(){
        bottomSheet.setVisibility(View.VISIBLE);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetBehavior.setHideable(false);
        isSearch = false;
        startCancel.setVisibility(View.VISIBLE);
        if(routeFloors.size() > 1){
            multiFloorGroup.setVisibility(View.VISIBLE);
            secondFloor.setBackground(getResources().getDrawable(R.drawable.background_round_grey));
            thirdFloor.setBackground(getResources().getDrawable(R.drawable.background_round_grey));
            firstFloor.setBackground(getResources().getDrawable(R.drawable.background_rounded_blue));
            firstFloor.setTextColor(Color.WHITE);
            secondFloor.setTextColor(Color.BLACK);
            thirdFloor.setTextColor(Color.BLACK);
        }else {
            multiFloorGroup.setVisibility(View.GONE);
        }

        goButton.setVisibility(View.GONE);
    }

    public void showBottomSheet(BEPoint point){
        searchBarView.setPoint(point);
        //closeIcon.setVisibility(View.VISIBLE);
        goButton.setVisibility(View.VISIBLE);
        bottomSheet.setVisibility(View.VISIBLE);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetBehavior.setHideable(false);
        pointName.setText(point.getName());
        floorDescription.setText(point.getFloorDescription());
        toText.setText(point.getName());
        destination = point;
        startCancel.setVisibility(View.GONE);
        multiFloorGroup.setVisibility(View.GONE);

    }
    @Override
    public void onMapLoaded() {
        //Init your views here
        Log.d(TAG,"onMapLoaded");
    }

    @Override
    public void onMapReady() {
        Log.d(TAG,"onMapReady");
        initSDK();
    }

    @Override
    public void onMapError(BEErrorCode code) {
        Log.d(TAG,"onMapError");
    }

    @Override
    public void onNavigationEnd() {
        showSearchView();
    }

    @Override
    public void onPreviewEnd() {
        Log.d(TAG,"Preview Ended");
        bottomSheet.setVisibility(View.VISIBLE);
        directionView.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy in Main activity");
        if(beMapFragment != null){
            beMapFragment.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onPointClick(BEPoint point) {
        if(!isRoutePlotted){
            showBottomSheet(point);
        }

    }

    @Override
    public void onClick(View view) {

        SearchDialog dialog;
        FragmentTransaction ft;
        Bundle bundle;

        switch (view.getId()) {
            case R.id.go_button:
                showDirections();
                break;
            case R.id.back_button:
            case R.id.cancel:
                beMapFragment.clearPoint();
                bottomSheet.setVisibility(View.GONE);
                showSearchView();
                break;
            case R.id.fromText:
                try {
                    dialog = new SearchDialog(beMapFragment.getPoints());
                    ft = getSupportFragmentManager().beginTransaction();
                    bundle = new Bundle();
                    bundle.putBoolean("isFrom", true);
                    dialog.setArguments(bundle);
                    dialog.show(ft, SearchDialog.class.getName());
                } catch (SiteNotSetException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.start:
                startNavigation();
                break;
            case R.id.show_preview:
                startPreview();
                break;

            case R.id.floor_zero:
                changeFloor(0);
                break;

            case R.id.floor_one:
                changeFloor(1);
                break;
            case R.id.floor_two:
                changeFloor(2);
                break;
            case R.id.close_icon:
                onCloseIconClick();
                break;
        }

    }

    private void startPreview() {
        bottomSheet.setVisibility(View.GONE);
        //searchView.setVisibility(View.GONE);
        directionView.setVisibility(View.GONE);
        beMapFragment.preview();
    }

    private void startNavigation(){
        try {
            beMapFragment.navigate();
            bottomSheet.setVisibility(View.GONE);
            //searchView.setVisibility(View.GONE);
            directionView.setVisibility(View.GONE);
        } catch (BeaconNotFoundException e) {
            Log.d(TAG,"error- No beacons on the current route");
        }
    }

    private void onCloseIconClick(){
        //searchText.setPoint("");
        bottomSheet.setVisibility(View.GONE);
        //closeIcon.setVisibility(View.INVISIBLE);
        destination = null;
        beMapFragment.clearPoint();
    }

    private void changeFloor(int i){
        if(currentFloor != i){

            if(i==0){
                firstFloor.setBackground(getResources().getDrawable(R.drawable.background_rounded_blue));
                firstFloor.setTextColor(Color.WHITE);

                secondFloor.setBackground(getResources().getDrawable(R.drawable.background_round_grey));
                secondFloor.setTextColor(Color.BLACK);

                thirdFloor.setBackground(getResources().getDrawable(R.drawable.background_round_grey));
                thirdFloor.setTextColor(Color.BLACK);
            }else if(i==1){
                secondFloor.setBackground(getResources().getDrawable(R.drawable.background_rounded_blue));
                secondFloor.setTextColor(Color.WHITE);


                firstFloor.setBackground(getResources().getDrawable(R.drawable.background_round_grey));
                firstFloor.setTextColor(Color.BLACK);

                thirdFloor.setBackground(getResources().getDrawable(R.drawable.background_round_grey));
                thirdFloor.setTextColor(Color.BLACK);

            }else if(i==2){
                thirdFloor.setBackground(getResources().getDrawable(R.drawable.background_rounded_blue));
                thirdFloor.setTextColor(Color.WHITE);


                firstFloor.setBackground(getResources().getDrawable(R.drawable.background_round_grey));
                firstFloor.setTextColor(Color.BLACK);

                secondFloor.setBackground(getResources().getDrawable(R.drawable.background_round_grey));
                secondFloor.setTextColor(Color.BLACK);

            }

            currentFloor = i;
            beMapFragment.showRouteOnFloor(routeFloors.get(currentFloor).getFloorId());
        }
    }

    private void initSDK(){
        sdk = BeCo.init();
        sdk.configure(this, new BeCoCredentialProvider(usageToken,licenseKey), new BEValidateCallback(){
            @Override
            public void onError(BEErrorCode errorCode) {
                Log.d(TAG,"error- The application is not authorized to use the Beco SDK Platform");
            }

            @Override
            public void onLoadFinished() {
                Log.d(TAG,"success");
                getSite();
            }
        });
    }

    private void getSite(){
        sdk.getSites(null, new BESiteCallback() {
            @Override
            public void onError(BEErrorCode status) {
                Log.d(TAG,"error");
            }

            @Override
            public void onLoadFinished(List<BESite> sites) {
                Log.d(TAG,"got sites");
                beSiteList = sites;
                Log.d(TAG, beSiteList.toString());
                initMap();
            }
        });
    }

    private void initMap(){
        beMapFragment.setSite(beSiteList.get(0));
        beMapFragment.setOnPointClickListener(this);
        beMapFragment.setOnNavigationListener(this);
        beMapFragment.setOnCurrentLocationChangeListener(this);
        beMapFragment.setOnReRouteClickListener(this);
    }

    @Override
    public void onLocationSelected(boolean isFrom, BEPoint point) {
        if (isFrom) {
            if(destination != null && (point.getId() == destination.getId())){
                showErrorDialog("source and destination cannot be the same");
            }else {
                fromText.setText(point.getName());
                source = point;
                navigate();
            }

        }else {
            beMapFragment.selectPoint(point);
            showBottomSheet(point);
        }
    }

    public void showErrorDialog(String message) {
        Bundle args = new Bundle();
        args.putString("Message", message);

        DialogFragment dialogFragment = (DialogFragment) Fragment.instantiate(this, MessageDialogFragment.class.getName(), args);
        dialogFragment.show(getSupportFragmentManager(), "message");
    }

    @Override
    public void onCurrentLocation(BEPoint point) {
        Log.d(TAG,point.getName());
        source = point;
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
        }
        catch (RuntimeException e) {
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
    public void onReRoute(BEPoint point) {
        Log.d(TAG,"onReRoute in Main activity"+point.getName());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to Re Route?");
        builder.setMessage("You have deviated from your route.");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showDirections();
                source = point;
            }
        });
        builder.setOnDismissListener(dialog -> {
            //finish();
            //System.exit(0);
        });
        builder.show();
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
    public void onCloseButtonClick(View view) {
        Log.d(TAG,"onCloseButtonClick");
    }

    @Override
    public void onNavigateButtonClick() {
        Log.d(TAG,"onNavigateButtonClick");
    }

    @Override
    public void onSearchClick(View view) {
        Log.d(TAG,"onSearchClick");
        try {
            SearchDialog dialog = new SearchDialog(beMapFragment.getPoints());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isFrom", false);
            dialog.setArguments(bundle);
            dialog.show(ft, SearchDialog.class.getName());

        } catch (SiteNotSetException e) {
            e.printStackTrace();
        }
    }
}
