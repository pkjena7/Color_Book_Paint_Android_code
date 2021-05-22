package com.templatevilla.colorbookspaint.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.util.Constants;

import java.io.File;

public class ImageActivity extends AppCompatActivity {

    String path;
    ImageView imageView;
    Button btn_share;
    ImageView  btn_delete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_image);
        init();
        setClick();
    }

    private void setClick() {
        btn_delete.setOnClickListener(view -> {
            deleteDialog();
        });

        btn_share.setOnClickListener(view -> {
            shareImage();
        });


    }

    public void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete this photo");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> deleteImage());
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void deleteImage() {
        File file1 = new File(path);
        file1.delete();

        Intent intent = new Intent(this, MyCreation.class);
        startActivity(intent);

    }


    public void shareImage() {
        File file1 = new File(path);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");
        Uri photoURI;
        photoURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".provider", file1);
        share.putExtra(Intent.EXTRA_STREAM, photoURI);

        startActivity(Intent.createChooser(share, getString(R.string.photo_editor_share_image)));
    }

    private void init() {
        path = getIntent().getStringExtra(Constants.IMAGEPATH);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            backInent();
        });

        imageView = findViewById(R.id.imageView);
        btn_share = findViewById(R.id.btn_share);
        btn_delete = findViewById(R.id.btn_delete);

        if (!TextUtils.isEmpty(path)) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        }

    }

    public void backInent() {
        Intent intent = new Intent(this, MyCreation.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        backInent();
    }
}
