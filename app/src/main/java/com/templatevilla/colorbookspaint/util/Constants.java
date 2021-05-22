package com.templatevilla.colorbookspaint.util;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.model.StoreOfflineModel;
import com.google.gson.Gson;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_PUSH_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_RELEASE_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class Constants {


    public static final String IMAGEPATH = "imgpath";
    public static final String EXTRA_SELECTED_IMAGES = "selected_image";
    public static final String SAVED_IMG_PATH = "/KidsDraw";
//    public static final String SAVED_IMG_PATH = Environment.getExternalStorageDirectory() + "/KidsDraw/";
    public static final String ImageName = "ImageName";
    public static final String SAVED_PAINT_IMG_PATH = "/KidsDrawPaint";

    public static final String SAVED_IMG_PATH_EASY = "/Easy";
//    public static final String SAVED_IMG_PATH_EASY = "/KidsDraw/Easy/";
    public static final String SAVED_IMG_PATH_HARD = "/Hard";
//    public static final String SAVED_IMG_PATH_HARD = "/KidsDraw/Hard";

    public static final String IMG_FOLDER = "300";
    public static final String PAINT_IMG_FOLDER = "white_img";
    public static final String CATEGORY_ID = "category_id";
    public static final String MyPref = "MyPref";
    public static final String CATEGORY_NAME = "category_name";
    public static final String TYPE = "type";
    public static final String IsOffLineMode = "isOfflineMode";
    private static final String CATEGORY_SIZE = "category_size";


//    private static String BASE_URL = "http://templatevilla.net/codecanyon/colorbookpaintadmin/";
    private static String BASE_URL = "http://templatevilla.net/codecanyon/colorbookpaintadmin/";
//    private static String BASE_URL = "http://templatevictory.com/colorpaint/";

    public static String IMAGE_URL = BASE_URL + "api/category.php";
    public static String SUB_IMAGE_URL = BASE_URL + "api/images.php";
    public static String SUB_TRANSPARENT_IMAGE_URL = BASE_URL + "api/images.php";
    public static String UPLOAD_URL = BASE_URL + "uploads/";





    public static String getRootDirectory(Context context){
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        String root;
        if (currentapiVersion > Build.VERSION_CODES.Q) {
            root = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString();
        }else {
            root = Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath();
        }

        return root;
    }

    public static List<String> getAllColors() {
        List<String> stringList = new ArrayList<>();
        stringList.add("#e53935");
        stringList.add("#7cb342");
        stringList.add("#c0ca33");
        stringList.add("#43a047");
        stringList.add("#3949ab");
        stringList.add("#8e24aa");
        stringList.add("#9e9e9e");
        stringList.add("#e91e63");
        stringList.add("#6D35B2");
        stringList.add("#009688");
        stringList.add("#ffeb3b");
        stringList.add("#ffc107");
        stringList.add("#ff9800");
        stringList.add("#795548");
        stringList.add("#26a69a");
        stringList.add("#29b6f6");
        stringList.add("#ff5722");
        stringList.add("#607d8b");
        stringList.add("#00acc1");
        return stringList;
    }


    public static Bitmap getBitmapFromAsset(Context context, String s) {
        Bitmap bitmap = null;

        InputStream inputStream;
        AssetManager assetManager = context.getAssets();
        try {
            inputStream = assetManager.open(s);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
        }
        return bitmap;
    }


    public static final int REDO = 1;
    public static final int UNDO = 2;
    public static final int CLEAR = 3;
    public static final int BRUSH = 4;
    public static final int MAGIC_BRUSH = 5;
    public static final int PENCIL = 6;


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static List<ToolsModle> ToolsModel(Context context) {
        List<ToolsModle> toolsModles = new ArrayList<>();
        toolsModles.add(new ToolsModle(context.getString(R.string.redo), R.drawable.redo, R.drawable.redo_unselected, REDO));
        toolsModles.add(new ToolsModle(context.getString(R.string.undo), R.drawable.undo, R.drawable.undo_unselected, UNDO));
        toolsModles.add(new ToolsModle(context.getString(R.string.eraser), R.drawable.eraser, 0, CLEAR));
        toolsModles.add(new ToolsModle(context.getString(R.string.brush), R.drawable.paint, 0, BRUSH));
        toolsModles.add(new ToolsModle(context.getString(R.string.magic_brush), R.drawable.magic_tool, 0, MAGIC_BRUSH));
        toolsModles.add(new ToolsModle(context.getString(R.string.pencil), R.drawable.pensil, 0, PENCIL));
        return toolsModles;
    }


    public static List<Integer> SelectedIcon() {
        List<Integer> toolsModles = new ArrayList<>();
        toolsModles.add(R.drawable.redo_selected);
        toolsModles.add(R.drawable.undo_selected);
        toolsModles.add(R.drawable.eraser_selected);
        toolsModles.add(R.drawable.paint_selected);
        toolsModles.add(R.drawable.magic_tool_selected);
        toolsModles.add(R.drawable.pensil_seleted);
        return toolsModles;
    }


    public static List<StoreOfflineModel> getOfflineData(Context context) {
        List<StoreOfflineModel> modelList = new ArrayList<>();

        for (int i = 0; i < Constants.getCategorySize(context); i++) {
            SharedPreferences sharedPreferences1 = context.getSharedPreferences(MyPref, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences1.getString(context.getString(R.string.main_category) + i, null);
            StoreOfflineModel model = gson.fromJson(json, StoreOfflineModel.class);

            if (model != null) {
                modelList.add(model);
            }
        }

        return modelList;
    }


    public static void setCategorySize(int size, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(CATEGORY_SIZE, size);
        editor.apply();
        editor.apply();
    }

    public static int getCategorySize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(CATEGORY_SIZE, 0);
    }


    public static void sendFeedback(String feedback, Activity activity) {

        String str;
        try {
            str = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{activity.getResources().getString(R.string.feedback_mail)});
            i.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Color book paint");
            i.putExtra(Intent.EXTRA_TEXT, "\n\n----------------------------------\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + str + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER + "\n" + "feedback : " + feedback);
            try {
                activity.startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void showRatingDialog(Activity activity) {
        final AlertDialog alert_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_rating, null);
        builder.setView(view);
        final RatingBar rating_bar = view.findViewById(R.id.rating_bar);
        ImageView btn_submit = view.findViewById(R.id.btn_submit);
        TextView tv_no = view.findViewById(R.id.tv_no);
        PushDownAnim.setPushDownAnimTo(btn_submit).setScale(MODE_SCALE, 0.89f).setDurationPush(DEFAULT_PUSH_DURATION).setDurationRelease(DEFAULT_RELEASE_DURATION);

        alert_dialog = builder.create();
        Objects.requireNonNull(alert_dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        alert_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alert_dialog.show();

        btn_submit.setOnClickListener(v -> {
            if (rating_bar.getRating() >= 3) {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")));

                } catch (android.content.ActivityNotFoundException anfe) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")));
                }
                alert_dialog.dismiss();
            } else if (rating_bar.getRating() <= 0) {
                Toast.makeText(activity, "" + activity.getString(R.string.rating_error), Toast.LENGTH_SHORT).show();
            } else {

                alert_dialog.dismiss();
                showFeedbackDialog(activity);
            }
        });
        tv_no.setOnClickListener(v -> alert_dialog.dismiss());
    }


    private static void showFeedbackDialog(Activity activity) {
        final AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_feedback, null);
        builder.setView(view);
        final EditText edt_feedback = view.findViewById(R.id.edt_feedback);
        ImageView btn_submit = view.findViewById(R.id.btn_submit);
        TextView btn_cancel = view.findViewById(R.id.btn_cancel);
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        PushDownAnim.setPushDownAnimTo(btn_submit).setScale(MODE_SCALE, 0.89f).setDurationPush(DEFAULT_PUSH_DURATION).setDurationRelease(DEFAULT_RELEASE_DURATION);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alertDialog.show();
        btn_cancel.setOnClickListener(v -> alertDialog.dismiss());
        btn_submit.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (!TextUtils.isEmpty(edt_feedback.getText().toString())) {
                sendFeedback(edt_feedback.getText().toString(),activity);
            }
        });

    }


}
