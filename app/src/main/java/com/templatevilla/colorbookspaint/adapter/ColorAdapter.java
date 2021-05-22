package com.templatevilla.colorbookspaint.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.templatevilla.colorbookspaint.R;

import java.util.List;
import java.util.Objects;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.MyViewHolder> {

    private List<String> stringList;
    private Context context;
    private clickInterface anInterface;
    private int old_pos = 0;
    private boolean isBrush;


    public ColorAdapter(List<String> stringList, Context context, boolean isBrush) {
        this.stringList = stringList;
        this.context = context;
        this.isBrush = isBrush;


    }

    public void setListeners(clickInterface anInterface1) {
        anInterface = anInterface1;
    }

    public interface clickInterface {
        void onColorPencilClick(View view, int i, String s);

        void onColorBrushClick(View view, int i, String s);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (isBrush) {
            view = LayoutInflater.from(context).inflate(R.layout.item_brush_color, viewGroup, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_pencil_color, viewGroup, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        if (old_pos == i) {
            Animation animator = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
            myViewHolder.layout_frame.setAnimation(animator);
            animator.start();
        }
        Drawable drawable;

        if (isBrush) {
            drawable = Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.frount_2)).mutate();
        } else {
            drawable = Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.pencil_back)).mutate();
        }
        int color = Color.parseColor(stringList.get(i));
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        myViewHolder.img_pencil.setImageDrawable(drawable);
        myViewHolder.img_pencil.setOnClickListener(v -> {
            if (anInterface != null) {
                Animation animator = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
                myViewHolder.layout_frame.setAnimation(animator);
                animator.start();
                notifyItemChanged(old_pos);
                old_pos = i;
                if (isBrush) {
                    anInterface.onColorBrushClick(v, i, stringList.get(i));
                } else {
                    anInterface.onColorPencilClick(v, i, stringList.get(i));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_pencil;
        FrameLayout layout_frame;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_pencil = itemView.findViewById(R.id.img_pencil);
            layout_frame = itemView.findViewById(R.id.layout_frame);
        }

    }
}
