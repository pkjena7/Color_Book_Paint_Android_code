package com.templatevilla.colorbookspaint.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.adapter.SelectImageAdapter;
import com.templatevilla.colorbookspaint.model.MainImages;
import com.templatevilla.colorbookspaint.model.StoreOfflineModel;
import com.templatevilla.colorbookspaint.util.ConnectionDetector;
import com.templatevilla.colorbookspaint.util.Constants;
import com.templatevilla.colorbookspaint.util.JSONParser;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.templatevilla.colorbookspaint.util.Constants.IMAGE_URL;
import static com.templatevilla.colorbookspaint.util.Constants.MyPref;
import static com.templatevilla.colorbookspaint.util.Constants.SAVED_IMG_PATH;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_PUSH_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_RELEASE_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;


public class SelectImage extends AppCompatActivity {

    RecyclerView recyclerView;
    SelectImageAdapter recMainAdapter;
    JSONParser jsonparser = new JSONParser();
    String ab;
    TextView txt_no_data;
    JSONObject jobj = null;
    String category_id, category_name;
    List<StoreOfflineModel> storeOfflineModels = new ArrayList<>();
    List<StoreOfflineModel> getOfflineModels = new ArrayList<>();
    List<MainImages> mainImages = new ArrayList<>();
    ConnectionDetector cd;
    boolean isOfflineMode;
    ImageView btn_offline;
    ProgressDialog progressDialog;
    AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        init();
        setClick();
        showbanner();
    }

    private void showbanner() {
        if (getResources().getString(R.string.ADS_VISIBILITY).equals("YES")) {
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

    }

    private void setClick() {
        btn_offline.setOnClickListener(v -> {

            if (isOfflineMode) {
                isOfflineMode = false;

                if (cd.isConnectingToInternet()) {
                    btn_offline.setImageResource(R.drawable.switch_off);
                    isOfflineMode = false;
                    new GetOnlineImagesData().execute();
                } else {
                    Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                isOfflineMode = true;
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
    }

    private void init() {

        progressDialog = new ProgressDialog(SelectImage.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(view -> backIntent());

        btn_offline = findViewById(R.id.btn_offline);
        recyclerView = findViewById(R.id.recyclerView);
        txt_no_data = findViewById(R.id.txt_no_data);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        cd = new ConnectionDetector(this);

        if (cd.isConnectingToInternet()) {
            isOfflineMode = false;
            new GetOnlineImagesData().execute();
            btn_offline.setImageResource(R.drawable.switch_off);
        } else {

            if (Constants.getCategorySize(getApplicationContext()) > 0) {
                isOfflineMode = true;
                btn_offline.setImageResource(R.drawable.switch_on);
                new GetOfflineData().execute();
            } else {
                Toast.makeText(this, "" + getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }

        Log.e("Constants", "" + Constants.getCategorySize(getApplicationContext()));
    }

    public void backIntent() {
        Intent intent = new Intent(SelectImage.this, MainActivity.class);
        startActivity(intent);

    }


    @Override
    public void onBackPressed() {
        backIntent();
    }


    public void setMainAdapter() {
        txt_no_data.setVisibility(View.GONE);
        recMainAdapter = new SelectImageAdapter(getApplicationContext(), mainImages, (view, i) -> {
            category_id = mainImages.get(i).category_id;
            category_name = mainImages.get(i).name;
            showPaintTypeDialog(SelectImage.this, i, false);
        });
        recyclerView.setAdapter(recMainAdapter);
        recMainAdapter.notifyDataSetChanged();
    }


    public void passCategoryIntent(String type) {

        Intent intent = new Intent(SelectImage.this, SubImage.class);
        intent.putExtra(Constants.CATEGORY_ID, category_id);
        intent.putExtra(Constants.TYPE, type);
        intent.putExtra(Constants.IsOffLineMode, isOfflineMode);
        intent.putExtra(Constants.CATEGORY_NAME, category_name);

        startActivity(intent);
    }

    public void setOffLineMainAdapter() {
        if (getOfflineModels.size() > 0) {
            txt_no_data.setVisibility(View.GONE);
        }else {
            txt_no_data.setVisibility(View.VISIBLE);
        }
        recMainAdapter = new SelectImageAdapter(getApplicationContext(), true, getOfflineModels, (view, i) -> {
            category_id = getOfflineModels.get(i).category_id;
            showPaintTypeDialog(SelectImage.this, i, true);
        });
        recyclerView.setAdapter(recMainAdapter);
        recMainAdapter.notifyDataSetChanged();
    }


    public void passOffLineCategoryIntent(int position, String type) {


        if (type.equals(getString(R.string.easy))) {

            if (getOfflineModels.size() > 0) {
                if (getOfflineModels.get(position).subEasycategoryList.size() <= 0) {
                    Toast.makeText(SelectImage.this, "" + getString(R.string.no_category), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SelectImage.this, SubImage.class);
                    intent.putExtra(Constants.CATEGORY_ID, category_id);
                    intent.putExtra(Constants.IsOffLineMode, isOfflineMode);
                    intent.putExtra(Constants.TYPE, type);
                    intent.putExtra(Constants.CATEGORY_NAME, category_name);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(SelectImage.this, "" + getString(R.string.no_category), Toast.LENGTH_SHORT).show();
            }
        } else if (type.equals(getString(R.string.hard))) {
            if (getOfflineModels.size() > 0) {
                if (getOfflineModels.get(position).subHardcategoryList.size() <= 0) {
                    Toast.makeText(SelectImage.this, "" + getString(R.string.no_category), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SelectImage.this, SubImage.class);
                    intent.putExtra(Constants.CATEGORY_ID, category_id);
                    intent.putExtra(Constants.TYPE, type);
                    intent.putExtra(Constants.IsOffLineMode, isOfflineMode);
                    intent.putExtra(Constants.CATEGORY_NAME, category_name);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(SelectImage.this, "" + getString(R.string.no_category), Toast.LENGTH_SHORT).show();
            }

        }


    }

    public void showPaintTypeDialog(Activity activity, int mainposition, boolean isMode) {
        final AlertDialog alert_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_paint_type, null);
        builder.setView(view);
        ImageView btn_close = view.findViewById(R.id.btn_close);
        Button btn_easy = view.findViewById(R.id.btn_easy);
        Button btn_hard = view.findViewById(R.id.btn_hard);

        PushDownAnim.setPushDownAnimTo(btn_hard, btn_easy, btn_close).setScale(MODE_SCALE, 0.89f).setDurationPush(DEFAULT_PUSH_DURATION).setDurationRelease(DEFAULT_RELEASE_DURATION);

        alert_dialog = builder.create();
        alert_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alert_dialog.show();

        btn_close.setOnClickListener(v -> alert_dialog.dismiss());

        btn_easy.setOnClickListener(v -> {
            if (isMode) {
                isOfflineMode = true;
                passOffLineCategoryIntent(mainposition, getString(R.string.easy));
            } else {
                if (cd.isConnectingToInternet()) {
                    isOfflineMode = false;
                    passCategoryIntent(getString(R.string.easy));
                } else {
                    isOfflineMode = true;
                    passOffLineCategoryIntent(mainposition, getString(R.string.easy));
                }

            }
        });


        btn_hard.setOnClickListener(v -> {
            if (isMode) {
                isOfflineMode = true;
                passOffLineCategoryIntent(mainposition, getString(R.string.hard));
            } else {
                if (cd.isConnectingToInternet()) {
                    isOfflineMode = false;
                    passCategoryIntent(getString(R.string.hard));
                } else {
                    isOfflineMode = true;
                    passOffLineCategoryIntent(mainposition, getString(R.string.hard));
                }

            }

        });


    }


    class GetOnlineImagesData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(SelectImage.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub

            mainImages.clear();
            jobj = jsonparser.getJSONFromUrl(IMAGE_URL);
            // check your log for json response

            JSONArray resultList = null;


            try {

                if (jobj != null) {
                    JSONObject json2 = jobj.getJSONObject(getString(R.string.data));
                    resultList = json2.getJSONArray(getString(R.string.image_param));


                    Log.e("jobj===", "" + jobj.toString());
                    Log.e("jobj1===", "" + resultList.length());
                    for (int i = 0; i < resultList.length(); i++) {
                        JSONObject jsonobject = (JSONObject) resultList.get(i);
                        mainImages.add(new MainImages(jsonobject.optString(getString(R.string.category_id)), jsonobject.optString(getString(R.string.name)), jsonobject.optString(getString(R.string.image))));
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();


            }


            return ab;
        }

        protected void onPostExecute(String ab) {


            if (Constants.getCategorySize(getApplicationContext()) <= 0) {
                Log.e("mainImages", "" + mainImages.size());
//                imgName = mainImages.get(count).image;
                new DownloadOfflineImage().execute();
            } else {
                progressDialog.dismiss();
            }
            setMainAdapter();

        }

    }

    class GetOfflineData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(SelectImage.this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            getOfflineModels.clear();

            List<StoreOfflineModel> getOffline = Constants.getOfflineData(getApplicationContext());

            for (int i = 0; i < getOffline.size(); i++) {
                Log.e("subHardcategoryList-=--", "" + getOffline.get(i).subHardcategoryList.size() + "---" + getOffline.get(i).subEasycategoryList.size());
                if (getOffline.get(i).subEasycategoryList.size() > 0 || getOffline.get(i).subHardcategoryList.size() > 0) {
                    getOfflineModels.add(getOffline.get(i));
                }
            }
            return ab;
        }

        protected void onPostExecute(String ab) {
            progressDialog.dismiss();
            Log.e("getOfflineModels", "" + getOfflineModels.size());
            setOffLineMainAdapter();

        }

    }


    private class DownloadOfflineImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            for (int i = 0; i < mainImages.size(); i++) {
                storeOfflineModels.add(new StoreOfflineModel(mainImages.get(i).category_id, mainImages.get(i).name, mainImages.get(i).image, mainImages.get(i).image));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {


            Log.e("storeOfflineModels12654---", "---" + storeOfflineModels.size());

                for (int i = 0; i < storeOfflineModels.size(); i++) {
                    SharedPreferences sharedPreferences = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Gson gson = new Gson();
                    String json = gson.toJson(storeOfflineModels.get(i));
                    editor.putString(getString(R.string.main_category) + i, json);
                    editor.apply();
                }

                Constants.setCategorySize(mainImages.size(), getApplicationContext());
                progressDialog.dismiss();

        }
    }


}
