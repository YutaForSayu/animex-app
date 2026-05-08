package com.animex.app.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class Genre {
    @SerializedName("title") private String title;
    @SerializedName("slug") private String slug;
    @SerializedName("url") private String url;
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getUrl() { return url; }
    public static class GenreListData {
        @SerializedName("genres") private List<Genre> genres;
        public List<Genre> getGenres() { return genres; }
    }
}
