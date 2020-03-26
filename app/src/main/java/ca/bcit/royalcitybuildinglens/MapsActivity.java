package ca.bcit.royalcitybuildinglens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private CardView loadingCard;
    private TextView loadingText;
    private ProgressBar progressBar;
    private Button arButton;
    private Button tryAgainButton;
    private boolean errorDisplayed;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private JSONObject bldgAttributes;
    private JSONObject bldgAge;

    private Gson gson = new Gson();

    private HashMap<Integer, Building> buildings;
    private ArrayList<Building> sortedBuildings;

    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        loadingCard = findViewById(R.id.loadingCard);
        loadingText = findViewById(R.id.loadingText);
        progressBar = findViewById(R.id.progressBar);
        arButton = findViewById(R.id.arButton);
        tryAgainButton = findViewById(R.id.tryAgainButton);

        errorDisplayed = false;

        bldgAttributes = null;
        bldgAge = null;

        buildings = new HashMap<>();

        readData();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void readData() {
        new DownloadFileFromURL().execute(getString(R.string.building_attr_url));
        new DownloadFileFromURL().execute(getString(R.string.building_age_url));
    }

    private void createBuildingObjects() {
        try {
            JSONArray attrData = bldgAttributes.getJSONArray("features");
            JSONArray ageData = bldgAge.getJSONArray("features");
            for (int i = 0; i < attrData.length(); i++) {
                JSONObject jsonBldg = attrData.getJSONObject(i);
                Building bldg = gson.fromJson(jsonBldg.get("properties").toString(), Building.class);

                // Extracts array of coordinates from JSONObject and sets building coordinates to
                // JSONArray value
                JSONObject geo = (JSONObject) jsonBldg.get("geometry");
                JSONArray coordinates = (JSONArray) geo.get("coordinates");
                JSONArray inner_coordinates = (JSONArray) coordinates.get(0);
                bldg.setCoordinates(inner_coordinates);

                buildings.put(bldg.getId(), bldg);
            }
            for (int i = 0; i < ageData.length(); i++) {
                JSONObject jsonBldg = ageData.getJSONObject(i);
                Building bldg = gson.fromJson(jsonBldg.get("properties").toString(), Building.class);
                if (buildings.containsKey(bldg.getId())) {
                    buildings.get(bldg.getId()).merge(bldg);
                } else {
                    buildings.put(bldg.getId(), bldg);
                }
            }
            sortBuildingsByNearest();
            System.out.println("NUMBER OF BUILDINGS: " + buildings.size());
            clearLoadingCard();
        } catch (JSONException e) {
            e.printStackTrace();
            setLoadingError();
        }
    }

    private void clearLoadingCard() {
        loadingCard.setVisibility(View.GONE);
        arButton.setVisibility(View.VISIBLE);
    }

    private void setLoadingError() {
        if (!errorDisplayed) {
            loadingText.setText(getString(R.string.loading_failed));
            progressBar.setVisibility(View.GONE);
            tryAgainButton.setVisibility(View.VISIBLE);
            errorDisplayed = true;
        }
    }

    public void onTryAgain(View v) {
        loadingText.setText(getString(R.string.loading_data));
        progressBar.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.GONE);
        errorDisplayed = false;

        readData();
    }

    private void sortBuildingsByNearest() {
        Comparator<Building> compareByLocation = new Comparator<Building>() {
            @Override
            public int compare(Building b1, Building b2) {
                Float b1Distance = currentLocation.distanceTo(b1.getLocation());
                Float b2Distance = currentLocation.distanceTo(b2.getLocation());
                return b1Distance.compareTo(b2Distance);
            }
        };

        ArrayList<Building> buildingList = new ArrayList<>(buildings.values());
        Collections.sort(buildingList, compareByLocation);
        sortedBuildings = buildingList;

        // For testing
        Building closestBuilding = sortedBuildings.get(0);
        System.out.println("The closest building is at " + closestBuilding.getStreetNum() + " " + closestBuilding.getStreetName());
    }

    /**
     * Adds a circle on the map at the given location
     * @param location Location
     */
    private void addLocationToMap(Location location) {

        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear();
        mMap.addCircle(new CircleOptions()
                .center(latlng)
                .radius(5)
                .strokeColor(Color.WHITE)
                .fillColor(Color.BLUE));

        int zoomLevel = 18;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoomLevel));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                addLocationToMap(lastKnownLocation);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Disable scrolling & rotating
        UiSettings settings = mMap.getUiSettings();
        settings.setScrollGesturesEnabled(false);
        settings.setRotateGesturesEnabled(false);

        // Zoom into users location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                addLocationToMap(location);
                if (buildings.size() > 0) {
//                    sortBuildingsByNearest();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, locationListener);
            Location lastKnownLocation = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            addLocationToMap(lastKnownLocation);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    /**
     * Open AR activity
     * @param view view
     */
    public void onClickAR(View view) {
        Intent intent = new Intent(this, ARActivity.class);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadFileFromURL extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                return new JSONObject(builder.toString());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(MapsActivity.this::setLoadingError);
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                String name = result.getString("name");
                if (name.equals("BUILDING_ATTRIBUTES"))
                    bldgAttributes = result;
                if (name.equals("BUILDING_AGE"))
                    bldgAge = result;
                if (bldgAttributes != null && bldgAge != null)
                    createBuildingObjects();

            } catch (JSONException|NullPointerException e) {
                e.printStackTrace();
                runOnUiThread(MapsActivity.this::setLoadingError);
            }
        }
    }
}
