package com.templatevilla.colorbookspaint.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.adapter.ColorAdapter;
import com.templatevilla.colorbookspaint.adapter.FontStyleAdapter;
import com.templatevilla.colorbookspaint.adapter.MagicAdapter;

import com.templatevilla.colorbookspaint.colorpicker.ColorPickerDialog;
import com.templatevilla.colorbookspaint.colorpicker.ColorPickerDialogListener;
import com.templatevilla.colorbookspaint.sticker.StickerView;
import com.templatevilla.colorbookspaint.sticker.TextStickerView;
import com.templatevilla.colorbookspaint.util.ConnectionDetector;
import com.templatevilla.colorbookspaint.util.Constants;
import com.templatevilla.colorbookspaint.util.ToolsModle;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class EasyPaint extends AppCompatActivity implements DrawingArea.DrawTouch, View.OnClickListener, ColorAdapter.clickInterface, ColorPickerDialogListener {

    Button btn_ok;
    List<Integer> selectedIcon = new ArrayList<>();
    List<ToolsModle> toolsModles = new ArrayList<>();
    List<LinearLayout> parentLayoutList = new ArrayList<>();
    LinearLayout infoView, toolsLayout, clear_layout, layout_add_text, layout_pencil, layout_magic_brush, layout_brush_setting, layout_brush;
    IndicatorSeekBar brush_eraser_size, brush_smoothness, brush_brush_size;
    ImageView btn_magic_back, btn_zoom, btn_cleaner_back, btn_color, btn_add_text_back, btn_brush_back, btn_pencil_back, btn_brush_setting;
    int textColor, brush_color_pos, pencil_color_pos, brushColor = Color.RED, pencilColor = Color.RED, brush_smooth_size = 0, brush_size = 15, brush_erase_size = 10, pencil_erase_size;
    RecyclerView rec_magic, rec_brush_color, rec_pencil_color, rec_font;
    public static int RequestPermissionCode = 1;
    boolean isPencil = false, isPaintSelected = false;
    ImageButton btn_refresh, btn_back, btn_redo, btn_undo, btn_clear, btn_save, btn_brush, btn_magic_brush, btn_pencil, btn_sticker, btn_add_text;
    List<ImageView> headerList;
    boolean isZoomClick, isToolsLayoutVisible = true;
    int REQUEST_STICKER = 111;
    MagicAdapter magicAdapter;
    String[] font_file_list;
    String[] magic_pattern_list;
    TextStickerView mCurrentEditTextView;
    FontStyleAdapter fontStyleAdapter;
    List<String> colorsList;
    ConnectionDetector cd;
    int action = -1;
    ArrayList<String> stickerlist = new ArrayList<>();
    ColorAdapter colorAdapter, pencilColorAdapter;
    FrameLayout backgroundLayout;
    boolean isOfflineMode;
    RelativeLayout bottom_layout, header, top_layout, zoomView;
    String imageName, category_name, category_id, savePath, textString, font, magicBrush;
    ArrayList<StickerView> bitmapstickerList = new ArrayList<>();
    ArrayList<TextStickerView> textStickerViews = new ArrayList<>();
    public ArrayList<View> mViews = new ArrayList<>();
    StickerView mCurrentView;
    ImageView imageView;
    DrawingArea paintView;


    @Override
    protected void onResume() {
        super.onResume();
        cd = new ConnectionDetector(this);
    }

    private void getFontFileList() {
        AssetManager assetManager = getResources().getAssets();
        font_file_list = new String[0];

        try {
            String newfonts = "font";
            font_file_list = assetManager.list(newfonts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert font_file_list != null;
        font = font_file_list[0];

    }

    private void getMagicPatternList() {
        AssetManager assetManager = getResources().getAssets();
        magic_pattern_list = new String[0];

        try {
            String newfonts = "magicbrush";
            magic_pattern_list = assetManager.list(newfonts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert magic_pattern_list != null;
        magicBrush = magic_pattern_list[0];


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_easy_paint);
        init();
        setListener();
    }

    private void setListener() {
        brush_brush_size.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

                brush_size = seekParams.progress;
                paintView.setDrawingStroke(seekParams.progress);


            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });


        brush_smoothness.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                brush_smooth_size = seekParams.progress;
                paintView.setDrawingSmooth(brush_smooth_size);


            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });


        brush_eraser_size.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                if (isPencil) {
                    pencil_erase_size = seekParams.progress;
                } else {
                    brush_erase_size = seekParams.progress;
                }

                paintView.setEraserStroke(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

    }

    // Exit Page Dialog
    public void showExitDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setMessage(getString(R.string.exit_text));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
            finish();
//            Intent intent = new Intent(EasyPaint.this, SubImage.class);
//            intent.putExtra(Constants.CATEGORY_ID, category_id);
//            intent.putExtra(Constants.CATEGORY_NAME, category_name);
//            intent.putExtra(Constants.IsOffLineMode, isOfflineMode);
//            intent.putExtra(Constants.TYPE, getString(R.string.easy));
//            startActivity(intent);
        });
        builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void init() {
        headerList = new ArrayList<>();
        colorsList = new ArrayList<>();
        colorsList = Constants.getAllColors();
        imageName = getIntent().getStringExtra(Constants.ImageName);
        category_id = getIntent().getStringExtra(Constants.CATEGORY_ID);
        category_name = getIntent().getStringExtra(Constants.CATEGORY_NAME);
        isOfflineMode = getIntent().getBooleanExtra(Constants.IsOffLineMode, false);

        rec_magic = findViewById(R.id.rec_magic);
        layout_brush = findViewById(R.id.layout_brush);
        btn_brush_back = findViewById(R.id.btn_brush_back);
        btn_add_text_back = findViewById(R.id.btn_add_text_back);
        btn_pencil_back = findViewById(R.id.btn_pencil_back);
        btn_brush_setting = findViewById(R.id.btn_brush_setting);
        rec_brush_color = findViewById(R.id.rec_brush_color);
        rec_pencil_color = findViewById(R.id.rec_pencil_color);
        rec_font = findViewById(R.id.rec_font);
        layout_brush_setting = findViewById(R.id.layout_brush_setting);
        brush_smoothness = findViewById(R.id.brush_smoothness);
        brush_brush_size = findViewById(R.id.brush_brush_size);
        backgroundLayout = findViewById(R.id.backgroundLayout);
        layout_pencil = findViewById(R.id.layout_pencil);
        layout_magic_brush = findViewById(R.id.layout_magic_brush);
        clear_layout = findViewById(R.id.clear_layout);
        brush_eraser_size = findViewById(R.id.brush_eraser_size);
        layout_add_text = findViewById(R.id.layout_add_text);
        btn_color = findViewById(R.id.btn_color);
        btn_cleaner_back = findViewById(R.id.btn_cleaner_back);
        bottom_layout = findViewById(R.id.bottom_layout);
        header = findViewById(R.id.header);
        infoView = findViewById(R.id.infoView);
        btn_zoom = findViewById(R.id.btn_zoom);
        btn_back = findViewById(R.id.btn_back);
        btn_magic_back = findViewById(R.id.btn_magic_back);
        btn_redo = findViewById(R.id.btn_redo);
        btn_undo = findViewById(R.id.btn_undo);
        btn_clear = findViewById(R.id.btn_clear);
        btn_refresh = findViewById(R.id.btn_refresh);
        btn_save = findViewById(R.id.btn_save);
        btn_brush = findViewById(R.id.btn_brush);
        btn_magic_brush = findViewById(R.id.btn_magic_brush);
        btn_pencil = findViewById(R.id.btn_pencil);
        btn_sticker = findViewById(R.id.btn_sticker);
        btn_add_text = findViewById(R.id.btn_add_text);
        toolsLayout = findViewById(R.id.toolsLayout);
        top_layout = findViewById(R.id.top_layout);
        zoomView = findViewById(R.id.zoomView);
        btn_ok = findViewById(R.id.btn_ok);
        imageView = findViewById(R.id.imageView);
        paintView = findViewById(R.id.paintView);
        brush_brush_size.setMin(15);

        toolsModles = Constants.ToolsModel(getApplicationContext());
        selectedIcon = Constants.SelectedIcon();


        headerList.add(btn_redo);
        headerList.add(btn_undo);
        headerList.add(btn_clear);
        headerList.add(btn_brush);
        headerList.add(btn_magic_brush);
        headerList.add(btn_pencil);


        setClick(btn_back);
        setClick(btn_redo);
        setClick(btn_undo);
        setClick(btn_clear);
        setClick(btn_refresh);
        setClick(btn_save);
        setClick(btn_brush);
        setClick(btn_magic_brush);
        setClick(btn_pencil);
        setClick(btn_sticker);
        setClick(btn_add_text);
        setClick(btn_brush_back);
        setClick(btn_pencil_back);
        setClick(btn_brush_setting);
        setClick(layout_pencil);
        setClick(layout_magic_brush);
        setClick(btn_magic_back);
        setClick(btn_add_text_back);
        setClick(btn_color);
        setClick(btn_cleaner_back);
        setClick(btn_zoom);
        setClick(btn_ok);
        getFontFileList();
        getMagicPatternList();
        setRecyclerBrushColor();
        setRecyclerPencilColor();
        setRecyclerFontStyle();
        setRecyclerMagic();
        setDrawView();
        textString = "Text Sticker";
        setUnselectImage();

        headerList.get(Constants.UNDO - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), toolsModles.get(Constants.UNDO - 1).unSelectedIcon));
        headerList.get(Constants.REDO - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), toolsModles.get(Constants.REDO - 1).unSelectedIcon));

