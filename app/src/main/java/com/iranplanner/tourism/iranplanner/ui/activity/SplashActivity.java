package com.iranplanner.tourism.iranplanner.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.iranplanner.tourism.iranplanner.R;
import com.iranplanner.tourism.iranplanner.di.model.App;
import com.iranplanner.tourism.iranplanner.ui.activity.attractioListMore.AttractionListMorePresenter;
import com.iranplanner.tourism.iranplanner.ui.activity.hotelReservationListOfCity.ReservationContract;
import com.iranplanner.tourism.iranplanner.ui.activity.login.LoginActivity;
import com.iranplanner.tourism.iranplanner.ui.activity.reservationHotelList.ReservationHotelListPresenter;
import com.iranplanner.tourism.iranplanner.ui.fragment.home.DaggerHomeComponent;
import com.iranplanner.tourism.iranplanner.ui.fragment.home.HomeContract;
import com.iranplanner.tourism.iranplanner.ui.fragment.home.HomeModule;
import com.iranplanner.tourism.iranplanner.ui.fragment.home.HomePresenter;
import com.iranplanner.tourism.iranplanner.ui.fragment.itinerarySearch.MainSearchPresenter;

import javax.inject.Inject;

import entity.GetHomeResult;
import entity.ResultCommentList;
import entity.ResultEvent;
import entity.ResultEvents;
import entity.ResultItineraryList;
import entity.ResultLodgingHotel;
import entity.ResultLodgingList;
import entity.ShowAtractionDetailMore;
import entity.ShowAttractionListMore;
import entity.ShowAttractionMoreList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import server.Config;
import tools.CustomMessage;
import tools.Util;

public class SplashActivity extends StandardActivity implements MainSearchPresenter.View,HomeContract.View, ReservationContract.View, AttractionListMorePresenter.View, ReservationHotelListPresenter.View {
    Thread splashTread;
    @Inject
    HomePresenter homePresenter;

    private ImageView ivLogo, ivLogoType, ivLogoInner;
    private TextView tvInfo, tvWebSite;
    private View vLogoContainer, vLogoInfoContainer, vFellows;

    private UpdateReceiver receiver = new UpdateReceiver();

    private CustomMessage customMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Load Background Image
        Glide.with(this).load(R.drawable.splash_bg_blur).centerCrop().override(600, 400).into((ImageView) findViewById(R.id.splashBgIv));

        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    private void init() {
        ivLogo = (ImageView) findViewById(R.id.splashLogoIv);
        ivLogoType = (ImageView) findViewById(R.id.splashLogoTypeIv);
        ivLogoInner = (ImageView) findViewById(R.id.splashLogoInnerIv);

        tvInfo = (TextView) findViewById(R.id.splashInfoTv);
        tvWebSite = (TextView) findViewById(R.id.splashIranPlannerWebSiteTv);

        vLogoInfoContainer = findViewById(R.id.splashLogoInfoContainer);
        vLogoContainer = findViewById(R.id.splashLogoContainer);
        vFellows = findViewById(R.id.splashFellowView);

        //Move the ui Objects to their new Places and wait for Translation Animation
        vLogoContainer.setY(-(Util.dpToPx(this, 100)));
        vFellows.setY(Util.dpToPx(this, 40));

        //Reset View's Alpha to 0
        tvWebSite.setAlpha(0f);
        ivLogo.setAlpha(0f);
        vLogoContainer.setAlpha(0f);
        vLogoInfoContainer.setAlpha(0f);

        startAnimation();
    }

    private void startAnimation() {
        tvInfo.setVisibility(View.VISIBLE);
        ivLogoType.setVisibility(View.VISIBLE);

        //Downward Translation animations
        vLogoContainer.animate().alpha(1).translationYBy(Util.dpToPx(this, 100)).setStartDelay(500).setDuration(500).start();
        ivLogo.animate().alpha(1).setStartDelay(500).setDuration(500).start();

        //Separation Animations
        vLogoInfoContainer.animate().alpha(1).translationXBy(-Util.dpToPx(this, 75)).setStartDelay(1300).setDuration(500).start();
        vLogoContainer.animate().translationXBy(Util.dpToPx(this, 75)).setStartDelay(1300).setDuration(500).start();

        //Fellow Layout Upward Translation animations
        vFellows.animate().translationYBy(-(Util.dpToPx(this, 40))).setStartDelay(1700).start();

        //WebSite TextView Alpha Animation
        tvWebSite.animate().alpha(1).setStartDelay(1700).setDuration(400).start();

        //Rotation Animation Used For the Inner Part Of The Logo
        Animation rotation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        rotation.setStartOffset(1500);
        ivLogoInner.startAnimation(rotation);
    }

    private void proceed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getHomeResult("country", "311");
            }
        }, 2600);
    }

    private void getHomeResult(String destination, String selectId) {

        DaggerHomeComponent.builder().netComponent(((App) getApplicationContext().getApplicationContext()).getNetComponent())
                .homeModule(new HomeModule(this, this, this, this,this))
                .build().inject(this);
        String cid = Util.getTokenFromSharedPreferences(getApplicationContext());
        String andId = Util.getAndroidIdFromSharedPreferences(getApplicationContext());
        homePresenter.getHome("home", destination, selectId, cid, andId);
    }

    @Override
    public void ShowHomeResult(final GetHomeResult GetHomeResult) {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("HomeResult", GetHomeResult);
        startActivity(intent);
    }

    public class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
            if (isConnected)
                Log.e("NET", "connected  " + isConnected);
            else Log.e("NET", "not connected  " + isConnected);

            if (Util.isNetworkAvailable(context)) {
                if (customMessage != null)
                    customMessage.hide();
                proceed();
            } else
                showMessage();
        }
    }

    private void showMessage() {
        customMessage = new CustomMessage(this, "عدم دسترسی به اینترنت");
        customMessage.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void showLodgingList(ResultLodgingList resultLodgingList) {

    }

    @Override
    public void showComments(ResultCommentList resultCommentList) {

    }

    @Override
    public void sendCommentMessage(ResultCommentList resultCommentList) {

    }

    @Override
    public void showHotelReserveList(ResultLodgingHotel resultLodgingHotel) {

    }

    @Override
    public void showItineraries(ResultItineraryList resultItineraryList, String typeOfSearch) {

    }

    private void updateCId(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .baseUrl(Config.BASEURL)
                .build();

        // todo get the fucking user id here dude

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
//        String userId =

//        ApiInterface apiService = retrofit.create(ApiInterface.class);
//        apiService.updateCid(firebaseToken ,)


    }

    private interface ApiInterface {
        @POST()
        Call<ResponseBody> updateCid(@Body String cId, @Body String uId);
    }


    @Override
    public void showError(String message) {
        FirebaseCrash.report(new Exception("App Name :  non-fatal error"));
        Log.e(TAG, String.valueOf(message));
//        SplashActivity.this.finish();
    }

    @Override
    public void commentResult(String message) {

    }

    @Override
    public void showComplete() {
        SplashActivity.this.finish();
    }

    @Override
    public void ShowAttractionLists(ShowAttractionListMore getAttractionList) {

    }

    @Override
    public void ShowEventLists(ResultEvents resultEvents) {

    }

    @Override
    public void ShowEventDetail(ResultEvents resultEvent) {

    }


    @Override
    public void showProgress() {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public void ShowAttractionLists(ShowAttractionMoreList showAttractionList) {

    }

    @Override
    public void showAttractionDetail(ShowAtractionDetailMore showAttractionFull) {

    }

}

