package com.example.mapsapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mapsapp.R;
import com.example.mapsapp.models.MapType;

import java.util.ArrayList;
import java.util.List;

public class MapTypeRecyclerViewAdapter
        extends RecyclerView.Adapter<MapTypeRecyclerViewAdapter.MapTypeViewHolder> {

    private List<MapType> mListTypeMaps = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public MapTypeRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public MapTypeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_type_map, viewGroup, false);
        return new MapTypeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MapTypeViewHolder mapTypeViewHolder, int i) {
        mapTypeViewHolder.mImage.setBackgroundResource(mListTypeMaps.get(i).getSrcImage());
        mapTypeViewHolder.mText.setText(mListTypeMaps.get(i).getTypeName());
    }

    @Override
    public int getItemCount() {
        return mListTypeMaps.size();
    }

    public void addMap(MapType mapType) {
        mListTypeMaps.add(mapType);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class MapTypeViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mText;
        private CardView cardView;

        MapTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.image_row_type_map);
            mText = itemView.findViewById(R.id.text_row_type_map);
            cardView = itemView.findViewById(R.id.card_row_view_map);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClickListener(mListTypeMaps.get(getAdapterPosition()));
                    }
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onItemClickListener(MapType mapType);
    }

}
