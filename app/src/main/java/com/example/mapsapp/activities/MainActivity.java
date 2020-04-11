package com.example.mapsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsapp.R;
import com.example.mapsapp.adapters.MapTypeRecyclerViewAdapter;
import com.example.mapsapp.databinding.ActivityMainBinding;
import com.example.mapsapp.models.EMaps;
import com.example.mapsapp.models.MapType;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initListOfMaps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.item_menu_exit:
                finish();
        }
        return true;
    }

    private void initListOfMaps() {
        RecyclerView recyclerView = mBinding.recyclerViewTypeMap;
        MapTypeRecyclerViewAdapter adapter =
                new MapTypeRecyclerViewAdapter(MainActivity.this);

        adapter.addMap(new MapType(
                EMaps.NORMAL_LIGHT.getType(),
                R.drawable.ic_map_type_normal_light,
                "Normal Light"));
        adapter.addMap(new MapType(
                EMaps.NORMAL_SILVER.getType(),
                R.drawable.ic_map_type_normal_silver,
                "Normal Silver"));
                adapter.addMap(new MapType(
                EMaps.NORMAL_RETRO.getType(),
                R.drawable.ic_map_type_normal_retro,
                "Normal Retro"));
                adapter.addMap(new MapType(
                EMaps.NORMAL_AUBERGINE.getType(),
                R.drawable.ic_map_type_normal_aubergine,
                "Normal Aubergine"));
        adapter.addMap(new MapType(
                EMaps.NORMAL_DARK.getType(),
                R.drawable.ic_map_type_normal_dark,
                "Normal Dark"));
        adapter.addMap(new MapType(
                EMaps.SATELLITE.getType(),
                R.drawable.ic_map_type_satellite,
                "Satellite"));
        adapter.addMap(new MapType(
                EMaps.HYBRID.getType(),
                R.drawable.ic_map_type_hybrid,
                "Hybrid"));
        adapter.addMap(new MapType(
                EMaps.TERRAIN.getType(),
                R.drawable.ic_map_type_terrain,
                "Terrain"));

        recyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MapTypeRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(MapType mapType) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("map_type", mapType.getType());
                startActivity(intent);
            }
        });
    }

}
