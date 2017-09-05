package com.iranplanner.tourism.iranplanner.ui.fragment.itinerarySearch;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iranplanner.tourism.iranplanner.R;

import com.iranplanner.tourism.iranplanner.di.model.App;
import com.iranplanner.tourism.iranplanner.standard.StandardFragment;
import com.iranplanner.tourism.iranplanner.ui.fragment.itineraryList.ItineraryListFragment;
import com.ramotion.foldingcell.FoldingCell;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import autoComplet.MyFilterableAdapterAttraction;
import autoComplet.MyFilterableAdapterCity;
import autoComplet.MyFilterableAdapterProvince;
import autoComplet.ReadJsonAttraction;
import autoComplet.ReadJsonProvince;
import autoComplet.readJsonCity;
import entity.Attraction;
import entity.City;
import entity.Province;
import entity.ResultItinerary;
import entity.ResultItineraryList;
import tools.Util;


public class MainSearchFragment extends StandardFragment implements MainSearchContract.View, View.OnClickListener/*, Callback<ResultItineraryList>*/ {
    @Inject
    MainSearchPresenter mainPresenter;

    FoldingCell fcProvince, folding_cell_City_City, folding_cell_city, folding_cell_attraction;
    List<Province> provinces;
    String provinceName;
    String provinceId = null;
    AutoCompleteTextView textProvience;
    ProgressDialog progressDialog;
    Button searchOk_provience;
    List<Province> tempProvince;
    List<City> tempCity1, tempCity2, tempcity;
    AutoCompleteTextView fromCity_city, endCity_city, fromCity, fromCity_attraction, endAttraction;
    List<City> cities;
    Button searchOk_city_city, searchOk_city, searchOk_attraction;
    String cityCityFrom, cityEnd, cityFrom, cityFromAttraction;
    boolean checkfragment = false;
    LinearLayout city_city_layout, city_layout, province_layout, events_layout;
    private final static String TAG_FRAGMENT = "TAG_FRAGMENT";
    ItineraryListFragment itineraryListFragment;
    List<Attraction> attractions;
    List<City> tempAttractionCity;
    List<Attraction> tempAttraction;
    String attractionEnd;
    Button mmActivity;
    FrameLayout cell_title_view_theme, cell_title_view_events;

