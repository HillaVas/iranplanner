package com.iranplanner.tourism.iranplanner.ui.fragment.pandaMap;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.iranplanner.tourism.iranplanner.R;
import com.iranplanner.tourism.iranplanner.RecyclerItemOnClickListener;
import com.iranplanner.tourism.iranplanner.di.model.App;
import com.iranplanner.tourism.iranplanner.standard.StandardFragment;
import com.iranplanner.tourism.iranplanner.ui.activity.MapWrapperLayout;
import com.iranplanner.tourism.iranplanner.ui.activity.MySupportMapFragmen;
import com.iranplanner.tourism.iranplanner.ui.activity.filterMap.FilterMap;
import com.iranplanner.tourism.iranplanner.ui.activity.reservationHotelList.ReseveHotelListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import autoComplet.MyFilterableAdapterProvince;
import autoComplet.ReadJsonProvince;
import butterknife.BindView;
import butterknife.ButterKnife;
import entity.PandaMapList;
import entity.Province;
import entity.ResultLodging;
import entity.ResultPandaMap;
import entity.ResultPandaMaps;
import tools.Util;

/**
 * Created by h.vahidimehr on 11/11/2017.
 */

public class MapPandaFragment extends StandardFragment implements OnMapReadyCallback, MapWrapperLayout.OnDragListener, MapPandaPresenter.View/*, GoogleMap.OnMarkerClickListener*/ {
    @Inject
    MapPandaPresenter mapPandaPresenter;

    private GoogleMap mMap;
    ProgressDialog progressBar;
    LatLng Iran;
    @BindView(R.id.reservationListRecyclerView)
    RecyclerView recyclerView;
    MySupportMapFragmen mapFragment;
    Projection projection;
    public double latitude;
    public double longitude;
    private boolean drawing = false;
    private Polygon polygon;
    private List<LatLng> PolylinePoints;
    List<LatLng> markerPoints;
    Polyline polyline;
    boolean flagUp = false;
    Boolean Is_MAP_Moveable = false; // to detect map is movable
    PandaAdapter adapter;
    private boolean screenLeave = false;
    int source = 0;
    int destination = 1;
    private List<ResultPandaMap> resultPandaMapList;
    private List<String> markerNames;
    LinearLayoutManager horizontalLayoutManagaer;
    private boolean isResultForDraw = false;
    private Button btnFilter;
    private AutoCompleteTextView search;

    public static MapPandaFragment newInstance() {
        MapPandaFragment fragment = new MapPandaFragment();
        return fragment;
    }

    boolean setDraw = true;

    private void setUpRecyclerView(List<ResultPandaMap> resultPandaMapList) {

        recyclerView.setHasFixedSize(true);

        horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);

        adapter = new PandaAdapter(getActivity(), resultPandaMapList, getContext());
        recyclerView.setAdapter(adapter);
        final SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    View centerView = snapHelper.findSnapView(horizontalLayoutManagaer);
                    int position = horizontalLayoutManagaer.getPosition(centerView);

//                    showMarkers(markerPoints);
                    if (markerNames.size() > 0) {
                        Marker marker = mMap.addMarker(new MarkerOptions().position(markerPoints.get(position))
                                .title(markerNames.get(position))/*.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_blue_pin))*/);
                        marker.showInfoWindow();
                    }
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPoints.get(markerPosition)));
//                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
//                    mMap.animateCamera(zoom);
                }
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemOnClickListener(getContext(), new RecyclerItemOnClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                showMarkers(markerPoints);
                //open
                String offset = "0";

            }
        }));


    }

    private void cleanMapAndRecyclerView() {
        if (PolylinePoints.size() > 0) {
            PolylinePoints.clear();
            mMap.clear();
        }
        isResultForDraw = false;
        PolylinePoints.clear();
        markerPoints.clear();
        try {
            resultPandaMapList.clear();
            adapter.notifyDataSetChanged();

        } catch (Exception e) {

        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_panda, container, false);
        mapFragment = (MySupportMapFragmen) getChildFragmentManager()
                .findFragmentById(R.id.mapView);

        recyclerView = rootView.findViewById(R.id.reservationListRecyclerView);
        btnFilter = rootView.findViewById(R.id.btnFilter);
        search = rootView.findViewById(R.id.search);
        search.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mapPandaPresenter.getPandaSearch("pandaautocomplete","عار");

            }
        });
