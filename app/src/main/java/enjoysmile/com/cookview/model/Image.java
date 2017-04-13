package enjoysmile.com.cookview.model;

import java.io.Serializable;

/**
 * Created by Daniel on 2/5/2017.
 *
 * Image model to represent images used in
 * gallery and image activities
 */

public class Image implements Serializable {
    private String id;
    private String name;
    private String uploader;
    private String link;
    private String linkSmall;
    private long date;
    private int views;

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }


    public String getLinkSmall() {
        return linkSmall;
    }

    public void setLinkSmall(String linkSmall) {
        this.linkSmall = linkSmall;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long datetime) {
        this.date = datetime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
