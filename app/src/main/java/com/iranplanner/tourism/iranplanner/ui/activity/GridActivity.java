package com.iranplanner.tourism.iranplanner.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.iranplanner.tourism.iranplanner.R;
import com.iranplanner.tourism.iranplanner.adapter.CustomGridAdapter;

import java.util.List;

import entity.ResultSouvenir;

/**
 * Created by h.vahidimehr on 25/02/2017.
 */

public class GridActivity extends Activity {
    GridView grid;
    List<ResultSouvenir> resultComments;

//    String[] web = {
//            "Google",
//            "Github",
//            "Instagram",
//            "Facebook",
//            "Flickr",
//            "Pinterest",
//            "Quora",
//            "Twitter",
//            "Vimeo",
//            "WordPress",
//            "Youtube",
//            "Stumbleupon",
//            "SoundCloud",
//            "Reddit",
//            "Blogger"
//
//    } ;
//    int[] imageId = {
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2,
//            R.drawable.emam2
//
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_grid);
        Bundle extras = getIntent().getExtras();
        String fromOpen = extras.getString("fromOpen");
        if (fromOpen.equals("Souvenirs")) {
            resultComments = (List<ResultSouvenir>) extras.getSerializable("resultSouvenirs");
        }
        CustomGridAdapter adapter = new CustomGridAdapter(GridActivity.this, resultComments, fromOpen);
        grid = (GridView) findViewById(R.id.gridView);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();
            }
        });

    }
}
