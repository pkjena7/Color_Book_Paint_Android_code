package com.templatevilla.colorbookspaint.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.adapter.ParentStickerAdapter;
import com.templatevilla.colorbookspaint.adapter.StickerAdapter;
import com.templatevilla.colorbookspaint.view.CustomTextView;

import java.io.IOException;
import java.util.ArrayList;

import static com.templatevilla.colorbookspaint.util.Constants.EXTRA_SELECTED_IMAGES;


public class SelectStickerActivity extends AppCompatActivity implements StickerAdapter.clickInterface {

    public static ArrayList<String> mSelectedImages = new ArrayList<>();
    StickerAdapter stickerAdapter;
    String folderName;
    public static int sticker_position = 0;
    ParentStickerAdapter parentStickerAdapter;
    String[] list;
    CustomTextView textView;
    StickerSliderAdapter stickerSliderAdapter;
    ImageView btn_ok, btn_cancel;
    String[] strings = {"sticker/summersticker", "sticker/emojis", "sticker/Sticker", "sticker/food", "sticker/pinsticker"};
    String[] header_titles = {"Summer sticker", "Emojis", "Sticker", "Food", "Pin sticker"};
    int[] ints = {R.drawable.summer_sticker, R.drawable.emojis_sticker, R.drawable.splash_sticker, R.drawable.food_sticker, R.drawable.sticker_2};
    ViewPager viewPager;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_select_sticker);
        init();
    }


    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);

        textView = findViewById(R.id.textView);
        viewPager = findViewById(R.id.viewPager);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 5);
        recyclerView.setLayoutManager(layoutManager);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        Log.e("header_titles---", "----" + header_titles.length);
        stickerSliderAdapter = new StickerSliderAdapter(getApplicationContext(), header_titles);
        viewPager.setAdapter(stickerSliderAdapter);




        parentStickerAdapter = new ParentStickerAdapter(ints, strings, (view, i) -> {
            sticker_position = i;
            parentStickerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(i);
        });

        recyclerView.setAdapter(parentStickerAdapter);

        btn_cancel.setOnClickListener(view -> {
            mSelectedImages.clear();
            stickerAdapter.notifyDataSetChanged();
            finish();
        });

        btn_ok.setOnClickListener(view -> {
            Intent data = new Intent();
            data.putExtra(EXTRA_SELECTED_IMAGES, mSelectedImages);
            setResult(RESULT_OK, data);
            finish();
            mSelectedImages.clear();
        });



        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                sticker_position = position;
                parentStickerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    public String[] getList(String folder) {
        String[] list2;
        list2 = new String[0];
        try {
            list2 = this.getAssets().list(folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list2;
    }

    @Override
    public void itemClick(View view, int i) {

        int pos = viewPager.getCurrentItem();
        String[] list = getList(strings[pos]);
        String paths = strings[pos] + "/" + list[i];
        Log.e("folderName===", "" + paths + "    " + pos);


        if (mSelectedImages.contains(paths)) {
            mSelectedImages.remove(paths);
        } else {
            mSelectedImages.add(paths);
        }
        stickerSliderAdapter = new StickerSliderAdapter(getApplicationContext(), header_titles);
        viewPager.setAdapter(stickerSliderAdapter);
        viewPager.setCurrentItem(pos);
        stickerSliderAdapter.notifyDataSetChanged();

    }


    public class StickerSliderAdapter extends PagerAdapter {

        Context context;
        String[] headerList;

        StickerSliderAdapter(Context context, String[] list) {
            this.context = context;
            this.headerList = list;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View viewlayout;
            viewlayout = LayoutInflater.from(context).inflate(R.layout.silder_sticker, container, false);


            textView.setText(headerList[position]);
            RecyclerView recyclerView = viewlayout.findViewById(R.id.recyclerView);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
            recyclerView.setLayoutManager(layoutManager);

            folderName = strings[position];
            Log.e("folderName==", folderName);
            list = getList(folderName);
            stickerAdapter = new StickerAdapter(list, folderName, SelectStickerActivity.this, SelectStickerActivity.this);
            recyclerView.setAdapter(stickerAdapter);
            stickerAdapter.notifyDataSetChanged();

            container.addView(viewlayout);
            return viewlayout;

        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return headerList.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

}