//        enableDisableUndo();
        backgroundLayout.setOnTouchListener((view, motionEvent) -> {
            setCurrentEditFalse();
            setCurrentTextFalse();
            return true;
        });


        parentLayoutList.add(clear_layout);
        parentLayoutList.add(layout_add_text);
        parentLayoutList.add(layout_pencil);
        parentLayoutList.add(layout_brush_setting);
        parentLayoutList.add(layout_brush);
        parentLayoutList.add(layout_magic_brush);


    }

    public void setRecyclerBrushColor() {
        colorAdapter = new ColorAdapter(colorsList, getApplicationContext(), true);
        rec_brush_color.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rec_brush_color.setAdapter(colorAdapter);
        colorAdapter.setListeners(this);
    }


    public void setUnselectImage() {
        for (int i = 0; i < headerList.size(); i++) {
            headerList.get(i).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), toolsModles.get(i).icon));
//            headerList.get(i).setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unselect_bg));
        }
    }


    public void setRecyclerPencilColor() {
        pencilColorAdapter = new ColorAdapter(colorsList, getApplicationContext(), false);
        rec_pencil_color.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        rec_pencil_color.setAdapter(pencilColorAdapter);
        pencilColorAdapter.setListeners(this);
    }

    public void setRecyclerFontStyle() {
        rec_font.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        fontStyleAdapter = new FontStyleAdapter(getApplicationContext(), font_file_list, (v, pos) -> {
            font = font_file_list[pos];


            if (mCurrentEditTextView != null) {
                mCurrentEditTextView.setTextFontFile(getApplicationContext(), font);
            }


            fontStyleAdapter.selectBg(pos);
        });
        rec_font.setAdapter(fontStyleAdapter);


    }

    public void setRecyclerMagic() {
        rec_magic.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        magicAdapter = new MagicAdapter(Arrays.asList(magic_pattern_list), getApplicationContext(), (view, i, s) -> {

            magicBrush = s;

            Log.e("magicBrush", "" + magicBrush);
            paintView.setMagicPattern(magicBrush);
        });
        rec_magic.setAdapter(magicAdapter);


    }


    public void setDrawView() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        width = width - 20;

        final Bitmap[] originalBitmap = {null};
        cd = new ConnectionDetector(this);
        if (!isOfflineMode) {
            if (cd.isConnectingToInternet()) {

                int finalWidth = width;
                Glide.with(this)
                        .asBitmap()
                        .load(imageName)
                        .skipMemoryCache(true)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                Log.e("resource", "" + resource);
                                originalBitmap[0] = scaleBitmap(resource, finalWidth, finalWidth);


                                imageView.setImageBitmap(originalBitmap[0]);


                                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(originalBitmap[0].getWidth(), originalBitmap[0].getHeight());
                                layoutParams.gravity = Gravity.CENTER;
                                paintView.setLayoutParams(layoutParams);


                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });


                paintView.setDrawingListener(EasyPaint.this);
                paintView.setMagicPattern(magicBrush);
            } else {
//            Toast.makeText(this, ""+getString(R.string.download_error), Toast.LENGTH_SHORT).show();
                originalBitmap[0] = scaleBitmap(BitmapFactory.decodeFile(imageName), width, width);
                imageView.setImageBitmap(originalBitmap[0]);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(originalBitmap[0].getWidth(), originalBitmap[0].getHeight());
                layoutParams.gravity = Gravity.CENTER;
                paintView.setLayoutParams(layoutParams);
            }

        } else {

            originalBitmap[0] = scaleBitmap(BitmapFactory.decodeFile(imageName), width, width);
            imageView.setImageBitmap(originalBitmap[0]);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(originalBitmap[0].getWidth(), originalBitmap[0].getHeight());
            layoutParams.gravity = Gravity.CENTER;
            paintView.setLayoutParams(layoutParams);

        }

    }


    public Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    public void setBackgroundView() {
        paintView.setEnableView(true);
    }


    public void goneAllLayout() {
        for (int i = 0; i < parentLayoutList.size(); i++) {
            parentLayoutList.get(i).setVisibility(View.GONE);
        }
    }

    public void visibleActionLayout() {

        if (action != -1) {
            if (action == 3) {
                parentLayoutList.get(4).setVisibility(View.VISIBLE);
                parentLayoutList.get(action).setVisibility(View.VISIBLE);
            } else {
                parentLayoutList.get(action).setVisibility(View.VISIBLE);
            }
        } else {
            toolsLayout.setVisibility(View.VISIBLE);
        }

    }


    private void setClick(View view) {
        view.setOnClickListener(this);
    }

    public void setSelctedImage(int position) {
        Log.e("position", "" + position);
        headerList.get(position - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), selectedIcon.get(position - 1)));

    }

    @Override
    public void onClick(View view) {
        isZoomClick = false;

        Intent intent;
        switch (view.getId()) {


            case R.id.btn_cleaner_back:
                if (!isZoomEnabled()) {
                    clear_layout.setVisibility(View.GONE);
                    toolsLayout.setVisibility(View.VISIBLE);
                    isToolsLayoutVisible = true;
                    action = -1;
                }
                break;

            case R.id.btn_back:
                setUnselectImage();
                showExitDialog();
                break;


            case R.id.btn_brush_back:
                if (!isZoomEnabled()) {
                    if (layout_brush_setting.getVisibility() == View.VISIBLE) {
                        layout_brush_setting.setVisibility(View.GONE);
                        rec_brush_color.setVisibility(View.VISIBLE);
                    } else if (rec_brush_color.getVisibility() == View.VISIBLE) {
                        layout_brush.setVisibility(View.GONE);
                        toolsLayout.setVisibility(View.VISIBLE);
                        isToolsLayoutVisible = true;
                        action = -1;
                    }
                }
                break;


            case R.id.btn_pencil_back:
                if (!isZoomEnabled()) {
                    if (layout_pencil.getVisibility() == View.VISIBLE) {

                        layout_pencil.setVisibility(View.GONE);
                        toolsLayout.setVisibility(View.VISIBLE);
                        isToolsLayoutVisible = true;
                        action = -1;
                    }
                }
                break;
            case R.id.btn_magic_back:
                if (!isZoomEnabled()) {
                    if (layout_magic_brush.getVisibility() == View.VISIBLE) {

                        layout_magic_brush.setVisibility(View.GONE);
                        toolsLayout.setVisibility(View.VISIBLE);
                        isToolsLayoutVisible = true;
                        action = -1;
                    }
                }
                break;
            case R.id.btn_add_text_back:
                if (!isZoomEnabled()) {
                    if (layout_add_text.getVisibility() == View.VISIBLE) {
                        layout_add_text.setVisibility(View.GONE);
                        toolsLayout.setVisibility(View.VISIBLE);
                        isToolsLayoutVisible = true;
                        action = -1;
                    }
                    for (int i = 0; i < textStickerViews.size(); i++) {
                        textStickerViews.get(i).setInEdit(false);
                    }
                }
                break;

            case R.id.btn_brush_setting:
                if (!isZoomEnabled()) {
                    rec_brush_color.setVisibility(View.GONE);
                    layout_brush_setting.setVisibility(View.VISIBLE);
                    action = 3;
                }
                break;

            case R.id.btn_color:
                if (!isZoomEnabled()) {
                    ColorPickerDialog.newBuilder()
                            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                            .setAllowPresets(false)
                            .setDialogId(0)
                            .setColor(Color.BLACK)
                            .setShowAlphaSlider(true)
                            .show(this);
                }
                break;
            case R.id.btn_zoom:
                isZoomClick = true;

                if (isZoomEnabled()) {
                    zoomView.setBackgroundColor(Color.WHITE);
                    bottom_layout.setBackgroundColor(Color.WHITE);
                    zoomView.setVisibility(View.GONE);
                    paintView.setZoomEnabled(false, imageView);
                    top_layout.setBackgroundColor(Color.WHITE);
                    header.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.toolbar_header));
                    btn_zoom.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_zoom_out_map_black_24dp));
                } else {
                    infoView.setVisibility(View.VISIBLE);
                    btn_ok.setVisibility(View.VISIBLE);
                    zoomView.setVisibility(View.VISIBLE);
                    bottom_layout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                    top_layout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                    zoomView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                    paintView.setZoomEnabled(true, imageView);
                    btn_zoom.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_zoom));

                }

                break;
            case R.id.btn_ok:
                infoView.setVisibility(View.GONE);
                btn_ok.setVisibility(View.GONE);
                paintView.setZoomEnabledStart(true);


                break;
            case R.id.btn_brush:
                if (!isZoomEnabled()) {
                    setUnselectImage();
                    isPaintSelected = true;
                    paintView.isMagicBrush(false);
                    paintView.setEnableView(true);
                    paintView.enableEraser(false);
                    isPencil = false;
                    setBackgroundView();
                    paintView.setDrawingStroke(brush_size);
                    paintView.setDrawingSmooth(brush_smooth_size);
                    paintView.setDrawingColor(brushColor);
                    goneAllLayout();
                    toolsLayout.setVisibility(View.GONE);
                    isToolsLayoutVisible = false;
                    layout_brush.setVisibility(View.VISIBLE);
                    action = 4;
                    rec_brush_color.setVisibility(View.VISIBLE);

                    setSelctedImage(Constants.BRUSH);
                    headerList.get(Constants.CLEAR - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.eraser));
                }
                break;
            case R.id.btn_redo:
                if (!isZoomEnabled()) {
                    setUnselectImage();
                    paintView.redoOperation();
                    setSelctedImage(Constants.REDO);
                    setSelctedImage(Constants.BRUSH);

                }

                break;
            case R.id.btn_undo:

                if (!isZoomEnabled()) {
                    setUnselectImage();
                    paintView.undoOperation();
                    setSelctedImage(Constants.UNDO);
                    setSelctedImage(Constants.BRUSH);

                }

                break;
            case R.id.btn_clear:

                if (!isZoomEnabled()) {
                    setUnselectImage();
                    setSelctedImage(Constants.CLEAR);


                    if (isPaintSelected) {

                        if (paintView.getMagicBrush()) {

                            headerList.get(Constants.CLEAR - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.clean_selected));

                            paintView.clearMagicBrush();

                            headerList.get(Constants.MAGIC_BRUSH - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.magic_tool_selected));
                            headerList.get(Constants.CLEAR - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.clean));
                        } else {
                            goneAllLayout();

                            toolsLayout.setVisibility(View.GONE);
                            isToolsLayoutVisible = false;
                            clear_layout.setVisibility(View.VISIBLE);
                            action = 0;

                            if (isPencil) {
                                brush_eraser_size.setProgress(pencil_erase_size);
                            } else {
                                brush_eraser_size.setProgress(brush_erase_size);
                            }

                            paintView.enableEraser(true);

                        }
                    } else {
                        goneAllLayout();
                        toolsLayout.setVisibility(View.GONE);
                        isToolsLayoutVisible = false;
                        clear_layout.setVisibility(View.VISIBLE);
                        action = 0;
                        paintView.setEnableView(true);
                        paintView.enableEraser(true);
                    }
                }
                break;
            case R.id.btn_refresh:

                if (!isZoomEnabled()) {
                    setUnselectImage();
                    paintView.clearDrawing();
                    paintView.clearMagicBrush();
                    goneAllLayout();
                    clear_layout.setVisibility(View.GONE);
                    toolsLayout.setVisibility(View.VISIBLE);
                    isToolsLayoutVisible = true;
                    action = -1;

                }
                break;
            case R.id.btn_save:

                if (!isZoomEnabled()) {
                    setUnselectImage();

                    for (int i = 0; i < bitmapstickerList.size(); i++) {
                        bitmapstickerList.get(i).setInEdit(false);
                    }

                    for (int i = 0; i < textStickerViews.size(); i++) {
                        textStickerViews.get(i).setInEdit(false);
                    }
                    btn_zoom.setVisibility(View.GONE);

                    if (!checkPermission(getApplicationContext())) {
                        requestPermission();
                    } else {
                        new DownloadSaveImageTask().execute();
                    }
                }
                break;
            case R.id.btn_magic_brush:

                if (!isZoomEnabled()) {
                    setUnselectImage();
                    setSelctedImage(Constants.MAGIC_BRUSH);
                    headerList.get(Constants.CLEAR - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.clean));
                    paintView.setEnableView(true);
                    isPaintSelected = true;
                    goneAllLayout();
                    toolsLayout.setVisibility(View.GONE);
                    isToolsLayoutVisible = true;

                    goneAllLayout();
                    paintView.isMagicBrush(true);
                    layout_magic_brush.setVisibility(View.VISIBLE);
                    action = 5;
                }
                break;
            case R.id.btn_pencil:
                if (!isZoomEnabled()) {
                    setUnselectImage();
                    setSelctedImage(Constants.PENCIL);
                    headerList.get(Constants.CLEAR - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.eraser));
                    paintView.setEnableView(true);
                    isPaintSelected = true;
                    paintView.enableEraser(false);
                    paintView.isMagicBrush(false);
                    goneAllLayout();
                    setBackgroundView();
                    toolsLayout.setVisibility(View.GONE);
                    isToolsLayoutVisible = true;
                    action = -1;
                    isPencil = true;
                    paintView.setDrawingStroke(2);
                    paintView.setDrawingColor(pencilColor);
                    paintView.setDrawingSmooth(0);
                    layout_pencil.setVisibility(View.VISIBLE);
                    action = 2;
                }

                break;
            case R.id.btn_sticker:
                if (!isZoomEnabled()) {
                    setUnselectImage();
                    paintView.setEnableView(false);
                    isPaintSelected = false;
                    goneAllLayout();
                    intent = new Intent(EasyPaint.this, SelectStickerActivity.class);
                    startActivityForResult(intent, REQUEST_STICKER);
                }
                break;
            case R.id.btn_add_text:
                if (!isZoomEnabled()) {
                    setUnselectImage();
                    paintView.setEnableView(false);
                    isPaintSelected = false;
                    goneAllLayout();
                    showTextDialog(false, getString(R.string.sticker));
                }
                break;


        }

        enableDisableUndo();

    }


    public void addBubbleStickerView(String str) {
        paintView.setEnableView(false);
        mCurrentEditTextView = new TextStickerView(this);
        mCurrentEditTextView.setText(str);
        mCurrentEditTextView.setTextColor(Color.BLACK);

        mCurrentEditTextView.setOperationListener(new TextStickerView.OperationListener() {
            @Override
            public void onDeleteClick(TextStickerView textStickerView) {
                backgroundLayout.removeView(textStickerView);
                textStickerViews.remove(textStickerView);
            }

            @Override
            public void onEdit(TextStickerView textStickerView) {


                for (int i = 0; i < textStickerViews.size(); i++) {
                    textStickerViews.get(i).setInEdit(false);
                }

                goneAllLayout();
                mCurrentEditTextView = textStickerView;
                mCurrentEditTextView.setInEdit(true);
                layout_add_text.setVisibility(View.VISIBLE);
                toolsLayout.setVisibility(View.GONE);

            }

            @Override
            public void onEditSticker(TextStickerView bubbleTextView) {
                mCurrentEditTextView = bubbleTextView;
                mCurrentEditTextView.setInEdit(true);
                showTextDialog(true, bubbleTextView.getText());

            }

            @Override
            public void onClick(TextStickerView bubbleTextView) {
                mCurrentEditTextView = bubbleTextView;
            }

            @Override
            public void onTop(TextStickerView bubbleTextView) {

            }
        });

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        backgroundLayout.addView(mCurrentEditTextView, lp);
        textStickerViews.add(mCurrentEditTextView);

    }


    public boolean isZoomEnabled() {

        if (paintView.getZoomEnabled() && !isZoomClick) {
            Toast.makeText(this, "" + getString(R.string.unselect_zoom), Toast.LENGTH_SHORT).show();
        }
        return paintView.getZoomEnabled();
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, RequestPermissionCode);
    }

    public void setCurrentTextFalse() {

        if (mCurrentEditTextView != null) {
            mCurrentEditTextView.setInEdit(false);
            layout_add_text.setVisibility(View.GONE);

            Log.e("action", "" + action);
            visibleActionLayout();
        }
    }


    public boolean checkPermission(Context context) {
        int i3 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int i4 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return i3 == PackageManager.PERMISSION_GRANTED
                && i4 == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                for (int i = 0; i < bitmapstickerList.size(); i++) {
                    bitmapstickerList.get(i).setInEdit(false);
                }

                new DownloadSaveImageTask().execute();
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onColorPencilClick(View view, int i, String s) {
        pencil_color_pos = i;
        pencilColor = Color.parseColor(s);
        paintView.setDrawingColor(pencilColor);

    }

    @Override
    public void onDrawTouch() {
        enableDisableUndo();
        setCurrentEditFalse();
        setCurrentTextFalse();
    }


    @SuppressLint("StaticFieldLeak")
    public class DownloadSaveImageTask extends AsyncTask<String, Integer, Bitmap> {


        protected void onPreExecute() {

        }

        protected Bitmap doInBackground(String... urls) {


            SaveImage();

            return null;
        }


        protected void onPostExecute(Bitmap result) {
            btn_zoom.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "" + getString(R.string.save_success), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(EasyPaint.this, ActivityShare.class);
            intent.putExtra(Constants.IMAGEPATH, savePath);
            startActivity(intent);

        }
    }


    @Override
    public void onColorBrushClick(View view, int i, String s) {
        Log.e("i====", "" + i);
        brush_color_pos = i;
        brushColor = Color.parseColor(s);
        paintView.setDrawingColor(brushColor);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_STICKER) {
                assert data != null;
                stickerlist = data.getStringArrayListExtra(Constants.EXTRA_SELECTED_IMAGES);
                assert stickerlist != null;
                for (int i = 0; i < stickerlist.size(); i++) {
                    Bitmap bitmap = Constants.getBitmapFromAsset(getApplicationContext(), stickerlist.get(i));
                    addStickerView(bitmap);
                }
            }
        }
    }


    public void addStickerView(Bitmap img) {
        final StickerView stickerView = new StickerView(getApplicationContext());
        stickerView.setBitmap(img);
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick(StickerView stickerView) {
                for (int i = 0; i < bitmapstickerList.size(); i++) {
                    if (bitmapstickerList.get(i).equals(stickerView)) {
                        mViews.remove(stickerView);
                        backgroundLayout.removeView(stickerView);
                    }
                }
            }

            @Override
            public void onEdit(StickerView stickerView) {
                mCurrentView = stickerView;
                mCurrentView.setInEdit(true);
            }

            @Override
            public void onTop(StickerView stickerView) {
                int position = mViews.indexOf(stickerView);
                if (position == mViews.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) mViews.remove(position);
                mViews.add(mViews.size(), stickerTemp);
            }
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        backgroundLayout.addView(stickerView, lp);
        mViews.add(stickerView);
        bitmapstickerList.add(stickerView);
        setCurrentEdit(stickerView);
    }


    public void setCurrentEdit(StickerView stickerView) {
        stickerView.setInEdit(true);
    }

    public void setCurrentEditFalse() {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }

    }


    public void enableDisableUndo() {

        if (paintView.getUndoSize() > 0) {
            headerList.get(Constants.UNDO - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), toolsModles.get(Constants.UNDO - 1).icon));

        } else {
            headerList.get(Constants.UNDO - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), toolsModles.get(Constants.UNDO - 1).unSelectedIcon));
        }
        if (paintView.getRedoSize() > 0) {
            headerList.get(Constants.REDO - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), toolsModles.get(Constants.REDO - 1).icon));
        } else {
            headerList.get(Constants.REDO - 1).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), toolsModles.get(Constants.REDO - 1).unSelectedIcon));
        }

    }

    private void SaveImage() {
        // TODO Auto-generated method stub






        String path = Constants.getRootDirectory(getApplicationContext()) + Constants.SAVED_PAINT_IMG_PATH;


        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }


        Log.e("getAbsolutePath", "" + dir.getAbsolutePath());


        OutputStream output;


        Bitmap bitmap = Bitmap.createBitmap(backgroundLayout.getWidth(), backgroundLayout.getHeight(),
                Bitmap.Config.ARGB_8888);


        Canvas b = new Canvas(bitmap);
        backgroundLayout.draw(b);


        File saveFile1 = new File(dir, System.currentTimeMillis() + ".jpg");
        savePath = String.valueOf(saveFile1);
        Log.e("saved path", savePath);


        if (saveFile1.exists()) {
            saveFile1.delete();
        }


        try {
            output = new FileOutputStream(saveFile1);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.parse("file://"
                + Environment.getExternalStorageDirectory());
        mediaScanIntent.setData(contentUri);
        (this).sendBroadcast(mediaScanIntent);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(saveFile1.getAbsolutePath()))));


    }


    @Override
    public void onColorSelected(int dialogId, int color) {
        if (mCurrentEditTextView != null) {
            textColor = color;
            mCurrentEditTextView.setTextColor(color);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
    }


    public void showTextDialog(boolean isEdit, String text) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View mView = layoutInflaterAndroid.inflate(R.layout.dialog_add_text, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText editText = mView.findViewById(R.id.editText);
        final Button btn_ok = mView.findViewById(R.id.btn_ok);
        final Button btn_cancel = mView.findViewById(R.id.btn_cancel);

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        if (isEdit) {
            editText.setText(text);
        }

        btn_ok.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(editText.getText().toString())) {
                if (isEdit) {
                    if (mCurrentEditTextView != null) {
                        mCurrentEditTextView.setText(editText.getText().toString());
                    }
                    layout_add_text.setVisibility(View.VISIBLE);
                    toolsLayout.setVisibility(View.GONE);
                    isToolsLayoutVisible = false;
                } else {
                    addBubbleStickerView(editText.getText().toString());
                    layout_add_text.setVisibility(View.VISIBLE);
                    toolsLayout.setVisibility(View.GONE);
                    isToolsLayoutVisible = false;
                }
            }

            Constants.hideKeyboardFrom(getApplicationContext(), editText);
            alertDialogAndroid.dismiss();

        });

        btn_cancel.setOnClickListener(view -> {
            Constants.hideKeyboardFrom(getApplicationContext(), editText);
            alertDialogAndroid.dismiss();
        });

    }


}
