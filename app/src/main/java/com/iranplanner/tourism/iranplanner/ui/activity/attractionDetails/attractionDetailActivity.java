package com.iranplanner.tourism.iranplanner.ui.activity.attractionDetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.coinpany.core.android.widget.CTouchyWebView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.iranplanner.tourism.iranplanner.R;
import com.iranplanner.tourism.iranplanner.RecyclerItemOnClickListener;
import com.iranplanner.tourism.iranplanner.di.model.App;
import com.iranplanner.tourism.iranplanner.ui.activity.MapFullActivity;
import com.iranplanner.tourism.iranplanner.ui.activity.attractioListMore.AttractionListMoreContract;
import com.iranplanner.tourism.iranplanner.ui.activity.attractioListMore.AttractionListMorePresenter;
import com.iranplanner.tourism.iranplanner.ui.activity.comment.CommentListActivity;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import entity.InterestResult;
import entity.ItineraryLodgingCity;
import entity.ResulAttraction;
import entity.ResultAttractionList;
import entity.ResultComment;
import entity.ResultCommentList;
import entity.ResultData;
import entity.ResultWidget;
import entity.ResultWidgetFull;
import entity.ShowAtractionDetailMore;
import entity.ShowAttractionMoreList;
import tools.Constants;
import tools.Util;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class attractionDetailActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, AttractionDetailContract.View, AttractionListMoreContract.View {
    @Inject
    AttractionDetailPresenter attractionDetailPresenter;
    @Inject
    AttractionListMorePresenter attractionListMorePresenter;
    private GoogleMap mMap;
    ResulAttraction resulAttraction;
    Marker marker;
    SupportMapFragment mapFragment;
    Boolean showMore = true;
    String myData;
    boolean ratingHolderFlag = false;
    String rotateImage;
    RotateAnimation rotate;
    List<ResultWidget> resultWidget;
    ProgressDialog progressDialog;
    int BookmarkValue;
    int LikeValue;
    int VisitedValue;
    int WishValue;
//    RecyclerView recyclerBestAttraction;
//


    @InjectView(R.id.contentFullDescription)
    CTouchyWebView contentFullDescription;
    @InjectView(R.id.attractionName)
    TextView attractionName;
    @InjectView(R.id.attractionPlace)
    TextView attractionPlace;
    @InjectView(R.id.textTimeDuration)
    TextView textTimeDuration;
    @InjectView(R.id.textEntranceFee)
    TextView textEntranceFee;
    @InjectView(R.id.attractionType)
    TextView attractionType;
    @InjectView(R.id.imageTypeAttraction)
    ImageView imageTypeAttraction;
    @InjectView(R.id.imageAttraction)
    ImageView imageAttraction;
    @InjectView(R.id.commentHolder)
    LinearLayout commentHolder;
    @InjectView(R.id.rateHolder)
    LinearLayout rateHolder;
    @InjectView(R.id.doneHolder)
    LinearLayout doneHolder;
    @InjectView(R.id.nowVisitedHolder)
    LinearLayout nowVisitedHolder;
    @InjectView(R.id.beftorVisitedHolder)
    LinearLayout beftorVisitedHolder;
    @InjectView(R.id.dislikeHolder)
    LinearLayout dislikeHolder;
    @InjectView(R.id.okHolder)
    LinearLayout okHolder;
    @InjectView(R.id.likeHolder)
    LinearLayout likeHolder;
    @InjectView(R.id.bookmarkHolder)
    LinearLayout bookmarkHolder;
    @InjectView(R.id.ratingHolder)
    RelativeLayout ratingHolder;
    @InjectView(R.id.GroupHolder)
    RelativeLayout GroupHolder;
    @InjectView(R.id.interestingLayout)
    RelativeLayout interestingLayout;
    @InjectView(R.id.VisitedLayout)
    RelativeLayout VisitedLayout;
    @InjectView(R.id.LikeLayout)
    RelativeLayout LikeLayout;
    @InjectView(R.id.MoreInoText)
    TextView MoreInoText;
    @InjectView(R.id.bookmarkImg)
    ImageView bookmarkImg;
    @InjectView(R.id.doneImg)
    ImageView doneImg;
    @InjectView(R.id.dislikeImg)
    ImageView dislikeImg;
    @InjectView(R.id.okImg)
    ImageView okImg;
    @InjectView(R.id.likeImg)
    ImageView likeImg;
    @InjectView(R.id.rateImg)
    ImageView rateImg;
    @InjectView(R.id.beftorVisitedImg)
    ImageView beftorVisitedImg;
    @InjectView(R.id.nowVisitedImg)
    ImageView nowVisitedImg;
    @InjectView(R.id.wishImg)
    ImageView wishImg;
    @InjectView(R.id.triangleShowAttraction)
    ImageView triangleShowAttraction;
    @InjectView(R.id.recyclerBestAttraction)
    RecyclerView recyclerBestAttraction;
    DaggerAtractionDetailComponent.Builder builder;
    private List<ResultAttractionList> resultAttractionList;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void findView() {
//        setContentView(R.layout.activity_attraction_detail);
        setContentView(R.layout.fragment_attraction_detail);
        ButterKnife.inject(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    private void overrideFont() {
        // for Override font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void setImageHolder() {
        if (resulAttraction.getAttractionImgUrl() != null) {
            String url = resulAttraction.getAttractionImgUrl();
            Glide.with(getApplicationContext())
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            //// TODO: 22/01/2017  get defeult picture
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageAttraction);

        } else {
            Glide.clear(imageAttraction);
            imageAttraction.setImageDrawable(null);
        }
    }

    private void setAttractionTypeImage() {
        if (resulAttraction.getAttractionItineraryTypeId() != null) {
            if (resulAttraction.getAttractionItineraryTypeId().equals("2930")) {
                imageTypeAttraction.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mazhabi));
            } else if (resulAttraction.getAttractionItineraryTypeId().equals("2931")) {
                imageTypeAttraction.setImageDrawable(getResources().getDrawable(R.mipmap.ic_natural));
            } else if (resulAttraction.getAttractionItineraryTypeId().equals("2932")) {
                imageTypeAttraction.setImageDrawable(getResources().getDrawable(R.mipmap.ic_historical));
            } else if (resulAttraction.getAttractionItineraryTypeId().equals("2933")) {
                imageTypeAttraction.setImageDrawable(getResources().getDrawable(R.mipmap.ic_sport));
            }
        }
    }

    private void setWebViewContent(String myData) {
        contentFullDescription.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        contentFullDescription.setLongClickable(false);
        contentFullDescription.setHapticFeedbackEnabled(false);


        String pish = "<html><head><style type=\"text/css\">@font-face {color:#737373;font-family: MyFont;src: url(\"file:///android_asset/fonts/IRANSansMobile.ttf\")}body {font-family: MyFont;font-size: small;text-align: justify;direction:rtl}</style></head><body>";
        String pas = "</body></html>";
        String myHtmlString = pish + myData + pas;
        contentFullDescription.loadDataWithBaseURL(null, myHtmlString, "text/html", "UTF-8", null);
    }

    private String getShowMoreString(String myData) {
        int count = 0;
        int position = 0;
        for (count = 0; count < 40; count++) {
            position = myData.indexOf(" ", position + 1);
        }
        return myData.substring(0, position) + "...";
    }

    private void getExtra() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        attraction = (entity.ResultItineraryAttraction) bundle.getSerializable("ResultItineraryAttraction");
        resulAttraction = (ResulAttraction) bundle.getSerializable("resulAttraction");
        resultAttractionList = (List<ResultAttractionList>) bundle.getSerializable("resultAttractionList");
        resultWidget = (List<ResultWidget>) bundle.getSerializable("resultWidget");
        if (resultWidget != null) {
            setInterestResponce(resultWidget);
        }
    }

    private void setNearAttraction(List<ResultAttractionList> resultAttractions) {
        nearAttractionAdapter attractionHomeAdapter = new nearAttractionAdapter(resultAttractions, getApplicationContext());
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerBestAttraction.setLayoutManager(horizontalLayoutManagaer);
        recyclerBestAttraction.setAdapter(attractionHomeAdapter);
        recyclerBestAttraction.addOnItemTouchListener(new RecyclerItemOnClickListener(getApplicationContext(), new RecyclerItemOnClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                attractionListMorePresenter.getAttractionDetailNear("full", resulAttraction.getAttractionId(), "fa", "0", Util.getTokenFromSharedPreferences(getApplicationContext()), Util.getAndroidIdFromSharedPreferences(getApplicationContext()));

            }
        }));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        overrideFont();
        getExtra();
        setNearAttraction(resultAttractionList);
        attractionName.setText(resulAttraction.getAttractionTitle());
        attractionPlace.setText(resulAttraction.getProvinceTitle() + " - " + resulAttraction.getCityTitle());
        if (resulAttraction.getAttractionEstimatedTime() != null) {
            int totalMinute = Integer.parseInt(resulAttraction.getAttractionEstimatedTime());
            Util.convertMinuteToHour(totalMinute, textTimeDuration);
        }

        setImageHolder();
        myData = resulAttraction.getAttractionBody();
        if (myData != null) {
            setWebViewContent(getShowMoreString(myData));
        }

        if (resulAttraction.getAttractionPrice() == null) {
            textEntranceFee.setText("رایگان");
        } else {
            textEntranceFee.setText(Util.persianNumbers(resulAttraction.getAttractionPrice().toString()) + "تومان");
        }
        attractionType.setText(resulAttraction.getAttractionItineraryTypeTitle());
        setAttractionTypeImage();

        interestingLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = interestingLayout.getWidth();
                int height = interestingLayout.getHeight();
                if (width > 0 && height > 0) {
                    VisitedLayout.setVisibility(View.INVISIBLE);
                    LikeLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        mapFragment.getMapAsync(this);
        MoreInoText.setOnClickListener(this);
        ratingHolder.setOnClickListener(this);
        rateHolder.setOnClickListener(this);
        doneHolder.setOnClickListener(this);
        likeImg.setOnClickListener(this);
        okImg.setOnClickListener(this);
        dislikeImg.setOnClickListener(this);
        nowVisitedImg.setOnClickListener(this);
        wishImg.setOnClickListener(this);
        beftorVisitedImg.setOnClickListener(this);
        bookmarkHolder.setOnClickListener(this);
        commentHolder.setOnClickListener(this);

        builder = DaggerAtractionDetailComponent.builder()
                .netComponent(((App) getApplicationContext()).getNetComponent())
                .attractionDetailModule(new AttractionDetailModule(this, this));
        builder.build().inject(this);
        attractionDetailPresenter.getWidgetResult("nodeuser", resulAttraction.getAttractionId(), Util.getUseRIdFromShareprefrence(getApplicationContext()), "attraction", Util.getTokenFromSharedPreferences(getApplicationContext()), Util.getAndroidIdFromSharedPreferences(getApplicationContext()));


    }

    private void setInterestResponce(List<ResultWidget> resultWidget) {
        if (resultWidget.get(0).getWidgetBookmarkValue() != null && resultWidget.get(0).getWidgetBookmarkValue() == 1) {
            bookmarkImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_bookmarkgreen));
        }
        if (resultWidget.get(0).getWidgetLikeValue() != null && resultWidget.get(0).getWidgetLikeValue() == 1) {
            likeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_on));
            rateImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_on));
            okImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_off));
            dislikeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_off));
        }
        if (resultWidget.get(0).getWidgetLikeValue() != null && resultWidget.get(0).getWidgetLikeValue() == 2) {
            okImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_on));
            rateImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_on));
            dislikeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_off));
            likeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_off));
        }
        if (resultWidget.get(0).getWidgetLikeValue() != null && resultWidget.get(0).getWidgetLikeValue() == 3) {
            dislikeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_on));
            rateImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_on));
            likeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_off));
            okImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_off));
        }
        if (resultWidget.get(0).getWidgetVisitedValue() != null && resultWidget.get(0).getWidgetVisitedValue() == 1) {
            nowVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_now_seen_on));
            doneImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_now_seen_on));
            beftorVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_off));
        }
        if (resultWidget.get(0).getWidgetVisitedValue() != null && resultWidget.get(0).getWidgetVisitedValue() == 2) {
            beftorVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_on));
            doneImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_on));
            nowVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_off));
        }
        if (resultWidget.get(0).getWidgetWishValue() != null && resultWidget.get(0).getWidgetWishValue() == 1) {
            wishImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_wish_pink));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        resulAttraction.getAttractionPositionLat();
        resulAttraction.getAttractionPositionLon();
        float lan = Float.valueOf(resulAttraction.getAttractionPositionLat());
        float lon = Float.valueOf(resulAttraction.getAttractionPositionLon());
        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker));

        marker = mMap.addMarker(markerOptions
                .position(new LatLng(lan, lon))
                .title(resulAttraction.getCityTitle())
                .snippet(":)"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lan, lon), 15.0f));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("map is ckicked", "true");
                Intent intent = new Intent(getApplicationContext(), MapFullActivity.class);
                ItineraryLodgingCity i = new ItineraryLodgingCity();
                i.setCityPositionLat(resulAttraction.getAttractionPositionLat());
                i.setCityPositionLon(resulAttraction.getAttractionPositionLon());
                List<ItineraryLodgingCity> lodgingCities = new ArrayList<ItineraryLodgingCity>();
                lodgingCities.add(i);
                intent.putExtra("lodgingCities", (Serializable) lodgingCities);
                intent.putExtra("resulAttraction", (Serializable) resulAttraction);
                startActivity(intent);
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(attractionDetailActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("لطفا منتظر بمانید");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commentHolder:
                showProgressDialog();
                builder.build().inject(this);
                attractionDetailPresenter.getAttractionCommentList("pagecomments", resulAttraction.getAttractionId(), "attraction", "0", Util.getTokenFromSharedPreferences(getApplicationContext()), Util.getAndroidIdFromSharedPreferences(getApplicationContext()));
                break;
            case R.id.MoreInoText:
                if (showMore) {
                    setWebViewContent(myData);
                    MoreInoText.setText("مطلب کوتاه");
                    showMore = false;
                } else {
                    if (resulAttraction.getAttractionBody() != null) {
                        setWebViewContent(getShowMoreString(myData));
                        MoreInoText.setText("بیشتر بخوانید");
                        showMore = true;
                    }
                }

                break;
            case R.id.ratingHolder:
                if (ratingHolderFlag) {
//                    translateUp();
                }
                break;
            case R.id.rateHolder:
                if (!ratingHolderFlag) {
                    VisitedLayout.setVisibility(View.INVISIBLE);
                    LikeLayout.setVisibility(View.VISIBLE);
                    rotateImage = "rateImg";
//                    translateDown();
                    break;
                } else {
//                    translateUp();
                    break;
                }

            case R.id.doneHolder:
                if (!ratingHolderFlag) {
                    LikeLayout.setVisibility(View.INVISIBLE);
                    VisitedLayout.setVisibility(View.VISIBLE);
                    rotateImage = "doneImg";
//                    translateDown();
                    break;
                } else {
//                    translateUp();
                    break;
                }

            case R.id.likeImg:
                rotateImage = "likeImg";
                if (LikeValue == 1) {
                    OnClickedIntrestedWidget("like", Constants.intrestDefault, likeImg);
                } else {
                    OnClickedIntrestedWidget("like", Constants.likeImg, likeImg);
                }
                break;
            case R.id.okImg:
                rotateImage = "okImg";
                if (LikeValue == 2) {
                    OnClickedIntrestedWidget("like", Constants.intrestDefault, okImg);
                } else {
                    OnClickedIntrestedWidget("like", Constants.okImg, okImg);
                }
                break;
            case R.id.dislikeImg:
                rotateImage = "dislikeImg";
                if (LikeValue == 2) {
                    OnClickedIntrestedWidget("like", Constants.intrestDefault, dislikeImg);
                } else {
                    OnClickedIntrestedWidget("like", Constants.dislikeImg, dislikeImg);
                }
                break;
            case R.id.wishImg:
                rotateImage = "wishImg";
                if (WishValue == 1) {
                    OnClickedIntrestedWidget("wish", Constants.intrestDefault, wishImg);
                } else {
                    OnClickedIntrestedWidget("wish", Constants.wishImg, wishImg);
                }
                break;
            case R.id.nowVisitedImg:
                rotateImage = "nowVisitedImg";

                if (VisitedValue == 1) {
                    OnClickedIntrestedWidget("visited", Constants.intrestDefault, nowVisitedImg);
                } else {
                    OnClickedIntrestedWidget("visited", Constants.nowVisitedImg, nowVisitedImg);
                }
                break;
            case R.id.beftorVisitedImg:
                rotateImage = "beftorVisitedImg";
                if (VisitedValue == 2) {
                    OnClickedIntrestedWidget("visited", Constants.intrestDefault, beftorVisitedImg);
                } else {
                    OnClickedIntrestedWidget("visited", Constants.beftorVisitedImg, beftorVisitedImg);
                }
                break;
            case R.id.bookmarkHolder:

                rotateImage = "bookmarkImg";

                if (BookmarkValue == 1) {
                    OnClickedIntrestedWidget("bookmark", Constants.intrestDefault, bookmarkImg);
                } else {
                    OnClickedIntrestedWidget("bookmark", Constants.bookmarkImg, bookmarkImg);
                }
                break;



        }
    }
    private void OnClickedIntrestedWidget(String gType, String gValue, ImageView imageView) {
        if (!Util.getUseRIdFromShareprefrence(getApplicationContext()).isEmpty()) {
            attractionDetailPresenter.doWaitingAnimation(imageView);
            attractionDetailPresenter.getInterest("widget", Util.getUseRIdFromShareprefrence(getApplicationContext()), "1", "attraction", resulAttraction.getAttractionId(), gType, gValue, Util.getAndroidIdFromSharedPreferences(getApplicationContext()));

        } else {
            Log.e("user is not login", "error");
            Toast.makeText(getApplicationContext(), "شما به حساب کاربری خود وارد نشده اید", Toast.LENGTH_LONG).show();
        }
    }

    private void translateDown() {

        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(ratingHolder, "translationY", interestingLayout.getHeight()),
                ObjectAnimator.ofFloat(GroupHolder, "translationY", interestingLayout.getHeight()),
                ObjectAnimator.ofFloat(triangleShowAttraction, "translationY", -55));
        mAnimatorSet.setDuration(1000);
        mAnimatorSet.start();
        ratingHolderFlag = true;

    }

    private void translateUp() {
        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(ratingHolder, "translationY", 0),
                ObjectAnimator.ofFloat(GroupHolder, "translationY", 0),
                ObjectAnimator.ofFloat(triangleShowAttraction, "translationY", 0));
        mAnimatorSet.setDuration(1000);
        mAnimatorSet.start();
        ratingHolderFlag = false;
    }
    private void animWaiting(ImageView image) {
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setRepeatCount(5);
        rotate.setDuration(5000);
        rotate.setInterpolator(new LinearInterpolator());
        image.startAnimation(rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                translateUp();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void checkWhichImageIntrested(String imageView) {

        String im = imageView;
        switch (im) {
            case "bookmarkImg":
                bookmarkImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_bookmarkgreen));
                break;
            case "nowVisitedImg":
                nowVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_now_seen_on));
                doneImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_now_seen_on));
                beftorVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_off));

                break;
            case "beftorVisitedImg":
                beftorVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_on));
                doneImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_on));
                nowVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_off));

                break;
            case "dislikeImg":
                dislikeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_on));
                rateImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_on));
                okImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_off));
                likeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_off));

                break;
            case "okImg":
                okImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_on));
                rateImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_on));
                likeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_off));
                dislikeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_off));
                break;
            case "likeImg":
                likeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_on));
                rateImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_on));
                dislikeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_off));
                okImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_off));
                break;
            case "wishImg":
                wishImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_wish_pink));
                break;
            default:
                break;
        }
    }

    @Override
    public void showComments(ResultCommentList resultCommentList) {

    }

    @Override
    public void sendCommentMessage(ResultCommentList resultCommentList) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void commentResult(String message) {

    }

    @Override
    public void showComplete() {

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
        ResulAttraction resulAttraction = showAttractionFull.getResultAttractionFull().getResulAttraction();
        List<ResultAttractionList> resultAttractions = (List<ResultAttractionList>) showAttractionFull.getResultAttractionFull().getResultAttractionList();
        Intent intent = new Intent(this, attractionDetailActivity.class);
        intent.putExtra("resulAttraction", (Serializable) resulAttraction);
        intent.putExtra("resultAttractionList", (Serializable) resultAttractions);
        startActivity(intent);
    }


    @Override
    public void showComment(ResultCommentList resultCommentList, String commentType) {
        List<ResultComment> resultComments = resultCommentList.getResultComment();
        Intent intent = new Intent(attractionDetailActivity.this, CommentListActivity.class);
        intent.putExtra("resultComments", (Serializable) resultComments);
        intent.putExtra("attraction", (Serializable) resulAttraction);
        intent.putExtra("nextOffset", resultCommentList.getStatistics().getOffsetNext().toString());
        intent.putExtra("fromWhere", commentType);
        startActivity(intent);
        progressDialog.dismiss();
    }

    @Override
    public void setLoadWidget(ResultWidgetFull resultWidgetFull) {
        List<ResultWidget> resultWidget = resultWidgetFull.getResultWidget();
        setWidgetValue(resultWidget);
    }

    @Override
    public void setIntrestedWidget(InterestResult InterestResult) {
        ResultData resultData = InterestResult.getResultData();
        //// TODO: 14/02/2017
//        rotate.setRepeatCount(0);
        checkWhichImageIntrested(rotateImage);
    }

    @Override
    public void showAnimationWhenWaiting() {
        ratingHolderFlag = attractionDetailPresenter.doTranslateAnimationUp(ratingHolder, GroupHolder, triangleShowAttraction);

    }

    @Override
    public void setIntrestValue(InterestResult InterestResult) {

    }

    @Override
    public void showDirectionOnMap(PolylineOptions rectLine) {
        mMap.addPolyline(rectLine);

    }
    private void setWidgetValue(List<ResultWidget> resultWidget) {

        if (resultWidget.get(0).getWidgetBookmarkValue() != null) {
            BookmarkValue = resultWidget.get(0).getWidgetBookmarkValue();
        }
        if (resultWidget.get(0).getWidgetLikeValue() != null) {
            LikeValue = resultWidget.get(0).getWidgetLikeValue();
        }
        if (resultWidget.get(0).getWidgetVisitedValue() != null) {
            VisitedValue = resultWidget.get(0).getWidgetVisitedValue();
        }
        if (resultWidget.get(0).getWidgetWishValue() != null) {
            WishValue = resultWidget.get(0).getWidgetWishValue();
        }


        if (resultWidget.get(0).getWidgetBookmarkValue() != null && resultWidget.get(0).getWidgetBookmarkValue() == 1) {
            bookmarkImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_bookmarkgreen));
        } else if (resultWidget.get(0).getWidgetBookmarkValue() == null || resultWidget.get(0).getWidgetBookmarkValue() == 0) {
            bookmarkImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_bookmark_grey));
        }
        if (resultWidget.get(0).getWidgetWishValue() != null && resultWidget.get(0).getWidgetWishValue() == 1) {
            wishImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_wish_pink));
        } else if (resultWidget.get(0).getWidgetBookmarkValue() == null || resultWidget.get(0).getWidgetBookmarkValue() == 0) {
            wishImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_wish_grey));
        }
        if (resultWidget.get(0).getWidgetLikeValue() != null && resultWidget.get(0).getWidgetLikeValue() == 1) {
            likeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_on));
            rateImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_on));
        } else if (resultWidget.get(0).getWidgetLikeValue() == null || resultWidget.get(0).getWidgetLikeValue() == 0) {
            likeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_like_off));
        }
        if (resultWidget.get(0).getWidgetLikeValue() != null && resultWidget.get(0).getWidgetLikeValue() == 2) {
            okImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_on));
            rateImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_on));
        } else if (resultWidget.get(0).getWidgetLikeValue() == null || resultWidget.get(0).getWidgetLikeValue() == 0) {
            okImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_ok_off));

        }
        if (resultWidget.get(0).getWidgetLikeValue() != null && resultWidget.get(0).getWidgetLikeValue() == 3) {
            dislikeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_on));
            rateImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_on));
        } else if (resultWidget.get(0).getWidgetLikeValue() == null || resultWidget.get(0).getWidgetLikeValue() == 0) {
            dislikeImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_dislike_off));

        }
        if (resultWidget.get(0).getWidgetVisitedValue() != null && resultWidget.get(0).getWidgetVisitedValue() == 1) {
            nowVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_now_seen_on));
            doneImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_now_seen_on));
        } else if (resultWidget.get(0).getWidgetVisitedValue() == null || resultWidget.get(0).getWidgetVisitedValue() == 0) {
            nowVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_off));

        }
        if (resultWidget.get(0).getWidgetVisitedValue() != null && resultWidget.get(0).getWidgetVisitedValue() == 2) {
            beftorVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_on));
            doneImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_on));
        } else if (resultWidget.get(0).getWidgetVisitedValue() == null || resultWidget.get(0).getWidgetVisitedValue() == 0) {
            beftorVisitedImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_before_seen_off));

        }
    }

}
