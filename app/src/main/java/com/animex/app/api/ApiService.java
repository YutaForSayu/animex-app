package com.animex.app.api;

import com.animex.app.model.Anime;
import com.animex.app.model.AnimeDetail;
import com.animex.app.model.ApiResponse;
import com.animex.app.model.EpisodeDetail;
import com.animex.app.model.Genre;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/home")
    Call<ApiResponse<List<Anime>>> getHome();

    @GET("api/ongoing")
    Call<ApiResponse<List<Anime>>> getOngoing();

    @GET("api/ongoing/{page}")
    Call<ApiResponse<List<Anime>>> getOngoingPage(@Path("page") int page);

    @GET("api/completed")
    Call<ApiResponse<List<Anime>>> getCompleted();

    @GET("api/completed/{page}")
    Call<ApiResponse<List<Anime>>> getCompletedPage(@Path("page") int page);

    @GET("api/search/{query}")
    Call<ApiResponse<List<Anime>>> searchAnime(@Path("query") String query);

    @GET("api/anime/{slug}")
    Call<ApiResponse<AnimeDetail>> getAnimeDetail(@Path("slug") String slug);

    @GET("api/episode/{slug}")
    Call<ApiResponse<EpisodeDetail>> getEpisodeDetail(@Path("slug") String slug);

    @GET("api/genre-list")
    Call<ApiResponse<Genre.GenreListData>> getGenreList();

    @GET("api/genre/{slug}")
    Call<ApiResponse<List<Anime>>> getGenreAnime(@Path("slug") String slug);

    @GET("api/genre/{slug}/{page}")
    Call<ApiResponse<List<Anime>>> getGenreAnimePage(@Path("slug") String slug, @Path("page") int page);
}
