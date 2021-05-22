package com.templatevilla.colorbookspaint.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;


import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.model.MainImages;
import com.templatevilla.colorbookspaint.model.StoreOfflineModel;
import com.templatevilla.colorbookspaint.util.Constants;

import java.util.List;


public class SelectImageAdapter extends RecyclerView.Adapter<SelectImageAdapter.MyViewHolder> {
    private Context context;
    private List<MainImages> imgList1;
    private List<StoreOfflineModel> storeOfflineModels;
    private ClickInterface interfcaeobj;
    private boolean isOffLine;

    public SelectImageAdapter(Context context, List<MainImages> imgList, ClickInterface interfcaeobj) {
        this.context = context;
        this.imgList1 = imgList;
        this.interfcaeobj = interfcaeobj;
    }

    public SelectImageAdapter(Context context, boolean isOffLine, List<StoreOfflineModel> imgList, ClickInterface interfcaeobj) {
        this.context = context;
        this.storeOfflineModels = imgList;
        this.isOffLine = isOffLine;
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

        holder.textView.setVisibility(View.VISIBLE);

        if (isOffLine) {


            holder.cell.setVisibility(View.VISIBLE);
            holder.textView.setText(storeOfflineModels.get(position).name);
            holder.img_main.setImageBitmap(BitmapFactory.decodeFile(storeOfflineModels.get(position).path));


        } else {
            holder.textView.setText(imgList1.get(position).name);

            Log.e("imgList1",""+ imgList1.get(position).image);
            Glide.with(context)
                    .asBitmap()
                    .load(Constants.UPLOAD_URL + imgList1.get(position).image)
                    .skipMemoryCache(true)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.img_main.setImageBitmap(scaleBitmap(resource));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
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
        if (isOffLine) {
            return storeOfflineModels.size();
        } else {
            return imgList1.size();
        }
    }

    public interface ClickInterface {
        void recItemClick(View view, int i);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_main;
        CardView cell;
        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            img_main = itemView.findViewById(R.id.img_main);
            cell = itemView.findViewById(R.id.cell);
            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (interfcaeobj != null) {
                interfcaeobj.recItemClick(view, getAdapterPosition());
            }
        }
    }
}
