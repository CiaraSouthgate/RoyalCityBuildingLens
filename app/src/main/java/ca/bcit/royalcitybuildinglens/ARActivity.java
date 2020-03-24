package ca.bcit.royalcitybuildinglens;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.rendering.LocationNode;
import uk.co.appoly.arcorelocation.rendering.LocationNodeRender;


public class ARActivity extends AppCompatActivity {
    private ArSceneView arSceneView;
    private ViewRenderable buildingRenderable;
    private LocationScene locationScene;
    private boolean hasFinishedLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        CompletableFuture<ViewRenderable> buildingLayout =
                ViewRenderable.builder()
                .setView(this, R.layout.building_info_layout)
                .build();

        CompletableFuture.allOf(
                buildingLayout)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                Toast.makeText(
                                        this, "Unable to load renderables.", Toast.LENGTH_LONG)
                                        .show();
                                return null;
                            }

                            try {
                                buildingRenderable = buildingLayout.get();
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                Toast.makeText(
                                        this, "Unable to load renderables.", Toast.LENGTH_LONG)
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
                                // If our locationScene object hasn't been setup yet, this is a good time to do it
                                // We know that here, the AR components have been initiated.
                                locationScene = new LocationScene(this, this, arSceneView);

                                // Now lets create our location markers.
                                // First, a layout
                                LocationMarker layoutLocationMarker = new LocationMarker(
                                        -4.849509,
                                        42.814603,
                                        getBuildingView()
                                );

                                // An example "onRender" event, called every frame
                                // Updates the layout with the markers distance
                                layoutLocationMarker.setRenderEvent(new LocationNodeRender() {
                                    @Override
                                    public void render(LocationNode node) {
                                        View eView = buildingRenderable.getView();
                                        TextView distanceTextView = eView.findViewById(R.id.textView2);
                                        distanceTextView.setText(node.getDistance() + "M");
                                    }
                                });
                                // Adding the marker
                                locationScene.mLocationMarkers.add(layoutLocationMarker);

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

//                            if (loadingMessageSnackbar != null) {
//                                for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
//                                    if (plane.getTrackingState() == TrackingState.TRACKING) {
//                                        hideLoadingMessage();
//                                    }
//                                }
//                            }
                        });
    }
    private Node getBuildingView() {
        Node base = new Node();
        base.setRenderable(buildingRenderable);
        Context c = this;
        // Add  listeners etc here
        View eView = buildingRenderable.getView();
        eView.setOnTouchListener((v, event) -> {
            Toast.makeText(
                    c, "Location marker touched.", Toast.LENGTH_LONG)
                    .show();
            return false;
        });

        return base;
    }

}
