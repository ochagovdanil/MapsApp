package com.example.mapsapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.mapsapp.R;
import com.example.mapsapp.fragments.InformationDialogFragment;
import com.example.mapsapp.fragments.PinEditDialogFragment;
import com.example.mapsapp.fragments.PinTitleDialogFragment;
import com.example.mapsapp.helpers.DBHelper;
import com.example.mapsapp.models.EMaps;
import com.example.mapsapp.models.RestoreData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity
                         implements OnMapReadyCallback {

    // types of maps
    private static final int NORMAL_LIGHT_TYPE = EMaps.NORMAL_LIGHT.getType();
    private static final int NORMAL_SILVER_TYPE = EMaps.NORMAL_SILVER.getType();
    private static final int NORMAL_RETRO_TYPE = EMaps.NORMAL_RETRO.getType();
    private static final int NORMAL_AUBERGINE_TYPE = EMaps.NORMAL_AUBERGINE.getType();
    private static final int NORMAL_DARK_TYPE = EMaps.NORMAL_DARK.getType();
    private static final int HYBRID_TYPE = EMaps.HYBRID.getType();
    private static final int TERRAIN_TYPE = EMaps.TERRAIN.getType();
    private static final int SATELLITE_TYPE = EMaps.SATELLITE.getType();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;

    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(40.7143528, -74.0059731); // new york
    private RestoreData mRestoreData = null; // the parcelable object
    private SearchView mSearchView;

    private DBHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initToolbar();

        mDbHelper = new DBHelper(MapActivity.this);
        mDatabase = mDbHelper.getWritableDatabase();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSearchView = findViewById(R.id.search_view_places);

        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);

        // restore the traffic
        MenuItem menuItem = menu.findItem(R.id.item_menu_traffic);

        if (mRestoreData != null) {
            if (mRestoreData.getTraffic()) {
                mGoogleMap.setTrafficEnabled(true);
                menuItem.setChecked(true);
            } else {
                mGoogleMap.setTrafficEnabled(false);
                menuItem.setChecked(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_traffic:
                // set the traffic
                if (!item.isChecked()) {
                    mGoogleMap.setTrafficEnabled(true);
                    item.setChecked(true);
                } else {
                    mGoogleMap.setTrafficEnabled(false);
                    item.setChecked(false);
                }
                break;
            case R.id.item_menu_clean_map:
                mGoogleMap.clear();
                mDatabase.delete("maps", null, null);

                Toast.makeText(
                        MapActivity.this,
                        "The map was cleaned!",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_menu_off_app:
                openOfficialApp();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // saving the last zoom, coordinates, pins and enabled traffic
        double lat = mGoogleMap.getCameraPosition().target.latitude;
        double lon = mGoogleMap.getCameraPosition().target.longitude;
        float zoom = mGoogleMap.getCameraPosition().zoom;

        RestoreData restoreData =
                new RestoreData(zoom, lat, lon, mGoogleMap.isTrafficEnabled());
        savedInstanceState.putParcelable("restore_data", restoreData);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            // get the last zoom, coordinates, pins and enabled traffic
            mRestoreData = savedInstanceState.getParcelable("restore_data");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        setTypeOfMap();
        restoreData(); // it's used after the screen rotation
        restoreAllMarkers();

        // set the ui settings
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true);

        makeSearch();

        addPin();
        editPin();
        movePin();

        showInformationAboutPlace();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }

        updateLocationUI();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_my);
        toolbar.setNavigationIcon(R.drawable.ic_menu_back);
        toolbar.setTitle(R.string.map);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setTypeOfMap() {
        int type = getIntent().getIntExtra("map_type", 0);

        if (type == NORMAL_LIGHT_TYPE) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return;
        }
        if (type == NORMAL_SILVER_TYPE) {
            mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    MapActivity.this,
                    R.raw.silver_mode));
            return;
        }if (type == NORMAL_RETRO_TYPE) {
            mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    MapActivity.this,
                    R.raw.retro_mode));
            return;
        }if (type == NORMAL_AUBERGINE_TYPE) {
            mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    MapActivity.this,
                    R.raw.aubergine_mode));
            return;
        }
        if (type == NORMAL_DARK_TYPE) {
            mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    MapActivity.this,
                    R.raw.dark_mode));
            return;
        }
        if (type == HYBRID_TYPE) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return;
        }
        if (type == TERRAIN_TYPE) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return;
        }
        if (type == SATELLITE_TYPE) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;

        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // move the camera and save a last know location
                            mLastKnownLocation = task.getResult();
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(
                                            mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()),
                                    DEFAULT_ZOOM));
                        }
                    }
                });
            } else {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        mDefaultLocation,
                        DEFAULT_ZOOM));
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void addPin() {
        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                // set a title before creating
                PinTitleDialogFragment dialog = new PinTitleDialogFragment();
                dialog.show(getSupportFragmentManager(), "PinTitleDialogFragment");
                dialog.setOnSavePinTitleDialogListener(
                        new PinTitleDialogFragment.PinTitleDialogListener() {
                    @Override
                    public void onSavePinTitleDialogListener(String text) {
                        if (!text.equals("")) {
                            if (titleExists(text)) {
                                showInformationDialog(getString(R.string.same_result));
                            } else {
                                // add the marker at the map
                                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                        .draggable(true)
                                        .position(latLng)
                                        .title(text));
                                marker.showInfoWindow();

                                // add the marker into the database
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("title", marker.getTitle());
                                contentValues.put("lat", marker.getPosition().latitude);
                                contentValues.put("lon", marker.getPosition().longitude);
                                mDatabase.insert("maps", null, contentValues);

                                // show a position of a new marker
                                Toast toast = Toast.makeText(
                                        MapActivity.this,
                                        marker.getPosition().toString(),
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(
                                        Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,
                                        0,
                                        50);
                                toast.show();
                            }
                        }
                    }
                });
            }
        });
    }

    // change a title
    private void editPin() {
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                Bundle bundle = new Bundle();
                bundle.putString("marker_title", marker.getTitle());
                PinEditDialogFragment dialog = new PinEditDialogFragment();
                dialog.setArguments(bundle);

                dialog.show(getSupportFragmentManager(), "PinEditDialogFragment");

                dialog.setOnSavePinEditDialogListener
                        (new PinEditDialogFragment.PinEditDialogListener() {
                    @Override
                    public void onSavePinEditDialogListener(String text) {
                        if (titleExists(text)) {
                            showInformationDialog(getString(R.string.same_result));
                        } else {
                            // update at the database
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("title", text);
                            contentValues.put("lat", marker.getPosition().latitude);
                            contentValues.put("lon", marker.getPosition().longitude);

                            mDatabase.update("maps",
                                    contentValues,
                                    "title = ? and lat = ? and lon = ?",
                                    new String[]{
                                            marker.getTitle(),
                                            String.valueOf(marker.getPosition().latitude),
                                            String.valueOf(marker.getPosition().longitude)});

                            // update at the map
                            marker.setTitle(text);
                        }
                    }
                });
            }
        });
    }

    private void movePin() {
        mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // update the database
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", marker.getTitle());
                contentValues.put("lat", marker.getPosition().latitude);
                contentValues.put("lon", marker.getPosition().longitude);
                mDatabase.update(
                        "maps",
                        contentValues,
                        "title = ?",
                        new String[]{marker.getTitle()});

                Toast.makeText(
                        MapActivity.this,
                        marker.getPosition().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openOfficialApp() {
        LatLng latLng = mGoogleMap.getCameraPosition().target;
        double lat = latLng.latitude;
        double lon = latLng.longitude;
        float zoom = mGoogleMap.getCameraPosition().zoom;

        // open the map via the current coordinates and zoom level
        Uri uri = Uri.parse("geo:" + lat + "," + lon + "?z=" + zoom);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    private void showInformationAboutPlace() {
        mGoogleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                String msg =
                        "Id: " + pointOfInterest.placeId +
                        "\nName: " + pointOfInterest.name +
                        "\nCoordinates: " + pointOfInterest.latLng.latitude +
                        ", " + pointOfInterest.latLng.longitude;
                showInformationDialog(msg);
            }
        });
    }

    private void restoreData() {
        if (mRestoreData != null) {
            // restore zoom and coordinates
            final LatLng latLng = new LatLng(mRestoreData.getLat(), mRestoreData.getLon());

            mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mGoogleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(latLng, mRestoreData.getZoom()));
                }
            });
        }
    }

    private void restoreAllMarkers() {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM maps", null);

        if (cursor.moveToFirst()) {
            do {
                mGoogleMap.addMarker(new MarkerOptions()
                        .title(cursor.getString(cursor.getColumnIndex("title")))
                        .position(new LatLng(
                                cursor.getDouble(cursor.getColumnIndex("lat")),
                                cursor.getDouble(cursor.getColumnIndex("lon"))))
                        .draggable(true));
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void makeSearch() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")) {
                    try {
                        Geocoder geocoder = new Geocoder(MapActivity.this);
                        List<Address> listAddress = geocoder.getFromLocationName(query, 1);

                        if (listAddress.size() > 0) {
                            Address address = listAddress.get(0);
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
                                    address.getLatitude(),
                                    address.getLongitude())));
                        } else {
                            showInformationDialog(getString(R.string.search_fail));
                        }

                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private boolean titleExists(String title) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM maps", null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex("title")).equals(title)) {
                    return true;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return false;
    }

    private void showInformationDialog(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", message);

        InformationDialogFragment dialog = new InformationDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "InformationDialogFragment");
    }

}