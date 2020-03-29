package ca.bcit.royalcitybuildinglens;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;


public class ARActivity extends AppCompatActivity {
    private static final String TAG = "ARActivity";
    private ArSceneView arSceneView;
    private ArrayList<ViewRenderable> buildingRenderables = new ArrayList<ViewRenderable>();

    private ViewRenderable buildingRenderable0;
    private ViewRenderable buildingRenderable1;
    private ViewRenderable buildingRenderable2;

    private LocationScene locationScene;
    private boolean hasFinishedLoading = false;
    private Snackbar loadingMessageSnackbar = null;
    private boolean installRequested;

    private ArrayList<Building> buildings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arSceneView = findViewById(R.id.ar_scene_view);

        Intent i = getIntent();
        String buildingsJson = i.getStringExtra("buildings");
        Type buildingsType = new TypeToken<ArrayList<Building>>(){}.getType();
        buildings = new Gson().fromJson(buildingsJson, buildingsType);
        buildings.forEach(Building::restoreLocation);
        buildings.forEach(System.out::println);

        // Build building information renderable from building_info_Layout.xml
        CompletableFuture<ViewRenderable> buildingLayout0 =
                ViewRenderable.builder()
                        .setView(this, R.layout.building_info_layout0)
                        .build();
        CompletableFuture<ViewRenderable> buildingLayout1 =
                ViewRenderable.builder()
                        .setView(this, R.layout.building_info_layout1)
                        .build();

        CompletableFuture<ViewRenderable> buildingLayout2 =
                ViewRenderable.builder()
                        .setView(this, R.layout.building_info_layout2)
                        .build();

