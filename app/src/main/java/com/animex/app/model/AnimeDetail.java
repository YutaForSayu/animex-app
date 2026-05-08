package com.animex.app.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class AnimeDetail {
    @SerializedName("title") private String title;
    @SerializedName("japaneseTitle") private String japaneseTitle;
    @SerializedName("rating") private String rating;
    @SerializedName("producer") private String producer;
    @SerializedName("type") private String type;
    @SerializedName("status") private String status;
    @SerializedName("episodeTotal") private String episodeTotal;
    @SerializedName("duration") private String duration;
    @SerializedName("releaseDate") private String releaseDate;
    @SerializedName("studio") private String studio;
    @SerializedName("genre") private String genre;
    @SerializedName("synopsis") private String synopsis;
    @SerializedName("image") private String image;
    @SerializedName("episodes") private List<EpisodeItem> episodes;
    public String getTitle() { return title; }
    public String getJapaneseTitle() { return japaneseTitle; }
    public String getRating() { return rating; }
    public String getProducer() { return producer; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getEpisodeTotal() { return episodeTotal; }
    public String getDuration() { return duration; }
    public String getReleaseDate() { return releaseDate; }
    public String getStudio() { return studio; }
    public String getGenre() { return genre; }
    public String getSynopsis() { return synopsis; }
    public String getImage() { return image; }
    public List<EpisodeItem> getEpisodes() { return episodes; }
}
