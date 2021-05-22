package com.templatevilla.colorbookspaint.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.util.Constants;

import java.io.File;
import java.util.Objects;

import static com.templatevilla.colorbookspaint.util.Constants.showRatingDialog;

public class ActivityShare extends AppCompatActivity {

    String path;
    ImageView imageView;
    ImageView btn_more_app, btn_rate;
    Button btn_share, btn_my_creation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        init();
        setClick();
    }

    private void setClick() {


        btn_share.setOnClickListener(view -> {
            shareImage();
        });

        btn_my_creation.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MyCreation.class);
            startActivity(intent);
        });

        btn_rate.setOnClickListener(view -> {
            showRatingDialog(ActivityShare.this);
        });


        btn_more_app.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });
    }


    public void shareImage() {
        File file1 = new File(path);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");
        Uri photoURI;
        photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.templatevilla.colorbookspaint.provider", file1);
        share.putExtra(Intent.EXTRA_STREAM, photoURI);

        startActivity(Intent.createChooser(share, getString(R.string.photo_editor_share_image)));
    }

    private void init() {
        path = getIntent().getStringExtra(Constants.IMAGEPATH);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            backInent();
        });

        imageView = findViewById(R.id.imageView);
        btn_share = findViewById(R.id.btn_share);
        btn_my_creation = findViewById(R.id.btn_my_creation);
        btn_more_app = findViewById(R.id.btn_more_app);
        btn_rate = findViewById(R.id.btn_rate);

        if (!TextUtils.isEmpty(path)) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        }

    }

    public void backInent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        backInent();
    }
}
