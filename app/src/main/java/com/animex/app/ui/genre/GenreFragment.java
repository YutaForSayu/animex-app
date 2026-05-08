package com.animex.app.ui.genre;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.animex.app.R;
import com.animex.app.adapter.AnimeAdapter;
import com.animex.app.adapter.GenreAdapter;
import com.animex.app.api.ApiClient;
import com.animex.app.model.Anime;
import com.animex.app.model.ApiResponse;
import com.animex.app.model.Genre;
import com.animex.app.ui.detail.AnimeDetailActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreFragment extends Fragment {
    private GenreAdapter genreAdapter;
    private AnimeAdapter animeAdapter;
    private ProgressBar progressBar;
    private RecyclerView rvGenres, rvAnimes;
    private TextView tvTitle;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_genre, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvGenres = view.findViewById(R.id.rv_genres);
        rvAnimes = view.findViewById(R.id.rv_animes);
        progressBar = view.findViewById(R.id.progress_bar);
        tvTitle = view.findViewById(R.id.tv_genre_title);

        genreAdapter = new GenreAdapter(requireContext());
        genreAdapter.setOnGenreClickListener(genre -> {
            tvTitle.setText(genre.getTitle());
            loadGenreAnime(genre.getSlug());
        });
        rvGenres.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvGenres.setAdapter(genreAdapter);

        animeAdapter = new AnimeAdapter(requireContext());
        animeAdapter.setOnItemClickListener(anime -> {
            Intent intent = new Intent(getActivity(), AnimeDetailActivity.class);
            intent.putExtra("slug", anime.getSlug());
            intent.putExtra("title", anime.getTitle());
            intent.putExtra("image", anime.getImage());
            startActivity(intent);
        });
        rvAnimes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvAnimes.setAdapter(animeAdapter);

        loadGenres();
    }

    private void loadGenres() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getInstance().getApiService().getGenreList()
            .enqueue(new Callback<ApiResponse<Genre.GenreListData>>() {
                @Override public void onResponse(Call<ApiResponse<Genre.GenreListData>> call, Response<ApiResponse<Genre.GenreListData>> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Genre.GenreListData data = response.body().getData();
                        if (data != null) {
                            genreAdapter.setData(data.getGenres());
                            if (data.getGenres() != null && !data.getGenres().isEmpty()) {
                                Genre first = data.getGenres().get(0);
                                tvTitle.setText(first.getTitle());
                                loadGenreAnime(first.getSlug());
                            }
                        }
                    }
                }
                @Override public void onFailure(Call<ApiResponse<Genre.GenreListData>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
    }

    private void loadGenreAnime(String slug) {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getInstance().getApiService().getGenreAnime(slug)
            .enqueue(new Callback<ApiResponse<List<Anime>>>() {
                @Override public void onResponse(Call<ApiResponse<List<Anime>>> call, Response<ApiResponse<List<Anime>>> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        animeAdapter.setData(response.body().getData());
                    }
                }
                @Override public void onFailure(Call<ApiResponse<List<Anime>>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
    }
}
