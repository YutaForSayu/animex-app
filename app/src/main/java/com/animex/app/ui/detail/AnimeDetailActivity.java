package com.animex.app.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.animex.app.R;
import com.animex.app.adapter.EpisodeAdapter;
import com.animex.app.api.ApiClient;
import com.animex.app.model.AnimeDetail;
import com.animex.app.model.ApiResponse;
import com.animex.app.ui.player.EpisodePlayerActivity;
import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeDetailActivity extends AppCompatActivity {

    private EpisodeAdapter episodeAdapter;
    private ProgressBar progressBar;
    private LinearLayout contentLayout;
    private String slug;

    // Class-level fields so the episode click lambda can access them
    private String currentAnimeTitle = "";
    private String currentAnimeImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        slug                = getIntent().getStringExtra("slug");
        String title        = getIntent().getStringExtra("title");
        String image        = getIntent().getStringExtra("image");
        currentAnimeTitle   = title != null ? title : "";
        currentAnimeImage   = image != null ? image : "";

        setTitle(!currentAnimeTitle.isEmpty() ? currentAnimeTitle : "Detail Anime");

        progressBar   = findViewById(R.id.progress_bar);
        contentLayout = findViewById(R.id.content_layout);
        RecyclerView rvEpisodes = findViewById(R.id.rv_episodes);

        ImageView ivBanner = findViewById(R.id.iv_banner);
        if (image != null) Glide.with(this).load(image).centerCrop().into(ivBanner);

        episodeAdapter = new EpisodeAdapter(this);
        episodeAdapter.setOnEpisodeClickListener(episode -> {
            Intent intent = new Intent(this, EpisodePlayerActivity.class);
            intent.putExtra("slug",        episode.getSlug());
            intent.putExtra("title",       episode.getEpisodeTitle());
            intent.putExtra("anime_slug",  slug);
            intent.putExtra("anime_title", currentAnimeTitle);
            intent.putExtra("anime_image", currentAnimeImage);
            startActivity(intent);
        });

        rvEpisodes.setLayoutManager(new LinearLayoutManager(this));
        rvEpisodes.setAdapter(episodeAdapter);

        if (slug != null) loadDetail(slug);
    }

    private void loadDetail(String slug) {
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        ApiClient.getInstance().getApiService().getAnimeDetail(slug)
            .enqueue(new Callback<ApiResponse<AnimeDetail>>() {
                @Override
                public void onResponse(Call<ApiResponse<AnimeDetail>> call,
                                       Response<ApiResponse<AnimeDetail>> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        AnimeDetail detail = response.body().getData();
                        if (detail != null) {
                            // Update class-level fields so episode click gets latest data
                            if (detail.getTitle() != null) currentAnimeTitle = detail.getTitle();
                            if (detail.getImage() != null) currentAnimeImage = detail.getImage();
                            bindDetail(detail);
                            contentLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(AnimeDetailActivity.this,
                            "Gagal memuat detail", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<AnimeDetail>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AnimeDetailActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void bindDetail(AnimeDetail detail) {
        if (detail == null) return;
        setTitle(detail.getTitle());
        setText(R.id.tv_title,        detail.getTitle());
        setText(R.id.tv_japanese,     detail.getJapaneseTitle());
        setText(R.id.tv_rating,       detail.getRating() != null ? "★ " + detail.getRating() : "N/A");
        setText(R.id.tv_status,       detail.getStatus());
        setText(R.id.tv_type,         detail.getType());
        setText(R.id.tv_studio,       detail.getStudio());
        setText(R.id.tv_genre,        detail.getGenre());
        setText(R.id.tv_episode_total,detail.getEpisodeTotal() != null
                                        ? detail.getEpisodeTotal() + " Episode" : "-");
        setText(R.id.tv_duration,     detail.getDuration());
        setText(R.id.tv_release,      detail.getReleaseDate());
        setText(R.id.tv_synopsis,     detail.getSynopsis());

        if (detail.getImage() != null) {
            Glide.with(this).load(detail.getImage()).centerCrop()
                .into((ImageView) findViewById(R.id.iv_banner));
        }
        if (detail.getEpisodes() != null) {
            episodeAdapter.setData(detail.getEpisodes());
        }
    }

    private void setText(int id, String text) {
        TextView tv = findViewById(id);
        if (tv != null && text != null) tv.setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
