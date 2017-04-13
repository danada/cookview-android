package enjoysmile.com.cookview;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import enjoysmile.com.cookview.model.Comment;
import enjoysmile.com.cookview.model.Image;

public class ImageActivity extends AppCompatActivity {
    ArrayList<Comment> mComments;
    ImageCommentAdapter mImageCommentAdapter;
    Image mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // get references to UI elements
        TextView uploaderLabel = (TextView) findViewById(R.id.uploader_label);
        TextView viewLabel = (TextView) findViewById(R.id.view_label);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);

        // get passed image
        Intent intent = getIntent();
        mImage = (Image) intent.getSerializableExtra("image");

        // set transition if we have the API
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition));
//            imageView.setTransitionName("thumbnailTransition" + mImage.getId());
//        }

        // load the image with glide
        Glide.with(getApplicationContext())
                .load(mImage.getLink())
                .dontAnimate()
                .thumbnail(Glide.with(getApplicationContext()) // use the thumbnail we loaded before
                        .load(mImage.getLinkSmall())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        // load comments
                        getComments();
                        return false;
                    }
                })
                .into(imageView);



        // comment adapter
        RecyclerView commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        mComments = new ArrayList<>();
        mImageCommentAdapter = new ImageCommentAdapter(mComments);
        commentRecyclerView.setAdapter(mImageCommentAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        commentRecyclerView.setLayoutManager(layoutManager);
        commentRecyclerView.setNestedScrollingEnabled(false);

        // set first comment
        Comment uploaderComment = new Comment();
        uploaderComment.setUsername(mImage.getUploader());
        uploaderComment.setText(mImage.getName());
        uploaderComment.setDate(mImage.getDate());
        mComments.add(uploaderComment);

        // populate UI elements
        uploaderLabel.setText(mImage.getUploader());
        viewLabel.setText(getShortNumber((double) mImage.getViews(), 0));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getComments() {
        JsonObjectRequest commentObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.imgur_api_comment_endpoint, mImage.getId()),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("RESPONSE", response.toString());
                        try {
                            JSONArray commentArray = response.getJSONArray("data");

                            // loop through comments
                            for (int i = 0; i < commentArray.length(); i++) {
                                JSONObject commentObject = commentArray.getJSONObject(i);
                                Comment comment = new Comment();

                                // populate comment
                                comment.setDate(TimeUnit.SECONDS.toMillis(commentObject.getInt("datetime")));
                                comment.setText(commentObject.getString("comment"));
                                comment.setUsername(commentObject.getString("author"));

                                // add comment
                                mComments.add(comment);
                            }

                            // notify the adapter
                            mImageCommentAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("JSON Failure", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e("NETWORK Failure", String.format(Locale.getDefault(), "%d", e.networkResponse.statusCode));
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
        ApplicationController.getInstance().addToRequestQueue(commentObjectRequest);
    }

    /* Adapted from Elijah Saounkine's solution
     * from http://stackoverflow.com/a/4753866
     */
    private String getShortNumber(double n, int iteration) {
        // count abbreviations
        char[] c = new char[]{'k', 'M'};

        double d = ((long) n / 100) / 10.0;

        boolean isRound = (d * 10) % 10 == 0;

        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : getShortNumber(d, iteration + 1));

    }
}