    public static MainSearchFragment newInstance() {
        MainSearchFragment fragment = new MainSearchFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        View view = inflater.inflate(R.layout.search_main_fragment, container, false);
        city_city_layout = (LinearLayout) view.findViewById(R.id.city_city_layout);
        city_layout = (LinearLayout) view.findViewById(R.id.city_layout);
        province_layout = (LinearLayout) view.findViewById(R.id.province_layout);
//        events_layout = (LinearLayout) view.findViewById(R.id.events_layout);
//        cell_title_view_events = (FrameLayout) view.findViewById(R.id.cell_title_view_events);
//        cell_title_view_theme = (FrameLayout) view.findViewById(R.id.cell_title_view_theme);
        //province
        textProvience = (AutoCompleteTextView) view.findViewById(R.id.textProvience);
        searchOk_provience = (Button) view.findViewById(R.id.searchOk_provience);
        tempProvince = autoCompleteProvince(textProvience);
        fcProvince = (FoldingCell) view.findViewById(R.id.folding_cell_province);
        // -------------attach click listener to folding cell
        fcProvince.setOnClickListener(this);
        searchOk_provience.setOnClickListener(this);
        //attraction
        folding_cell_attraction = (FoldingCell) view.findViewById(R.id.folding_cell_attraction);
        fromCity_attraction = (AutoCompleteTextView) view.findViewById(R.id.fromCity_attraction);
        endAttraction = (AutoCompleteTextView) view.findViewById(R.id.endAttraction);
        searchOk_attraction = (Button) view.findViewById(R.id.searchOk_attraction);
        tempAttractionCity = autoCompleteCity(fromCity_attraction);
        tempAttraction = autoCompleteAttraction(endAttraction);
        folding_cell_attraction.setOnClickListener(this);
        searchOk_attraction.setOnClickListener(this);
        // city_city
        fromCity_city = (AutoCompleteTextView) view.findViewById(R.id.fromCity_city);
        endCity_city = (AutoCompleteTextView) view.findViewById(R.id.endCity_city);
        searchOk_city_city = (Button) view.findViewById(R.id.searchOk_city_city);
        fromCity_city.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        tempCity1 = autoCompleteCity(fromCity_city);
        tempCity2 = autoCompleteCity(endCity_city);
        folding_cell_City_City = (FoldingCell) view.findViewById(R.id.folding_cell_City_City);
        // -------------attach click listener to folding cell
        folding_cell_City_City.setOnClickListener(this);
        searchOk_city_city.setOnClickListener(this);


        //city
        fromCity = (AutoCompleteTextView) view.findViewById(R.id.fromCity);
        searchOk_city = (Button) view.findViewById(R.id.searchOk_city);
        tempcity = autoCompleteCity(fromCity);
        folding_cell_city = (FoldingCell) view.findViewById(R.id.folding_cell_city);

        // -------------attach click listener to folding cell
        folding_cell_city.setOnClickListener(this);
        searchOk_city.setOnClickListener(this);
//        cell_title_view_events.setOnClickListener(this);
//        cell_title_view_theme.setOnClickListener(this);
        return view;
    }

    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("GPS شما فعال نیست. آیا تمایل به روشن کردن آن دارید")
                .setCancelable(false)
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {

        DaggerMainScreenComponent.builder()
                .netComponent(((App) getActivity().getApplicationContext()).getNetComponent())
                .mainSearchnModule(new MainSearchnModule(this))
                .build().injectionMainSearchFragment(this);

        switch (v.getId()) {
//            case R.id.findMyLocation:
//                Intent intentMapActivity =new Intent(getContext(), MoreItemItineraryActivity.class);
//                startActivity(intentMapActivity);
//                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//                LocationListener locationListener = new LocationListinerGps(getContext());
//
//                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    buildAlertMessageNoGps();
//                }
//                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                }
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
//                break;
            case R.id.folding_cell_province:
                fcProvince.toggle(false);
                folding_cell_City_City.fold(false);
                folding_cell_attraction.fold(false);
                folding_cell_city.fold(false);
                break;

            case R.id.folding_cell_City_City:
                folding_cell_City_City.toggle(false);
                fcProvince.fold(false);
                folding_cell_attraction.fold(false);
                folding_cell_city.fold(false);
                break;

            case R.id.folding_cell_attraction:
                folding_cell_attraction.toggle(false);
                fcProvince.fold(false);
                folding_cell_City_City.fold(false);
                folding_cell_city.fold(false);
                break;

            case R.id.searchOk_attraction:
                hideKeyBoard();
                showAttraction();
                break;

            case R.id.searchOk_city:
                hideKeyBoard();
                showCity();
                break;

            case R.id.folding_cell_city:
                folding_cell_city.toggle(false);
                fcProvince.fold(false);
                folding_cell_City_City.fold(false);
                folding_cell_attraction.fold(false);
                break;

            case R.id.searchOk_city_city:
                hideKeyBoard();
                showCityCity();
                break;

            case R.id.searchOk_provience:
                hideKeyBoard();
                showProvience();
                break;
//            case R.id.cell_title_view_theme:
//                Toast.makeText(getContext(), "به زودی ", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.cell_title_view_events:
//                Toast.makeText(getContext(), "به زودی ", Toast.LENGTH_SHORT).show();
//                break;
        }
    }

