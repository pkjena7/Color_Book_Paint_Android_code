package com.templatevilla.colorbookspaint.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.model.SubImages;
import com.templatevilla.colorbookspaint.ui.SubImage;
import com.templatevilla.colorbookspaint.util.Constants;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.templatevilla.colorbookspaint.ui.SubImage.category_name;
import static com.templatevilla.colorbookspaint.ui.SubImage.image_type;
import static com.templatevilla.colorbookspaint.util.Constants.SAVED_IMG_PATH;
import static com.templatevilla.colorbookspaint.util.Constants.SAVED_IMG_PATH_EASY;


public class SelectSubImageAdapter extends RecyclerView.Adapter<SelectSubImageAdapter.MyViewHolder> {
    private Context context;
    private List<SubImages> imgList1;
    private List<String> storeOfflineModels;
    private ClickInterface interfcaeobj;
    private boolean isOfflineMode;

    public SelectSubImageAdapter(Context context, List<SubImages> imgList, ClickInterface interfcaeobj) {
        this.context = context;
        this.imgList1 = imgList;
        this.interfcaeobj = interfcaeobj;
    }


    public SelectSubImageAdapter(Context context, boolean isOfflineMode, List<String> storeOfflineModels, ClickInterface interfcaeobj) {
        this.context = context;
        this.storeOfflineModels = storeOfflineModels;
        this.isOfflineMode = isOfflineMode;
        this.interfcaeobj = interfcaeobj;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_select_image, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (isOfflineMode) {
            holder.btn_delete.setVisibility(View.VISIBLE);
            holder.btn_download.setVisibility(View.GONE);
            holder.img_main.setImageBitmap(BitmapFactory.decodeFile(storeOfflineModels.get(position)));

            holder.btn_delete.setOnClickListener(view -> {
                if (interfcaeobj != null) {
                    interfcaeobj.onDeleteImage(view, storeOfflineModels.get(position));
                }
            });
        } else {
            holder.btn_delete.setVisibility(View.GONE);

            if (SubImage.subImgaeList != null) {
                if (SubImage.subImgaeList.size() > 0) {
                    String path;
                    if (image_type.equals(context.getString(R.string.easy))) {
                        path =  Constants.getRootDirectory(context)  + SAVED_IMG_PATH_EASY;
                    } else {
                        path =  Constants.getRootDirectory(context)  + Constants.SAVED_IMG_PATH_HARD;
                    }

                    File dir = new File(path);
                    Log.e("imgList1", "" + imgList1.get(position).image);
                    File saveFile1 = new File(dir, category_name + imgList1.get(position).image);
                    Log.e("saveFile==", "" + saveFile1.getAbsolutePath() + "====");

                    if (SubImage.subImgaeList.contains(saveFile1.getAbsolutePath())) {
                        holder.btn_download.setVisibility(View.GONE);
                    } else {
                        holder.btn_download.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.btn_download.setVisibility(View.VISIBLE);
                }
            }
            Glide.with(context)
                    .asBitmap()
                    .load(Constants.UPLOAD_URL + imgList1.get(position).image)
                    .skipMemoryCache(true)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            Log.e("resource", "" + resource);

                            holder.img_main.setImageBitmap(scaleBitmap(resource));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });


            holder.btn_download.setOnClickListener(v -> {
                if (interfcaeobj != null) {
                    interfcaeobj.onDownloadClick(v, position);
                }
            });
        }
    }


    private Bitmap scaleBitmap(Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);

        float scaleX = 300 / (float) bitmap.getWidth();
        float scaleY = 300 / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    @Override
    public int getItemCount() {
        if (isOfflineMode) {
            return storeOfflineModels.size();
        } else {
            return imgList1.size();
        }
    }

    public interface ClickInterface {
        void recItemClick(View view, int i, boolean isDownload, String path);

        void onDownloadClick(View view, int i);

        void onDeleteImage(View view, String path);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_main, btn_download, btn_delete;

        MyViewHolder(View itemView) {
            super(itemView);
            btn_download = itemView.findViewById(R.id.btn_download);
            img_main = itemView.findViewById(R.id.img_main);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (interfcaeobj != null) {


                String path =  Constants.getRootDirectory(context)  + SAVED_IMG_PATH;


                File dir = new File(path);

                File saveFile1;
                boolean isDownload = false;
                if (!isOfflineMode) {
                    saveFile1 = new File(dir, imgList1.get(getAdapterPosition()).image);

                    if (SubImage.subImgaeList.size() > 0) {
                        isDownload = SubImage.subImgaeList.contains(saveFile1.getAbsolutePath());
                    }
                } else {
                    saveFile1 = new File(path);
                }

                interfcaeobj.recItemClick(view, getAdapterPosition(), isDownload, saveFile1.getAbsolutePath());
            }
        }
    }
}
