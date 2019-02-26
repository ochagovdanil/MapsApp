package com.example.mapsapp.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mapsapp.R;
import com.example.mapsapp.adapters.SearchRecyclerViewAdapter;
import com.example.mapsapp.models.SearchPlace;

import java.io.IOException;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initToolbar();
        initRecyclerView();

        final EditText inputSearch = findViewById(R.id.edit_search);
        Button buttonSearch = findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSearch(inputSearch.getText().toString());
            }
        });
        openPlaceInMap();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_my);
        toolbar.setTitle(R.string.search_for_places);
        toolbar.setNavigationIcon(R.drawable.ic_menu_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_search);
        mAdapter = new SearchRecyclerViewAdapter(SearchActivity.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
    }

    private void makeSearch(String query) {
        if (query.equals("")) {
            mAdapter.clearAll();
            mAdapter.notifyDataSetChanged();
        } else {
            // get results
            try {
                Geocoder geocoder = new Geocoder(SearchActivity.this);
                List<Address> listAddress = geocoder.getFromLocationName(query, 100);

                if (listAddress.size() > 0) {
                    // show results
                    for (int i = 0; i < listAddress.size(); i++) {
                        Address address = listAddress.get(i);
                        mAdapter.addPlace(new SearchPlace(
                                address.getLatitude(),
                                address.getLongitude(),
                                address.getLocality(),
                                address.getSubLocality(),
                                address.getSubAdminArea(),
                                address.getCountryCode(),
                                address.getCountryName()));
                    }

                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(
                            SearchActivity.this,
                            "No results",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openPlaceInMap() {
        mAdapter.setOnItemClickListener(new SearchRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClickListener(double latitudes, double longitudes) {
                Intent intent = new Intent();
                intent.putExtra("place_lat", latitudes);
                intent.putExtra("place_long", longitudes);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
