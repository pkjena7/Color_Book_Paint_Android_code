package com.templatevilla.colorbookspaint.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.ui.SelectStickerActivity;


/**
 * Created by bhavika on 2/14/2017.
 */

public class ParentStickerAdapter extends RecyclerView.Adapter<ParentStickerAdapter.ViewHolder> {


    private clickInterface clickInterfaceobj;
    private String[] folder;
    private int[] ints;


    public ParentStickerAdapter(int[] ints, String[] data, clickInterface clickInterfaceobj) {
        folder = data;
        this.ints = ints;
        this.clickInterfaceobj = clickInterfaceobj;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_parent_sticker, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.img_sticker.setImageResource(ints[position]);


        if (SelectStickerActivity.sticker_position == position) {
            holder.view.setVisibility(View.VISIBLE);
        } else {
            holder.view.setVisibility(View.GONE);
        }


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
        View view;

        ViewHolder(final View v) {
            super(v);
            img_sticker = v.findViewById(R.id.img_sticker);
            view = v.findViewById(R.id.view_v);


            v.setOnClickListener(view -> {
                if (clickInterfaceobj != null) {
                    clickInterfaceobj.itemClick(view, getAdapterPosition());
                }
            });
        }

    }


}
