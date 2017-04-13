package enjoysmile.com.cookview.model;

/**
 * Created by Daniel on 2/8/2017.
 *
 * Model to hold image comments
 */

public class Comment {
    private String username;
    private String text;
    private long date;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
