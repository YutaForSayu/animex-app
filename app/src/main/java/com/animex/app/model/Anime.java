package com.animex.app.model;
import com.google.gson.annotations.SerializedName;
public class Anime {
    @SerializedName("title") private String title;
    @SerializedName("episode") private String episode;
    @SerializedName("status") private String status;
    @SerializedName("rating") private String rating;
    @SerializedName("date") private String date;
    @SerializedName("slug") private String slug;
    @SerializedName("image") private String image;
    public String getTitle() { return title; }
    public String getEpisode() { return episode; }
    public String getStatus() { return status; }
    public String getRating() { return rating; }
    public String getDate() { return date; }
    public String getSlug() { return slug; }
    public String getImage() { return image; }
}
