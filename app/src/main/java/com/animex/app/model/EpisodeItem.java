package com.animex.app.model;
import com.google.gson.annotations.SerializedName;
public class EpisodeItem {
    @SerializedName("episodeTitle") private String episodeTitle;
    @SerializedName("slug") private String slug;
    @SerializedName("url") private String url;
    @SerializedName("label") private String label;
    public String getEpisodeTitle() { return episodeTitle != null ? episodeTitle : label; }
    public String getSlug() { return slug; }
    public String getUrl() { return url; }
}
