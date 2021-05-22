package com.templatevilla.colorbookspaint.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.adapter.SelectSubImageAdapter;
import com.templatevilla.colorbookspaint.model.StoreOfflineModel;
import com.templatevilla.colorbookspaint.model.SubImages;
import com.templatevilla.colorbookspaint.util.ConnectionDetector;
import com.templatevilla.colorbookspaint.util.Constants;
import com.templatevilla.colorbookspaint.util.RequestHandler;
import com.templatevilla.colorbookspaint.view.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.templatevilla.colorbookspaint.util.Constants.MyPref;
import static com.templatevilla.colorbookspaint.util.Constants.SAVED_IMG_PATH_EASY;
import static com.templatevilla.colorbookspaint.util.Constants.SAVED_IMG_PATH_HARD;
import static com.templatevilla.colorbookspaint.util.Constants.SUB_IMAGE_URL;
import static com.templatevilla.colorbookspaint.util.Constants.UPLOAD_URL;


public class SubImage1 extends AppCompatActivity {

    RecyclerView recyclerView;
    SelectSubImageAdapter recMainAdapter;
    String ab;
    TextView txt_no_data;
    JSONObject jobj = null;
    String category_id;
    CustomTextView text_header;
    String jsonString;
    int notifyPosition;
    ConnectionDetector cd;
    String imgName, saveFile;
    AdView adView;
    int level;
    Bitmap bitmap;
    boolean isOfflineMode;
    public static List<String> subImgaeList = new ArrayList<>();
    List<StoreOfflineModel> getOfflineModels = new ArrayList<>();
    ProgressDialog progressDialog;
    ImageView btn_offline;
    public static String image_type, category_name;
    List<SubImages> mainImages = new ArrayList<>();
    boolean interstitialCanceled;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_select_image);
        getOfflineModels = Constants.getOfflineData(getApplicationContext());
        Log.e("getOfflineModels--", "" + getOfflineModels.size());
        init();
        setClick();
        admob_banner();
    }

    private void admob_banner() {
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void setClick() {
        btn_offline.setOnClickListener(v -> {

            if (isOfflineMode) {
                isOfflineMode = false;

                if (cd.isConnectingToInternet()) {
                    isOfflineMode = false;
                    btn_offline.setImageResource(R.drawable.switch_off);
                    new GetData().execute();
                } else {
                    Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }


            } else {


                if (Constants.getCategorySize(getApplicationContext()) > 0) {
                    isOfflineMode = true;
                    new GetOfflineData().execute();
                    btn_offline.setImageResource(R.drawable.switch_off);
                } else {
                    Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

                btn_offline.setImageResource(R.drawable.switch_on);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cd = new ConnectionDetector(this);
        interstitialCanceled = false;
        if (getResources().getString(R.string.ADS_VISIBILITY).equals("YES")) {
            CallNewInsertial();
        }

    }

    private void CallNewInsertial() {
        cd = new ConnectionDetector(SubImage1.this);
        if (cd.isConnectingToInternet()) {
            mInterstitialAd = new InterstitialAd(SubImage1.this);
            mInterstitialAd.setAdUnitId(getString(R.string.admob_interestial_id));
            requestNewInterstitial();

        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void init() {
        isOfflineMode = getIntent().getBooleanExtra(Constants.IsOffLineMode, false);
        category_id = getIntent().getStringExtra(Constants.CATEGORY_ID);
        category_name = getIntent().getStringExtra(Constants.CATEGORY_NAME);
        image_type = getIntent().getStringExtra(Constants.TYPE);
        assert image_type != null;
        if (image_type.equals(getString(R.string.easy))) {
            level = 0;

        } else {
            level = 1;
        }


        Log.e("category_id", "" + category_id);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(view -> backIntent());
        txt_no_data = findViewById(R.id.txt_no_data);
        btn_offline = findViewById(R.id.btn_offline);
        recyclerView = findViewById(R.id.recyclerView);
        text_header = findViewById(R.id.text_header);
        text_header.setText(category_name);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        cd = new ConnectionDetector(this);


        Log.e("isOfflineMode", "" + isOfflineMode);

        if (isOfflineMode) {
            if (Constants.getCategorySize(getApplicationContext()) > 0) {
                isOfflineMode = true;
                btn_offline.setImageResource(R.drawable.switch_on);
                new GetOfflineData().execute();
            } else {
                Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }

        } else {
            new GetData().execute();
            btn_offline.setImageResource(R.drawable.switch_off);
        }


    }

    public void backIntent() {
        Intent intent = new Intent(SubImage1.this, SelectImage.class);
        startActivity(intent);

    }


    @Override
    public void onBackPressed() {
        backIntent();
    }

    public void setMainAdapter() {
        if (mainImages.size() > 0) {
            txt_no_data.setVisibility(View.GONE);
        }

        recMainAdapter = new SelectSubImageAdapter(getApplicationContext(), mainImages, new SelectSubImageAdapter.ClickInterface() {
            @Override
            public void recItemClick(View view, int i, boolean isDownload, String path) {

                if (!interstitialCanceled) {
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {
                            public void onAdClosed() {
                                passIntentonline(i, isDownload, path);
                            }
                        });
                    } else {
                        passIntentonline(i, isDownload, path);
                    }
                }


            }

            @Override
            public void onDownloadClick(View view, int i) {
                notifyPosition = i;
                imgName = mainImages.get(i).image;
                if (checkPermission(getApplicationContext())) {
                    requestPermission();
                } else {
                    Log.e("imgList1", "" + mainImages.get(i).image);
                    new DownloadImage().execute(UPLOAD_URL + mainImages.get(i).image);
                }


            }

            @Override
            public void onDeleteImage(View view, String path) {

            }
        });
        recyclerView.setAdapter(recMainAdapter);
        recMainAdapter.notifyDataSetChanged();
    }

    private void passIntentonline(int i, boolean isDownload, String path) {
        if (cd.isConnectingToInternet()) {
            Intent intent;
            if (image_type.equals(getString(R.string.easy))) {
                intent = new Intent(getApplicationContext(), EasyPaint.class);
            } else {
                intent = new Intent(getApplicationContext(), HardPaint.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra(Constants.ImageName, UPLOAD_URL + mainImages.get(i).image);
            intent.putExtra(Constants.CATEGORY_ID, mainImages.get(i).category_id);
            intent.putExtra(Constants.CATEGORY_NAME, category_name);
            intent.putExtra(Constants.IsOffLineMode, isOfflineMode);
            startActivity(intent);
        } else {

            if (isDownload) {
                Intent intent;

                if (image_type.equals(getString(R.string.easy))) {
                    intent = new Intent(getApplicationContext(), EasyPaint.class);
                } else {
                    intent = new Intent(getApplicationContext(), HardPaint.class);
                }


                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(Constants.ImageName, path);
                intent.putExtra(Constants.IsOffLineMode, isOfflineMode);
                intent.putExtra(Constants.CATEGORY_ID, category_id);
                intent.putExtra(Constants.CATEGORY_NAME, category_name);
                startActivity(intent);
            } else {
                Toast.makeText(SubImage1.this, "" + getString(R.string.download_error), Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void setOfflineAdapter() {
        if (subImgaeList.size() > 0) {
            txt_no_data.setVisibility(View.GONE);
        } else {
            txt_no_data.setVisibility(View.VISIBLE);
        }


        recMainAdapter = new SelectSubImageAdapter(getApplicationContext(), true, subImgaeList, new SelectSubImageAdapter.ClickInterface() {
            @Override
            public void recItemClick(View view, int i, boolean isDownload, String path) {

                if (!interstitialCanceled) {
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        mInterstitialAd.setAdListener(new AdListener() {
                            public void onAdClosed() {
                                passIntentoffline(i, isDownload, path);
                            }
                        });
                    } else {
                        passIntentoffline(i, isDownload, path);
                    }
                }


            }

            @Override
            public void onDownloadClick(View view, int i) {
                imgName = mainImages.get(i).image;

                if (checkPermission(getApplicationContext())) {
                    requestPermission();
                } else {
                    new DownloadImage().execute(UPLOAD_URL + mainImages.get(i).image);
                }


            }

            @Override
            public void onDeleteImage(View view, String path) {
                deleteDialog(path);

            }
        });
        recyclerView.setAdapter(recMainAdapter);
        recMainAdapter.notifyDataSetChanged();
    }

    private void passIntentoffline(int i, boolean isDownload, String path) {
        Intent intent;

        if (image_type.equals(getString(R.string.easy))) {
            intent = new Intent(getApplicationContext(), EasyPaint.class);
        } else {
            intent = new Intent(getApplicationContext(), HardPaint.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(Constants.ImageName, subImgaeList.get(i));
        intent.putExtra(Constants.CATEGORY_ID, category_id);
        intent.putExtra(Constants.CATEGORY_NAME, category_name);
        startActivity(intent);
    }


    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 2);
    }

    public boolean checkPermission(Context context) {
        int i3 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int i4 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return i3 != PackageManager.PERMISSION_GRANTED
                || i4 != PackageManager.PERMISSION_GRANTED;
    }

    class GetData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SubImage1.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub


            try {
                // POST Request
                JSONObject postDataParams = new JSONObject();
                postDataParams.put(getString(R.string.image_param), category_id);
                postDataParams.put(getString(R.string.image_param1), level);

                return RequestHandler.sendPost(SUB_IMAGE_URL, postDataParams);
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }


        }

        protected void onPostExecute(String ab) {
            jsonString = ab;
            Log.e("jsonString", "" + jsonString);
            new GetImagesData().execute();
        }

    }


    class GetOfflineData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SubImage1.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            getOfflineModels.clear();
            getOfflineModels = Constants.getOfflineData(getApplicationContext());
            return ab;
        }

        protected void onPostExecute(String ab) {
            progressDialog.dismiss();
            subImgaeList.clear();

            for (int i = 0; i < getOfflineModels.size(); i++) {
                if (getOfflineModels.get(i).category_id.equals(category_id)) {

                    if (image_type.equals(getString(R.string.easy))) {
                        subImgaeList = getOfflineModels.get(i).subEasycategoryList;
                    } else if (image_type.equals(getString(R.string.hard))) {
                        subImgaeList = getOfflineModels.get(i).subHardcategoryList;
                    }
                }
            }


            HashSet<String> hashSet = new HashSet<>(subImgaeList);
            List<String> arrayList2 = new ArrayList<>(hashSet);
            subImgaeList.clear();
            subImgaeList.addAll(arrayList2);


            new SetEmptyData().execute();

        }

    }

    class SetEmptyData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SubImage1.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub


            for (int i = 0; i < subImgaeList.size(); i++) {
                if (BitmapFactory.decodeFile(subImgaeList.get(i)) == null) {
                    subImgaeList.remove(subImgaeList.get(i));


                }
            }
            return ab;
        }

        protected void onPostExecute(String ab) {
            progressDialog.dismiss();


            setOfflineAdapter();

        }

    }


    class GetImagesData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            JSONArray resultList;
            mainImages.clear();
            try {
                if (jsonString != null) {
                    jobj = new JSONObject(jsonString);
                    JSONObject json2 = jobj.getJSONObject(getString(R.string.data));
                    resultList = json2.getJSONArray(getString(R.string.images));
                    Log.e("mainImages", "" + resultList.toString());
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject jsonobject = (JSONObject) resultList.get(i);

                        if (!TextUtils.isEmpty(jsonobject.optString(getString(R.string.name)))) {
                            mainImages.add(new SubImages(jsonobject.optString(getString(R.string.category_id)), jsonobject.optString(getString(R.string.image_id)),
                                    jsonobject.optString(getString(R.string.name)), jsonobject.optString(getString(R.string.image))));
                        }


                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            getOfflineModels.clear();
            getOfflineModels = Constants.getOfflineData(getApplicationContext());
            return ab;
        }

        protected void onPostExecute(String ab) {
            if (getOfflineModels.size() > 0) {
                for (int i = 0; i < getOfflineModels.size(); i++) {
                    if (getOfflineModels.get(i).category_id.equals(category_id)) {
                        if (image_type.equals(getString(R.string.easy))) {
                            subImgaeList = getOfflineModels.get(i).subEasycategoryList;
                        } else if (image_type.equals(getString(R.string.hard))) {
                            subImgaeList = getOfflineModels.get(i).subHardcategoryList;
                        }
                    }
                }
            }

            HashSet<String> hashSet = new HashSet<>(subImgaeList);
            subImgaeList = new ArrayList<>(hashSet);
            progressDialog.dismiss();
            setMainAdapter();
        }

    }


    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SubImage1.this);
            progressDialog.setMessage(getString(R.string.download_image));
            progressDialog.show();

        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            // Close progressdialog

            if (result != null) {
                bitmap = result;
                new DownloadSaveImageTask().execute();
            }

        }
    }

    public void deleteDialog(String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete this photo");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> deleteImage(path));
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void deleteImage(String path) {
        File file1 = new File(path);
        file1.delete();
        subImgaeList.remove(path);
        if (subImgaeList.size() == 0) {
            txt_no_data.setVisibility(View.VISIBLE);
        }
        recMainAdapter.notifyDataSetChanged();
        MediaScannerConnection.scanFile(SubImage1.this, new String[]{Environment.getExternalStorageDirectory().toString()}, null,
                (path1, uri) -> {
                    Log.i("ExternalStorage", "Scanned " + path1 + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                });


        Log.e("imageType===--", "" + (image_type.equals(getString(R.string.hard))) + "====" + (image_type.equals(getString(R.string.easy))));

        List<StoreOfflineModel> getOfflineModels = Constants.getOfflineData(getApplicationContext());
        if (getOfflineModels.size() > 0) {
            for (int i = 0; i < getOfflineModels.size(); i++) {
                if (category_id.equals(getOfflineModels.get(i).category_id)) {
                    List<String> strings = new ArrayList<>();
                    if (image_type.equals(getString(R.string.easy))) {
                        strings = getOfflineModels.get(i).subEasycategoryList;
                    } else if (image_type.equals(getString(R.string.hard))) {
                        strings = getOfflineModels.get(i).subHardcategoryList;
                    }


                    Log.e("strings", "" + strings.size());

                    if (strings.size() > 0) {
                        Log.e("strings", "" + strings.size());
                        if (getOfflineModels.get(i).getSubcategoryList().contains(path)) {
                            strings.remove(path);
                            getOfflineModels.get(i).setEasySubcategoryList(strings);
                        } else if (getOfflineModels.get(i).getSubHardcategoryList().contains(path)) {
                            strings.remove(path);
                            getOfflineModels.get(i).setSubHardcategoryList(strings);
                        }

                    }
                }


            }
        }


        for (int i = 0; i < getOfflineModels.size(); i++) {
            SharedPreferences sharedPreferences = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();
            String json = gson.toJson(getOfflineModels.get(i));
            editor.putString(getString(R.string.main_category) + i, json);
            editor.apply();

        }
    }


    protected void saveImageToInternalStorage(Bitmap bitmap) {
        // Initialize ContextWrapper





        String path;




        if (image_type.equals(getString(R.string.easy))) {
            path =  Constants.getRootDirectory(getApplicationContext())  + SAVED_IMG_PATH_EASY;
        } else {
            path =  Constants.getRootDirectory(getApplicationContext())  + SAVED_IMG_PATH_HARD;
        }
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }


        // Create a file to save the image
        File saveFile1 = new File(dir, category_name + imgName);
        saveFile = saveFile1.getAbsolutePath();
        Log.e("saveFile1=====", "" + saveFile);
        try {
            // Initialize a new OutputStream
            OutputStream stream;

            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(saveFile1);

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();

        } catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }


        // Return the saved image Uri


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new DownloadImage().execute(UPLOAD_URL + imgName);
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @SuppressLint("StaticFieldLeak")
    public class DownloadSaveImageTask extends AsyncTask<String, Integer, Bitmap> {


        protected void onPreExecute() {
        }

        protected Bitmap doInBackground(String... urls) {

            saveImageToInternalStorage(bitmap);

            return null;
        }


        protected void onPostExecute(Bitmap result) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "" + getString(R.string.download_success), Toast.LENGTH_SHORT).show();



//            Log.e("saveFile------",""+saveFile);

            getOfflineModels = Constants.getOfflineData(getApplicationContext());


                        Log.e("getOfflineModelsSave------",""+getOfflineModels.size()+"===="+Constants.getCategorySize(getApplicationContext()));

            if (getOfflineModels.size() > 0) {
                Log.e("getOfflineModels--", "" + getOfflineModels.size());
                for (int i = 0; i < getOfflineModels.size(); i++) {
                    if (category_id.equals(getOfflineModels.get(i).category_id)) {
                        List<String> strings = new ArrayList<>();
                        if (image_type.equals(getString(R.string.easy))) {
                            strings = getOfflineModels.get(i).subEasycategoryList;
                        } else if (image_type.equals(getString(R.string.hard))) {
                            strings = getOfflineModels.get(i).subHardcategoryList;
                        }


                        Log.e("strings", "" + strings.size());

                        if (strings.size() > 0) {

                            if (!strings.contains(saveFile)) {
//                        if (!getOfflineModels.get(i).getSubcategoryList().contains(saveFile)) {
                                strings.add(saveFile);
                                if (image_type.equals(getString(R.string.easy))) {
                                    getOfflineModels.get(i).setEasySubcategoryList(strings);
                                } else if (image_type.equals(getString(R.string.hard))) {
                                    getOfflineModels.get(i).setSubHardcategoryList(strings);
                                }
                            }

                        } else {
                            Log.e("strings1234----", "" + strings.size());
                            strings.add(saveFile);
                            if (image_type.equals(getString(R.string.easy))) {
                                getOfflineModels.get(i).setEasySubcategoryList(strings);
                            } else if (image_type.equals(getString(R.string.hard))) {
                                getOfflineModels.get(i).setSubHardcategoryList(strings);
                            }

                        }
                    }
//
//

                }




                subImgaeList.add(saveFile);
                recMainAdapter.notifyItemChanged(notifyPosition);
            }


            for (int i = 0; i < getOfflineModels.size(); i++) {
                SharedPreferences sharedPreferences = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String json = gson.toJson(getOfflineModels.get(i));
                editor.putString(getString(R.string.main_category) + i, json);
                editor.apply();

            }
        }
    }


}
