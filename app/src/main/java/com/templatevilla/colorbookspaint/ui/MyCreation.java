package com.templatevilla.colorbookspaint.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;



public class MyCreation extends AppCompatActivity {
    RecyclerView rec_moment;
    MomentAdapter momentAdapter;
    ArrayList<String> filelist;
    File[] files;
    TextView txt_no_data;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_creation);
        init();
        getFromSDCard();

        Collections.reverse(filelist);
        if (filelist.size() > 0) {
            txt_no_data.setVisibility(View.GONE);
        }
        momentAdapter = new MomentAdapter(getApplicationContext(), filelist);
        rec_moment.setAdapter(momentAdapter);


    }

    public void init() {
        filelist = new ArrayList<>();

        txt_no_data = findViewById(R.id.txt_no_data);
        rec_moment = findViewById(R.id.rec_moment);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationOnClickListener(view -> {
            backInent();
        });
        rec_moment.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
    }

    private void getFromSDCard() {
        filelist.clear();

//        String root = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES).toString();
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;



        String path = Constants.getRootDirectory(getApplicationContext()) + Constants.SAVED_PAINT_IMG_PATH;
        File file = new File(path);

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }


        if (file.isDirectory()) {
            files = file.listFiles();
            assert files != null;
            for (File file1 : files) {
                filelist.add(file1.getAbsolutePath());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        momentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        backInent();
    }


    public void backInent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }


    class MomentAdapter extends RecyclerView.Adapter<MyViewHolder> {
        LayoutInflater inflater;
        View view;
        Context context;
        ArrayList<String> file;

        MomentAdapter(Context context, ArrayList<String> file) {
            this.context = context;
            this.file = file;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.item_creation, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            Bitmap myBitmap = BitmapFactory.decodeFile(file.get(position));
            Log.e("position", "" + file.get(position));
            holder.img_photo.setImageBitmap(myBitmap);
            holder.img_photo.setOnClickListener(v -> {
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra(Constants.IMAGEPATH, file.get(position));
                startActivity(intent);
            });

        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return file.size();
        }


    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_photo;

        MyViewHolder(View itemView) {
            super(itemView);
            img_photo = itemView.findViewById(R.id.img_photo);
        }

    }
}