    @Override
    public void showError(String message) {
        progressDialog.dismiss();
        if (message.contains("Unable to resolve host ") || message.contains("Software caused connection abort")) {
            Toast.makeText(getContext(), "عدم دسترسی به اینترنت", Toast.LENGTH_LONG).show();
        }
        if (message.contains("HTTP 400 BAD REQUEST")) {
            Toast.makeText(getContext(), "در این مسیر برنامه سفری یافت نشد", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showComplete() {
        progressDialog.dismiss();
    }

    @Override
    public void showProgress() {
        progressDialog = Util.showProgressDialog(getContext(), "لطفا منتظر بمانید", getActivity());
    }

    @Override
    public void dismissProgress() {
        Util.dismissProgress(progressDialog);
    }

    public class GPSTracker extends Service implements LocationListener {

        private final Context mContext;

        // Flag for GPS status
        boolean isGPSEnabled = false;

        // Flag for network status
        boolean isNetworkEnabled = false;

        // Flag for GPS status
        boolean canGetLocation = false;

        Location location; // Location

        double latitude; // Latitude
        double longitude; // Longitude
        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        public GPSTracker(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

                // Getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // Getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // No network provider is enabled
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return null;
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // If GPS enabled, get latitude/longitude using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app.
         */
        public void stopUsingGPS() {
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.removeUpdates(GPSTracker.this);
            }
        }


        /**
         * Function to get latitude
         */
        public double getLatitude() {
            if (location != null) {
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }


        /**
         * Function to get longitude
         */
        public double getLongitude() {
            if (location != null) {
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }


        /**
         * Function to check GPS/Wi-Fi enabled
         *
         * @return boolean
         */
        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        /**
         * Function to show settings alert dialog.
         * On pressing the Settings button it will launch Settings Options.
         */
        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing the Settings button.
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // On pressing the cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }


        @Override
        public void onLocationChanged(Location location) {
        }


        @Override
        public void onProviderDisabled(String provider) {
        }


        @Override
        public void onProviderEnabled(String provider) {
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }


        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }


    }

    //province method
    public List<Province> autoCompleteProvince(AutoCompleteTextView textProvience) {
        ReadJsonProvince readJsonProvince = new ReadJsonProvince();
        provinces = readJsonProvince.getListOfProvience(getContext());
        MyFilterableAdapterProvince adapter = new MyFilterableAdapterProvince(getContext(), android.R.layout.simple_list_item_1, provinces);
        textProvience.setAdapter(adapter);
        return provinces;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("لطفا منتظر بمانید");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public String returnProvinceId(AutoCompleteTextView textView, List<Province> provinceList) {

        for (Province p : provinceList) {
            Log.e("Province", p.getProvinceName());
            if (p.getProvinceName().equals(textView.getText().toString())) {
                provinceId = p.getProvinceId();
                showProgressDialog();
            }
        }
        return provinceId;
    }

    //-------------------city_city
    public List<City> autoCompleteCity(AutoCompleteTextView city) {
        readJsonCity readJsonCity = new readJsonCity();
        cities = readJsonCity.getListOfCity(getContext());
        MyFilterableAdapterCity adapter = new MyFilterableAdapterCity(getContext(), android.R.layout.simple_list_item_1, cities);
        city.setAdapter(adapter);
        return cities;
    }

    public String returnCityId(AutoCompleteTextView textView, List<City> cityList) {
        String cityId = null;
        for (City c : cityList) {
            Log.e("city", c.getCityName());
            if (c.getCityName().equals(textView.getText().toString())) {
                cityId = c.getCityId();
            }
        }
        return cityId;
    }

    public List<Attraction> autoCompleteAttraction(AutoCompleteTextView city) {
        ReadJsonAttraction readJsonAttraction = new ReadJsonAttraction();
        attractions = readJsonAttraction.getListOfAttractin(getContext());
        MyFilterableAdapterAttraction adapter = new MyFilterableAdapterAttraction(getContext(), android.R.layout.simple_list_item_1, attractions);
        city.setAdapter(adapter);
        return attractions;
    }

    public String returnAttractionId(AutoCompleteTextView textView, List<Attraction> attractionList) {
        String attractionId = null;
        for (Attraction attraction : attractionList) {
            Log.e("city", attraction.getAttractionName());
            if (attraction.getAttractionName().equals(textView.getText().toString())) {
                attractionId = attraction.getAttractionId();
            }
        }
        return attractionId;
    }

    @Override
    public boolean onBackPressed() {

        return super.onBackPressed();
    }


    @Override
    public void showItineraries(ResultItineraryList resultItineraryList, String typeOfSearch) {
        List<ResultItinerary> data = resultItineraryList.getResultItinerary();
        itineraryListFragment = new ItineraryListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("resuliItineraryList", (Serializable) data);
        bundle.putString("fromWhere", typeOfSearch);
        bundle.putString("nextOffset", resultItineraryList.getStatistics().getOffsetNext().toString());
        bundle.putString("provinceId", provinceId);
        bundle.putString("attractionId", attractionEnd);
        if (typeOfSearch.equals("fromCityToCity")) {
            bundle.putString("endCity", cityEnd);
        } else {
            bundle.putString("endCity", "");
        }
        itineraryListFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, itineraryListFragment);
        ft.addToBackStack(null);
        ft.commit();
        cityEnd = "";
        checkfragment = true;
        progressDialog.dismiss();
    }


    //---------------------------
    private void showAttraction() {
        folding_cell_attraction.fold(false);
        cityFromAttraction = returnCityId(fromCity_attraction, tempAttractionCity);
        attractionEnd = returnAttractionId(endAttraction, tempAttraction);
        if (cityFromAttraction != null && attractionEnd != null) {
            String offset = "0";
            mainPresenter.loadItineraryFromAttraction("searchattractioncity", "fa", cityFromAttraction, "10", offset, attractionEnd, Util.getTokenFromSharedPreferences(getContext()), Util.getAndroidIdFromSharedPreferences(getContext()));
            showProgressDialog();
        } else {
            Toast.makeText(getActivity(), "نام شهر یا جاذبه ثبت نشده است", Toast.LENGTH_SHORT).show();
        }
        Log.d("search ok clicked", "true");
    }

    private void showCity() {
        folding_cell_city.fold(false);
        cityFrom = returnCityId(fromCity, tempcity);
        cityEnd = cityFrom;
        if (cityFrom != null) {
            String offset = "0";
            mainPresenter.loadItineraryFromCity("list", "fa", cityFrom, "20", offset, cityEnd, Util.getTokenFromSharedPreferences(getContext()), Util.getAndroidIdFromSharedPreferences(getContext()));
            showProgressDialog();
        } else {
            Toast.makeText(getActivity(), "لطفا نام شهر را اصلاح کنید", Toast.LENGTH_SHORT).show();
        }
        Log.d("search ok clicked", "true");
    }

    private void showCityCity() {
        folding_cell_City_City.fold(false);

        cityCityFrom = returnCityId(fromCity_city, tempCity1);
        cityEnd = returnCityId(endCity_city, tempCity2);
        if (endCity_city.getText() == null) {
            cityEnd = "";
        }
        if (cityCityFrom != null) {
//                  getItinerary(cityCityFrom, "0", false, cityEnd);
            String offset = "0";
            mainPresenter.loadItineraryFromCity("list", "fa", cityCityFrom, "20", offset, cityEnd, Util.getTokenFromSharedPreferences(getContext()), Util.getAndroidIdFromSharedPreferences(getContext()));
            showProgressDialog();

        } else {
            Toast.makeText(getActivity(), "لطفا نام شهر را اصلاح کنید", Toast.LENGTH_SHORT).show();
        }
        Log.d("search ok clicked", "true");
    }

    private void showProvience() {
        fcProvince.fold(false);
        provinceName = returnProvinceId(textProvience, tempProvince);
        if (provinceName != null) {
//                    getItinerary(provinceName, "0");
            String offset = "0";
            mainPresenter.loadItineraryFromProvince("searchprovince", provinceName, offset, Util.getTokenFromSharedPreferences(getContext()), Util.getAndroidIdFromSharedPreferences(getContext()));
        } else {
            Toast.makeText(getActivity(), "لطفا نام استان را اصلاح کنید ", Toast.LENGTH_SHORT).show();
        }
        Log.d("search ok clicked", "true");
    }

    //------------------------------
    private void hideKeyBoard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
