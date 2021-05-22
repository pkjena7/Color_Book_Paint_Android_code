package com.templatevilla.colorbookspaint.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.application.MyApplication;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.File;

import static com.templatevilla.colorbookspaint.util.Constants.SAVED_IMG_PATH;
import static com.templatevilla.colorbookspaint.util.Constants.showRatingDialog;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_PUSH_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_RELEASE_DURATION;


public class MainActivity extends AppCompatActivity {

    Button btn_play, btn_my_creation, btn_share, btn_rate,btn_privacy_policy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        if (!checkPermission(getApplicationContext())) {
            requestPermission();
        }
        init();
        setClick();


    }

    @Override
    protected void onPause() {

        super.onPause();
        MyApplication.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
        MyApplication.activityResumed();// On Resume notify the Application
    }





    private void setClick() {
        btn_play.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SelectImage.class);
            startActivity(intent);
        });

        btn_my_creation.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MyCreation.class);
            startActivity(intent);
        });

        btn_share.setOnClickListener(view -> {
            share();
        });

        btn_rate.setOnClickListener(view -> {

            showRatingDialog(MainActivity.this);
        });
//        btn_privacy_policy.setOnClickListener(view -> {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_link)));
//            startActivity(browserIntent);
//        });
    }


    public boolean checkPermission(Context context) {
        int i3 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int i4 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return i3 == PackageManager.PERMISSION_GRANTED
                && i4 == PackageManager.PERMISSION_GRANTED;
    }


    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 2);
    }

    private void init() {
        btn_play = findViewById(R.id.btn_play);
        btn_my_creation = findViewById(R.id.btn_my_creation);
        btn_share = findViewById(R.id.btn_share);
        btn_rate = findViewById(R.id.btn_rate);
    //    btn_privacy_policy = findViewById(R.id.btn_privacy_policy);

        PushDownAnim.setPushDownAnimTo(btn_rate, btn_play, btn_my_creation, btn_share).
                setScale(PushDownAnim.MODE_STATIC_DP, 10).setDurationPush(DEFAULT_PUSH_DURATION)
                .setDurationRelease(DEFAULT_RELEASE_DURATION)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);

    }

    public void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Xyz");
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.SHARE_APP_LINK)
                + getPackageName());
        startActivity(Intent.createChooser(share, "Share Link!"));

    }


    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
    }


}



