package com.example.mapsapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mapsapp.R;
import com.example.mapsapp.models.SearchPlace;

import java.util.ArrayList;
import java.util.List;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder> {

    private Context mContext;
    private List<SearchPlace> mList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public SearchRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_search, viewGroup, false);
        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int i) {
        String text =
                mList.get(i).getName() + ", " +
                mList.get(i).getSubName() + ", " +
                mList.get(i).getSubArea() + ", " +
                mList.get(i).getCountryCode() + ", " +
                mList.get(i).getCountryName();
        searchViewHolder.mTextName.setText(text);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addPlace(SearchPlace place) {
        mList.add(place);
    }

    public void clearAll() {
        mList.clear();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextName;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextName = itemView.findViewById(R.id.text_row_search_name);

            mTextName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.setOnItemClickListener(
                                mList.get(getAdapterPosition()).getLat(),
                                mList.get(getAdapterPosition()).getLong());
                    }
                }
            });
        }

    }

    public interface OnItemClickListener {
        void setOnItemClickListener(double latitudes, double longitudes);
    }

}
