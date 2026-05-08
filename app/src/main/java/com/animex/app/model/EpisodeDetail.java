package com.animex.app.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EpisodeDetail {
    @SerializedName("title") private String title;
    @SerializedName("streaming_url") private String streamingUrl;
    @SerializedName("downloads") private List<DownloadQuality> downloads;

    public String getTitle() { return title; }
    public String getStreamingUrl() { return streamingUrl; }
    public List<DownloadQuality> getDownloads() { return downloads; }

    public static class DownloadQuality {
        @SerializedName("quality") private String quality;
        @SerializedName("size") private String size;
        @SerializedName("downloads") private List<DownloadLink> links;

        public String getQuality() { return quality; }
        public String getSize() { return size; }
        public List<DownloadLink> getLinks() { return links; }
    }

    public static class DownloadLink {
        @SerializedName("provider") private String provider;
        @SerializedName("downloadUrl") private String downloadUrl;
        public String getProvider() { return provider; }
        public String getDownloadUrl() { return downloadUrl; }
    }
}
