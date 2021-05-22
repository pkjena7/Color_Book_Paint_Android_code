package com.templatevilla.colorbookspaint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.templatevilla.colorbookspaint.R;
import com.templatevilla.colorbookspaint.util.Constants;

import java.util.List;

public class MagicAdapter extends RecyclerView.Adapter<MagicAdapter.MyViewHolder> {

    private List<String> stringList;
    private Context context;
    private clickInterface anInterface;
    private int old_pos = 0;


    public MagicAdapter(List<String> stringList, Context context, clickInterface clickInterface) {
        this.stringList = stringList;
        this.context = context;
        this.anInterface = clickInterface;
    }


    public void setListeners(clickInterface anInterface1) {
        anInterface = anInterface1;
    }

    public interface clickInterface {

        void onMagicBrushClick(View view, int i, String s);
    }

    public void setPos(int old_pos) {
        this.old_pos = old_pos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_magic_brush, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        if (old_pos == i) {
            Animation animator = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
            myViewHolder.layout_frame.setAnimation(animator);
            animator.start();
        }



        myViewHolder.imageView.setImageBitmap(Constants.getBitmapFromAsset(context,"magicbrush/"+stringList.get(i)));


        myViewHolder.imageView.setOnClickListener(v -> {
            if (anInterface != null) {
                Animation animator = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
                myViewHolder.layout_frame.setAnimation(animator);
                animator.start();
                notifyItemChanged(old_pos);
                old_pos = i;
                anInterface.onMagicBrushClick(v, i, stringList.get(i));


            }
        });
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        FrameLayout layout_frame;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            layout_frame = itemView.findViewById(R.id.layout_frame);
        }

    }
}
