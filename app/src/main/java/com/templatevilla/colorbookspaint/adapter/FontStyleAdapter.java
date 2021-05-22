package com.templatevilla.colorbookspaint.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.templatevilla.colorbookspaint.R;


public class FontStyleAdapter extends RecyclerView.Adapter<FontStyleAdapter.ViewHolder> {


    private String[] data;
    private int pos = 0;
    private Context ctx;
    private fontClick fontClick;

    public FontStyleAdapter(Context c, String[] data, fontClick fontClick) {
        ctx = c;
        this.data = data;
        this.fontClick = fontClick;


    }


    public void selectBg(int pos) {
        this.pos = pos;
        notifyDataSetChanged();
    }

    public interface fontClick {
        void onClick(View v, int pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_font, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        if (position == pos) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent));
            holder.tvFont.setTextColor(Color.WHITE);
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
            holder.tvFont.setTextColor(ContextCompat.getColor(ctx, R.color.colorAccent));

        }
        final Typeface titleFont = Typeface.
                createFromAsset(ctx.getAssets(), "font/" + data[position]);
        holder.tvFont.setTypeface(titleFont);


    }

    @Override
    public int getItemCount() {
        return data.length;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFont;
        LinearLayout lout_main;
        CardView cardView;


        ViewHolder(View v) {
            super(v);
            tvFont = v.findViewById(R.id.tvFont);
            lout_main = v.findViewById(R.id.lout_main);
            cardView = v.findViewById(R.id.cardView);

            v.setOnClickListener(v1 -> {
                if (fontClick != null) {
                    fontClick.onClick(v1, getAdapterPosition());
                }
            });
        }

    }
}
