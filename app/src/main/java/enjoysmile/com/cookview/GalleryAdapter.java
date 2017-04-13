package enjoysmile.com.cookview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import enjoysmile.com.cookview.model.Image;

/**
 * Created by Daniel on 2/5/2017.
 *
 * Adapter that feeds thumbnails to the gallery
 * recycler view
 */

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ThumbnailViewHolder> {
    private List<Image> images;
    private Context mContext;

    class ThumbnailViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        ThumbnailViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            // Define click listener for the ViewHolder's View.
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("GRID ITEM CLICKED", "AT: " + getAdapterPosition());

                    // start edit intent
                    Intent intent = new Intent(v.getContext(), ImageActivity.class);
                    // put image object
                    intent.putExtra("image", images.get(getAdapterPosition()));
                    mContext.startActivity(intent);

//                    // set transition
//                    if (Build.VERSION.SDK_INT >= 21) {
//                        thumbnail.setTransitionName("thumbnailTransition" + images.get(getAdapterPosition()).getId());
//                        Pair<View, String> pair1 = Pair.create((View) thumbnail, thumbnail.getTransitionName());
//
//                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, pair1);
//                        mContext.startActivity(intent, optionsCompat.toBundle());
//                    } else {
//                        // start the activity
//                        mContext.startActivity(intent);
//                    }
                }
            });
        }
    }

    GalleryAdapter(Context context, List<Image> images) {
        mContext = context;
        this.images = images;
    }

    @Override
    public ThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View thumbnailView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_grid_thumbnail, parent, false);
        return new ThumbnailViewHolder(thumbnailView);
    }

    @Override
    public void onBindViewHolder(ThumbnailViewHolder viewHolder, int position) {
        // get image at this position
        Image image = images.get(position);

        // load the image with glide
        Glide.with(mContext)
                .load(image.getLinkSmall()) // load big square thumbnail
                //.thumbnail(0.5f) // not necessary at the moment since we're using small thumbs already
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
