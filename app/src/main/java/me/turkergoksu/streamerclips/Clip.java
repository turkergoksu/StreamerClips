package me.turkergoksu.streamerclips;

import android.os.Parcel;
import android.os.Parcelable;

public class Clip implements Parcelable {

    private String id;
    private String url;
    private String embedURL;
    private String broadcasterID;
    private String broadcasterName;
    private String creatorID;
    private String creatorName;
    private String title;
    private int viewCount;
    private String createdAt;
    private String thumbnailImageURL;

    public Clip(String id, String url, String embedURL, String broadcasterID, String broadcasterName,
                String creatorID, String creatorName, String title, int viewCount,
                String createdAt, String thumbnailImageURL) {
        this.id = id;
        this.url = url;
        this.embedURL = embedURL;
        this.broadcasterID = broadcasterID;
        this.broadcasterName = broadcasterName;
        this.creatorID = creatorID;
        this.creatorName = creatorName;
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.thumbnailImageURL = thumbnailImageURL;
    }

    public Clip(Parcel in) {
        id = in.readString();
        url = in.readString();
        embedURL = in.readString();
        broadcasterID = in.readString();
        broadcasterName = in.readString();
        creatorID = in.readString();
        creatorName = in.readString();
        title = in.readString();
        viewCount = in.readInt();
        createdAt = in.readString();
        thumbnailImageURL = in.readString();
    }

    public static final Creator<Clip> CREATOR = new Creator<Clip>() {
        @Override
        public Clip createFromParcel(Parcel in) {
            return new Clip(in);
        }

        @Override
        public Clip[] newArray(int size) {
            return new Clip[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmbedURL() {
        return embedURL;
    }

    public void setEmbedURL(String embedURL) {
        this.embedURL = embedURL;
    }

    public String getBroadcasterID() {
        return broadcasterID;
    }

    public void setBroadcasterID(String broadcasterID) {
        this.broadcasterID = broadcasterID;
    }

    public String getBroadcasterName() {
        return broadcasterName;
    }

    public void setBroadcasterName(String broadcasterName) {
        this.broadcasterName = broadcasterName;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getThumbnailImageURL() {
        return thumbnailImageURL;
    }

    public void setThumbnailImageURL(String thumbnailImageURL) {
        this.thumbnailImageURL = thumbnailImageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(url);
        parcel.writeString(embedURL);
        parcel.writeString(broadcasterID);
        parcel.writeString(broadcasterName);
        parcel.writeString(creatorID);
        parcel.writeString(creatorName);
        parcel.writeString(title);
        parcel.writeInt(viewCount);
        parcel.writeString(createdAt);
        parcel.writeString(thumbnailImageURL);
    }

}
