package com.templatevilla.colorbookspaint.adapter;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.ui.SelectStickerActivity;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by bhavika on s/14/2017.
 */

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {


    private clickInterface clickInterfaceobj;
    private String[] folder;
    private String foldern;

    private Activity ctx;


    public StickerAdapter(String[] data, String s, Activity ctx, clickInterface clickInterfaceobj) {
        folder = data;
        this.ctx = ctx;
        foldern = s;
        this.clickInterfaceobj = clickInterfaceobj;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String imgPath = foldern + "/" + folder[position];
        Bitmap bitmap = getBitmapFromAsset(imgPath);


        if (SelectStickerActivity.mSelectedImages.contains(imgPath)) {
            holder.img_checkbox.setBackgroundResource(R.drawable.ic_check_box_black_24dp);
        } else {
            holder.img_checkbox.setBackgroundResource(R.drawable.ic_check_box_outline_blank_black_24dp);

        }
        holder.img_sticker.setImageBitmap(bitmap);

    }

    private Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = ctx.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(istr);
    }

    @Override
    public int getItemCount() {
        return folder.length;
    }

    public interface clickInterface {
        void itemClick(View view, int i);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_sticker;
        ImageView img_checkbox;

        ViewHolder(final View v) {
            super(v);
            img_sticker = v.findViewById(R.id.img_sticker);
            img_checkbox = v.findViewById(R.id.img_checkbox);


            v.setOnClickListener(view -> {
                if (clickInterfaceobj != null) {
                    clickInterfaceobj.itemClick(view, getAdapterPosition());
                }
            });
        }

    }


}