//        autoCompleteProvince(search);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog mBottomSheetDialog = new Dialog(getActivity(), R.style.MaterialDialogSheet);
                mBottomSheetDialog.setContentView(R.layout.fragment_panda_filter); // your custom view.
                mBottomSheetDialog.setCancelable(true);
                mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
                mBottomSheetDialog.show();
            }
        });
        Button btn = rootView.findViewById(R.id.btnSelectPolygon);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDrawable(setDraw);
                cleanMapAndRecyclerView();

            }
        });
        mapFragment.getMapAsync(this);
        mapFragment.setOnDragListener(this);
        ButterKnife.bind(this, rootView);

        return rootView;

    }





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//         Obtain the SupportMapFragment and get notified when the map is ready to be used.

        List<LatLng> nn = new ArrayList<>();
        nn.add(Iran);
        Iran = new LatLng(35.6887931, 51.3891646);
        nn.add(Iran);
        markerPoints = nn;
        PolylinePoints = new ArrayList<LatLng>();

        DaggerMapPandaComponent.builder().netComponent(((App) getContext().getApplicationContext()).getNetComponent())
                .mapPandaModule(new MapPandaModule(this))
                .build().inject(this);
        markerNames = new ArrayList<>();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.getUiSettings().setScrollGesturesEnabled(false);
        setDrawable(setDraw);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Iran, 10.0f));
    }

    private void setDrawable(boolean drawable) {
        if (drawable) {
            mMap.getUiSettings().setScrollGesturesEnabled(drawable);
            setDraw = false;

        } else {
            mMap.getUiSettings().setScrollGesturesEnabled(drawable);
            setDraw = true;
        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            int markerPosition;

            @Override
            public boolean onMarkerClick(Marker marker) {
                int c = 0;
                for (ResultPandaMap resultLodging : resultPandaMapList) {
                    if (resultLodging.getPoint().getTitle().equals(marker.getTitle())) {
                        showMarkers(markerPoints);
                        mMap.addMarker(new MarkerOptions().position(markerPoints.get(c))
                                .title(markerNames.get(c))/*.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_blue_pin))*/);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPoints.get(c)));
                        Log.e("marker", marker.getTitle());
                        markerPosition = c;
                    }
                    c++;
                }
                recyclerView.getLayoutManager().scrollToPosition(markerPosition);

                return false;
            }
        });

    }

    private void draw_final_polygon() {

        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(PolylinePoints);
        polygonOptions.strokeWidth(8);
        polygonOptions.fillColor(ContextCompat.getColor(getContext(), R.color.map));
        polygon = mMap.addPolygon(polygonOptions);
        PandaMapList PandaMapList = new PandaMapList(polygon.getPoints());

//                mapPandaPresenter.getPandaSearch("pandaautocomplete","غار");
        mapPandaPresenter.getDrawResult(PandaMapList, Util.getTokenFromSharedPreferences(getContext()), Util.getAndroidIdFromSharedPreferences(getContext()));

    }

    public void Draw_Map() {


//        specify latitude and longitude of both source and destination Polyline

        if (PolylinePoints.size() > 1) {
            polyline = mMap.addPolyline(new PolylineOptions().add(PolylinePoints.get(source), PolylinePoints.get(destination)).width(8));
            source++;
            destination++;
        }


    }

    private void showMarkers(List<LatLng> draw) {
        int index = 0;
        for (LatLng latLng : draw) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(markerNames.get(index));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Iran));
            Marker marker = mMap.addMarker(markerOptions);
            index++;
        }

    }


    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (setDraw && !isResultForDraw) {
            Log.d("ON_DRAG", String.format("ME: %s", motionEvent));
            Log.d("ON_DRAG", String.format("ME: %s", motionEvent));
            // Handle motion event:

            Log.i("ON_DRAG", "X:" + String.valueOf(motionEvent.getX()));
            Log.i("ON_DRAG", "Y:" + String.valueOf(motionEvent.getY()));

            float x = motionEvent.getX();
            float y = motionEvent.getY();

            int x_co = Integer.parseInt(String.valueOf(Math.round(x)));
            int y_co = Integer.parseInt(String.valueOf(Math.round(y)));

            projection = mMap.getProjection();
            Point x_y_points = new Point(x_co, y_co);
            LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
            latitude = latLng.latitude;
            longitude = latLng.longitude;

            Log.i("ON_DRAG", "lat:" + latitude);
            Log.i("ON_DRAG", "long:" + longitude);
            LatLng point = new LatLng(latitude, longitude);

            int eventaction = motionEvent.getAction();
            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
//                    if (PolylinePoints.size() > 0) {
//                        PolylinePoints.clear();
//                        mMap.clear();
//                    }

                    // finger touches the screen
                    screenLeave = false;
//                            System.out.println("ACTION_DOWN");

//                            val.add(new LatLng(latitude, longitude));

                case MotionEvent.ACTION_MOVE:
                    // finger moves on the screen
//                            System.out.println("ACTION_MOVE");
                          /*  if (val.size()==3){
                                val.remove(1);
                            }*/

                    PolylinePoints.add(new LatLng(latitude, longitude));
                    screenLeave = false;
                    Draw_Map();


                case MotionEvent.ACTION_UP:

//                            System.out.println("ACTION_UP");
                    if (!screenLeave) {
                        screenLeave = true;
                    } else {
                        System.out.println("ACTION_UP ELSE CAse");
                        Is_MAP_Moveable = false; // to detect map is movable
                        source = 0;
                        destination = 1;
                        draw_final_polygon();
                    }


                    // finger leaves the screen
//                            Is_MAP_Moveable = false; // to detect map is movable
//                            Draw_Map();
                    break;
                default:
                    break;
            }
        } else if (setDraw && isResultForDraw) {
            setDrawable(true);
        }


    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showComplete() {

    }

    @Override
    public void showProgress() {
        progressBar = Util.showProgressDialog(getContext(), "لطفا منتظر بمانید", getActivity());

    }

    @Override
    public void dismissProgress() {
        Util.dismissProgress(progressBar);

    }

    @Override
    public void showPointOnMap(ResultPandaMaps resultPandaMaps) {
        resultPandaMapList = resultPandaMaps.getResultPandaMap();
        markerPoints.clear();
        if (resultPandaMapList.size() > 0) {
            for (ResultPandaMap resultPandaMap : resultPandaMapList) {
                LatLng point = new LatLng(Double.valueOf(resultPandaMap.getPoint().getLatitude()), Double.valueOf(resultPandaMap.getPoint().getLongitude()));
                markerPoints.add(point);
                markerNames.add(resultPandaMap.getPoint().getTitle());

            }
        }
        isResultForDraw = true;
        showMarkers(markerPoints);
        setUpRecyclerView(resultPandaMapList);

    }

//    int position = -1;

//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        int c = 0;
//        for (ResultPandaMap resultLodging : resultPandaMapList) {
//            if (resultLodging.equals(marker.getTitle())) {
//                showMarkers(markerPoints);
//                mMap.addMarker(new MarkerOptions().position(markerPoints.get(c))
//                        .title(markerNames.get(c)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_blue_pin)));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPoints.get(c)));
//                Log.e("marker", marker.getTitle());
//                position = c;
//            }
//            c++;
//        }
//        recyclerView.getLayoutManager().scrollToPosition(position);
//        return false;
//    }

}