        CompletableFuture.allOf(
                buildingLayout0,
                buildingLayout1,
                buildingLayout2)
                .handle(
                        (notUsed, throwable) -> {
                            // When Renderable is builded, Sceneform loads its resources
                            // in the background while returning a CompletableFuture.
                            // Call handle(), thenAccept(), or check isDone() before calling get().

                            if (throwable != null) {
                                Toast.makeText(
                                        this, "Unable to load renderables. "+ throwable.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                                return null;
                            }

                            try {
                                buildingRenderable0 = buildingLayout0.get();
                                buildingRenderable1 = buildingLayout1.get();
                                buildingRenderable2 = buildingLayout2.get();

                                buildingRenderables.add(buildingRenderable0);
                                buildingRenderables.add(buildingRenderable1);
                                buildingRenderables.add(buildingRenderable2);
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                Toast.makeText(
                                        this, "Unable to load renderables. "+ ex.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                            }

                            return null;
                        });
        arSceneView
                .getScene()
                .setOnUpdateListener(
                        frameTime -> {
                            if (!hasFinishedLoading) {
                                return;
                            }

                            if (locationScene == null) {

                                locationScene = new LocationScene(this, this, arSceneView);
                                ArrayList<LocationMarker> locationMarkers = new ArrayList<LocationMarker>();

                                // Building building information with layout( getBuildingView() )
                                for(int i = 0; i < 3; i ++){
                                    LocationMarker layoutLocationMarker = new LocationMarker(
                                            buildings.get(i).getLocation().getLongitude(),
                                            buildings.get(i).getLocation().getLatitude(),
                                            getBuildingView(i)
                                    );
                                    locationMarkers.add(layoutLocationMarker);
                                }
                                // An example "onRender" event, called every frame
                                // Updates the layout with the markers distance
                                locationMarkers.get(0).setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        View eView = buildingRenderable0.getView();
                                        TextView attrTextView = eView.findViewById(R.id.building_attr0);
                                        TextView distanceTextView = eView.findViewById(R.id.building_dist0);
                                        String attr = "ID: "+ buildings.get(0).getId()+"\n"
                                                + "MapRef: " + buildings.get(0).getMapRef()+"\n"
                                                + "Address: " + buildings.get(0).getStreetName()+"\n"
                                                + "Builder: " + buildings.get(0).getDeveloper()+"\n"
                                                + "Architect: " + buildings.get(0).getArchitect()+"\n"
                                                + "Year Built: " + buildings.get(0).getYearBuilt()+"\n"
                                                + "Year Moved: " + buildings.get(0).getYearMoved();
                                        attrTextView.setText(attr);
                                        distanceTextView.setText(node.getDistance() + "M");
                                    }
                                });
                                locationMarkers.get(1).setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        View eView = buildingRenderable1.getView();
                                        TextView attrTextView = eView.findViewById(R.id.building_attr1);
                                        TextView distanceTextView = eView.findViewById(R.id.building_dist1);
                                        String attr = "ID: "+ buildings.get(1).getId()+"\n"
                                                + "MapRef: " + buildings.get(1).getMapRef()+"\n"
                                                + "Address: " + buildings.get(1).getStreetName()+"\n"
                                                + "Builder: " + buildings.get(1).getDeveloper()+"\n"
                                                + "Architect: " + buildings.get(1).getArchitect()+"\n"
                                                + "Year Built: " + buildings.get(1).getYearBuilt()+"\n"
                                                + "Year Moved: " + buildings.get(1).getYearMoved();
                                        attrTextView.setText(attr);
                                        distanceTextView.setText(node.getDistance() + "M");
                                    }
                                });
                                locationMarkers.get(2).setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        View eView = buildingRenderable2.getView();
                                        TextView attrTextView = eView.findViewById(R.id.building_attr2);
                                        TextView distanceTextView = eView.findViewById(R.id.building_dist2);
                                        String attr = "ID: "+ buildings.get(2).getId()+"\n"
                                                + "MapRef: " + buildings.get(2).getMapRef()+"\n"
                                                + "Address: " + buildings.get(2).getStreetName()+"\n"
                                                + "Builder: " + buildings.get(2).getDeveloper()+"\n"
                                                + "Architect: " + buildings.get(2).getArchitect()+"\n"
                                                + "Year Built: " + buildings.get(2).getYearBuilt()+"\n"
                                                + "Year Moved: " + buildings.get(2).getYearMoved();
                                        attrTextView.setText(attr);

                                        distanceTextView.setText(node.getDistance() + "M");
                                    }
                                });
                                // Adding the marker
                                locationScene.mLocationMarkers.addAll(locationMarkers);
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            if (locationScene != null) {
                                locationScene.processFrame(frame);
                            }

                            if (loadingMessageSnackbar != null) {
                                for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                                        hideLoadingMessage();
                                    }
                                }
                            }
                        });
        ARLocationPermissionHelper.requestPermission(this);

    }
    private Node getBuildingView(int index) {
        Node base = new Node();
        base.setRenderable(buildingRenderables.get(index));
        Context c = this;
        // Add  listeners etc here
        View eView = buildingRenderable0.getView();
        eView.setOnTouchListener((v, event) -> {
            Toast.makeText(
                    c, "Touched.", Toast.LENGTH_LONG)
                    .show();
            return false;
        });
        return base;
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            Toast.makeText(
                    this, "Unable to get camera", Toast.LENGTH_LONG)
                    .show();
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }

    }
    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ARActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }
    public static Session createArSession(Activity activity, boolean installRequested)
            throws UnavailableException {
        Session session = null;
        // if we have the camera permission, create the session
        if (ARLocationPermissionHelper.hasPermission(activity)) {
            switch (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
                case INSTALL_REQUESTED:
                    return null;
                case INSTALLED:
                    break;
            }
            session = new Session(activity);
            // IMPORTANT!!!  ArSceneView needs to use the non-blocking update mode.
            Config config = new Config(session);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            session.configure(config);
        }
        return session;
    }

    public static void handleSessionException(
            Activity activity, UnavailableException sessionException) {

        String message;
        if (sessionException instanceof UnavailableArcoreNotInstalledException) {
            message = "Please install ARCore";
        } else if (sessionException instanceof UnavailableApkTooOldException) {
            message = "Please update ARCore";
        } else if (sessionException instanceof UnavailableSdkTooOldException) {
            message = "Please update this app";
        } else if (sessionException instanceof UnavailableDeviceNotCompatibleException) {
            message = "This device does not support AR";
        } else {
            message = "Failed to create AR session";
            Log.e(TAG, "Exception: " + sessionException);
        }
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}
