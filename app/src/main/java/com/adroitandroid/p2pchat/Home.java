package com.adroitandroid.p2pchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class Home extends AppCompatActivity {

    CardView mDiscover, mInbox, mSettings, mAbout;
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_home);


        mDiscover = (CardView)findViewById(R.id.carddiscover);
        mInbox = (CardView) findViewById(R.id.cardinbox);
        mSettings = (CardView) findViewById(R.id.cardsettings);
        mAbout = (CardView) findViewById(R.id.cardabout);

        mDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, MainActivity.class));
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Settings.class));
            }
        });
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, About.class));
            }
        });
        mInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Inbox.class));
            }
        });


    }
}
