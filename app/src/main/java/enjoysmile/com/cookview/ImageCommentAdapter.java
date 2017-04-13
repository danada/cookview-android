package enjoysmile.com.cookview;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import enjoysmile.com.cookview.model.Comment;

/**
 * Created by Daniel on 2/8/2017.
 * Adapter for the comments in the image view
 */

class ImageCommentAdapter extends RecyclerView.Adapter<ImageCommentAdapter.CommentViewHolder> {
    private ArrayList<Comment> mComments;

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView commentText;
        TextView dateText;

        CommentViewHolder(View v) {
            super(v);

            commentText = (TextView) v.findViewById(R.id.comment_text);
            dateText = (TextView) v.findViewById(R.id.comment_date);
        }
    }

    ImageCommentAdapter(ArrayList<Comment> comments) {
        mComments = comments;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        // build a formatted string
        final SpannableStringBuilder commentString = new SpannableStringBuilder(
                mComments.get(position).getUsername() +
                        " " +
                        mComments.get(position).getText()
        );
        // set the username to bold
        commentString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0,
                mComments.get(position).getUsername().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.commentText.setText(commentString);
        holder.dateText.setText(DateUtils.getRelativeTimeSpanString(mComments.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }
}
