package enjoysmile.com.cookview;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import enjoysmile.com.cookview.model.Image;

public class GalleryActivity extends AppCompatActivity {
    private CoordinatorLayout mCoordinatorLayout;
    private ArrayList<Image> mGalleryImages;
    private GalleryAdapter mGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // used for coordinating snack bars
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_gallery_coordinator_layout);

        // empty image array list
        mGalleryImages = new ArrayList<>();

        // create a new adapter
        mGalleryAdapter = new GalleryAdapter(this, mGalleryImages);

        // find the grid recycler view
        RecyclerView galleryGrid = (RecyclerView) findViewById(R.id.gallery_grid);

        // set grid to 3 columns
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        galleryGrid.setLayoutManager(mLayoutManager);
        galleryGrid.setItemAnimator(new DefaultItemAnimator());
        galleryGrid.setAdapter(mGalleryAdapter);

        // get gallery images
        getGallery();
    }

    private void getGallery() {
        // loading snack bar
        Snackbar.make(mCoordinatorLayout, getString(R.string.get_image_snackbar), Snackbar.LENGTH_SHORT).show();

        // build the object request
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.imgur_api_endpoint),
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("RESPONSE", response.toString());
                        try {
                            JSONArray galleryArray = response.getJSONArray("data");

                            // loop through images
                            for (int i = 0; i < galleryArray.length(); i++) {
                                JSONObject imageObject = galleryArray.getJSONObject(i);
                                Image image = new Image();

                                String imageId;

                                // determine if it's an album
                                if (imageObject.getBoolean("is_album")) {
                                    imageId = imageObject.getString("cover");
                                } else {
                                    imageId = imageObject.getString("id");
                                }

                                // set image information
                                image.setLink(String.format(Locale.getDefault(), "%s%s.jpg", getString(R.string.imgur_base_url), imageId));
                                image.setLinkSmall(String.format(Locale.getDefault(), "%s%sm.jpg", getString(R.string.imgur_base_url), imageId));
                                image.setName(imageObject.getString("title"));
                                image.setId(imageObject.getString("id"));
                                image.setViews(imageObject.getInt("views"));
                                image.setDate(TimeUnit.SECONDS.toMillis(imageObject.getInt("datetime")));
                                image.setUploader(imageObject.isNull("account_url") ? // set to username or anonymous
                                        getString(R.string.anonymous_user) :
                                        imageObject.getString("account_url"));

                                // add into our list
                                mGalleryImages.add(image);
                            }

                            // notify the adapter
                            mGalleryAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.e("JSON Failure", e.getMessage());
                            // malformed data error snack bar
                            Snackbar.make(mCoordinatorLayout, getString(R.string.get_image_snackbar_malformed_error), Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e("NETWORK Failure", String.format(Locale.getDefault(), "%d", e.networkResponse.statusCode));
                        // connection error snack bar
                        Snackbar.make(mCoordinatorLayout, getString(R.string.get_image_snackbar_connection_error), Snackbar.LENGTH_LONG)
                                .show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization",
                        String.format(Locale.getDefault(), "Client-Id %s", BuildConfig.IMGUR_API_KEY));
                return params;

            }
        };

        // queue our request with Volley
        ApplicationController.getInstance().addToRequestQueue(jsObjRequest);
    }
}