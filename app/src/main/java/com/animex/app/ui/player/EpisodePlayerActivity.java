package com.animex.app.ui.player;

import android.content.*;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;
import com.animex.app.R;
import com.animex.app.adapter.CommentAdapter;
import com.animex.app.api.ApiClient;
import com.animex.app.api.FirebaseCommentHelper;
import com.animex.app.model.*;
import com.animex.app.util.*;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ValueEventListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class EpisodePlayerActivity extends AppCompatActivity {

    // Views
    private WebView webView;
    private ProgressBar progressBar;
    private TextView tvPlayerError;
    private CommentAdapter commentAdapter;
    private RecyclerView rvComments;
    private EditText etUsername, etComment;
    private ImageButton btnLike, btnDislike, btnFavorite, btnShare, btnDownload;
    private TextView tvLikeCount, tvDislikeCount;

    // Data
    private String episodeSlug, animeSlug, animeTitle, animeImage, episodeTitle;
    private EpisodeDetail currentDetail;
    private boolean webViewReady = false;

    // Managers
    private VoteManager     voteManager;
    private FavoritesManager favManager;
    private HistoryManager  histManager;

    // Firebase realtime listener — removed in onDestroy
    private ValueEventListener firebaseListener;

    private static final String PREFS = "animex_prefs";

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_player);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Intent extras
        episodeSlug  = getIntent().getStringExtra("slug");
        episodeTitle = getIntent().getStringExtra("title");
        animeSlug    = getIntent().getStringExtra("anime_slug");
        animeTitle   = getIntent().getStringExtra("anime_title");
        animeImage   = getIntent().getStringExtra("anime_image");
        setTitle(episodeTitle != null ? episodeTitle : "Nonton");

        // Managers
        voteManager  = new VoteManager(this);
        favManager   = new FavoritesManager(this);
        histManager  = new HistoryManager(this);

        // Bind views
        webView        = findViewById(R.id.web_view);
        progressBar    = findViewById(R.id.progress_bar);
        tvPlayerError  = findViewById(R.id.tv_player_error);
        rvComments     = findViewById(R.id.rv_comments);
        etUsername     = findViewById(R.id.et_username);
        etComment      = findViewById(R.id.et_comment);
        btnLike        = findViewById(R.id.btn_like);
        btnDislike     = findViewById(R.id.btn_dislike);
        btnFavorite    = findViewById(R.id.btn_favorite);
        btnShare       = findViewById(R.id.btn_share);
        btnDownload    = findViewById(R.id.btn_download);
        tvLikeCount    = findViewById(R.id.tv_like_count);
        tvDislikeCount = findViewById(R.id.tv_dislike_count);

        initWebView();
        setupComments();
        setupActions();

        // Auto-fill saved username
        etUsername.setText(
            getSharedPreferences(PREFS, MODE_PRIVATE).getString("username", ""));

        if (episodeSlug != null) {
            fetchAndPlay();
            histManager.record(
                episodeSlug,
                episodeTitle != null ? episodeTitle : "",
                animeSlug   != null ? animeSlug   : "",
                animeTitle  != null ? animeTitle  : "",
                animeImage  != null ? animeImage  : "");
        } else {
            showPlayerError("Slug episode tidak valid.");
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (webViewReady) webView.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        if (webViewReady) webView.onPause();
    }

    @Override protected void onDestroy() {
        // Remove Firebase listener to prevent memory leak
        if (firebaseListener != null && episodeSlug != null) {
            FirebaseCommentHelper.getInstance()
                .removeListener(episodeSlug, firebaseListener);
        }
        if (webViewReady) { webView.stopLoading(); webView.destroy(); }
        super.onDestroy();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        if (webViewReady && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    // ─── WebView ──────────────────────────────────────────────────────────────

    @SuppressWarnings("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setAllowContentAccess(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUserAgentString(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/124.0.0.0 Safari/537.36");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView v, int p) {
                progressBar.setVisibility(p < 100 ? View.VISIBLE : View.GONE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest req) {
                String scheme = req.getUrl().getScheme();
                if (scheme == null) return false;
                switch (scheme) {
                    case "http": case "https": case "blob": return false;
                    case "intent":
                        try {
                            Intent i = Intent.parseUri(
                                req.getUrl().toString(), Intent.URI_INTENT_SCHEME);
                            if (i.resolveActivity(getPackageManager()) != null)
                                startActivity(i);
                        } catch (Exception ignored) {}
                        return true;
                    default:
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, req.getUrl()));
                        } catch (Exception ignored) {}
                        return true;
                }
            }
            @Override public void onPageFinished(WebView v, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
        webViewReady = true;
    }

    // ─── Fetch & Play ─────────────────────────────────────────────────────────

    private void fetchAndPlay() {
        progressBar.setVisibility(View.VISIBLE);
        tvPlayerError.setVisibility(View.GONE);
        ApiClient.getInstance().getApiService().getEpisodeDetail(episodeSlug)
            .enqueue(new Callback<ApiResponse<EpisodeDetail>>() {
                @Override
                public void onResponse(Call<ApiResponse<EpisodeDetail>> c,
                                       Response<ApiResponse<EpisodeDetail>> r) {
                    if (isFinishing() || isDestroyed()) return;
                    EpisodeDetail d = (r.isSuccessful() && r.body() != null
                        && r.body().isSuccess()) ? r.body().getData() : null;
                    String url = d != null ? d.getStreamingUrl() : null;
                    if (url != null && !url.isEmpty()) {
                        currentDetail = d;
                        playUrl(url);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showPlayerError("Streaming URL tidak tersedia.");
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<EpisodeDetail>> c, Throwable t) {
                    if (isFinishing() || isDestroyed()) return;
                    progressBar.setVisibility(View.GONE);
                    showPlayerError("Gagal load: " + t.getMessage());
                }
            });
    }

    private void playUrl(String streamingUrl) {
        String safe = streamingUrl.replace("\"", "&quot;");
        String html =
            "<!DOCTYPE html><html><head>" +
            "<meta charset='utf-8'>" +
            "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
            "<style>*{margin:0;padding:0}html,body{width:100%;height:100%;" +
            "background:#000;overflow:hidden}" +
            "iframe{position:absolute;top:0;left:0;width:100%;height:100%;border:none}" +
            "</style></head><body>" +
            "<iframe src=\"" + safe + "\" allowfullscreen " +
            "allow='autoplay;encrypted-media;fullscreen;picture-in-picture' " +
            "scrolling='no'></iframe></body></html>";
        Uri uri = Uri.parse(streamingUrl);
        String baseUrl = uri.getScheme() + "://" + uri.getHost();
        webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null);
    }

    private void showPlayerError(String msg) {
        tvPlayerError.setText(msg);
        tvPlayerError.setVisibility(View.VISIBLE);
    }

    // ─── Actions (Like / Dislike / Share / Download / Favorite) ──────────────

    private void setupActions() {
        refreshVoteUI(voteManager.get(episodeSlug != null ? episodeSlug : ""));

        btnLike.setOnClickListener(v ->
            refreshVoteUI(voteManager.toggleLike(episodeSlug)));
        btnDislike.setOnClickListener(v ->
            refreshVoteUI(voteManager.toggleDislike(episodeSlug)));

        btnShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT,
                "Nonton \"" + (episodeTitle != null ? episodeTitle : "") +
                "\" di AniMex!\nhttps://apinime.tineo.my.id/api/episode/" + episodeSlug);
            startActivity(Intent.createChooser(share, "Bagikan ke..."));
        });

        btnDownload.setOnClickListener(v -> showDownloadSheet());

        refreshFavUI();
        btnFavorite.setOnClickListener(v -> {
            if (animeSlug == null || animeSlug.isEmpty()) {
                Toast.makeText(this, "Data anime tidak lengkap", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean added = favManager.toggle(buildFakeAnime());
            Toast.makeText(this,
                added ? "⭐ Ditambah ke Favorit" : "Dihapus dari Favorit",
                Toast.LENGTH_SHORT).show();
            refreshFavUI();
        });
    }

    private void refreshVoteUI(VoteManager.VoteState s) {
        tvLikeCount.setText(String.valueOf(s.likes));
        tvDislikeCount.setText(String.valueOf(s.dislikes));
        int red  = getResources().getColor(R.color.red_accent,    null);
        int grey = getResources().getColor(R.color.text_secondary, null);
        btnLike.setColorFilter(VoteManager.LIKE.equals(s.type)    ? red : grey);
        btnDislike.setColorFilter(VoteManager.DISLIKE.equals(s.type) ? red : grey);
    }

    private void refreshFavUI() {
        boolean fav = animeSlug != null && favManager.isFavorite(animeSlug);
        btnFavorite.setColorFilter(getResources().getColor(
            fav ? R.color.red_accent : R.color.text_secondary, null));
    }

    private Anime buildFakeAnime() {
        com.google.gson.JsonObject o = new com.google.gson.JsonObject();
        o.addProperty("slug",    animeSlug  != null ? animeSlug  : "");
        o.addProperty("title",   animeTitle != null ? animeTitle : "");
        o.addProperty("image",   animeImage != null ? animeImage : "");
        o.addProperty("episode", episodeTitle != null ? episodeTitle : "");
        return new com.google.gson.Gson().fromJson(o, Anime.class);
    }

    private void showDownloadSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(this, R.style.BottomSheetTheme);
        View root = getLayoutInflater().inflate(R.layout.sheet_download, null);
        sheet.setContentView(root);

        LinearLayout container = root.findViewById(R.id.ll_download_options);
        TextView tvEmpty       = root.findViewById(R.id.tv_no_downloads);

        boolean has = currentDetail != null
            && currentDetail.getDownloads() != null
            && !currentDetail.getDownloads().isEmpty();

        if (!has) {
            tvEmpty.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            for (EpisodeDetail.DownloadQuality q : currentDetail.getDownloads()) {
                View row = getLayoutInflater().inflate(
                    R.layout.item_download_quality, container, false);
                ((TextView) row.findViewById(R.id.tv_quality)).setText(q.getQuality());
                ((TextView) row.findViewById(R.id.tv_size))
                    .setText(q.getSize() != null ? q.getSize() : "");
                LinearLayout ll = row.findViewById(R.id.ll_links);
                if (q.getLinks() != null) {
                    for (EpisodeDetail.DownloadLink link : q.getLinks()) {
                        Button btn = new Button(this);
                        btn.setText(link.getProvider());
                        btn.setBackgroundTintList(
                            getResources().getColorStateList(R.color.red_accent, null));
                        btn.setTextColor(getResources().getColor(R.color.white, null));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, 0, 12, 0);
                        btn.setLayoutParams(lp);
                        btn.setOnClickListener(v -> {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(link.getDownloadUrl())));
                            } catch (Exception ignored) {}
                            sheet.dismiss();
                        });
                        ll.addView(btn);
                    }
                }
                container.addView(row);
            }
        }
        sheet.show();
    }

    // ─── Firebase Realtime Comments ───────────────────────────────────────────

    private void setupComments() {
        commentAdapter = new CommentAdapter(this);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);
        findViewById(R.id.btn_send_comment).setOnClickListener(v -> sendComment());

        // Attach Firebase realtime listener
        if (episodeSlug != null) {
            firebaseListener = FirebaseCommentHelper.getInstance()
                .listenComments(episodeSlug, comments -> {
                    if (isFinishing() || isDestroyed()) return;
                    commentAdapter.setData(comments);
                    if (!comments.isEmpty()) {
                        rvComments.post(() ->
                            rvComments.smoothScrollToPosition(comments.size() - 1));
                    }
                });
        }
    }

    private void sendComment() {
        String name = etUsername.getText().toString().trim();
        String msg  = etComment.getText().toString().trim();
        if (name.isEmpty()) { etUsername.setError("Isi nama dulu"); return; }
        if (msg.isEmpty())  { etComment.setError("Tulis komentar dulu"); return; }

        getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit().putString("username", name).apply();

        etComment.setEnabled(false);
        FirebaseCommentHelper.getInstance().postComment(
            episodeSlug, name, msg,
            new FirebaseCommentHelper.PostCallback() {
                @Override public void onSuccess() {
                    etComment.setText("");
                    etComment.setEnabled(true);
                    // No need to manually reload — Firebase listener auto-triggers
                }
                @Override public void onError(String e) {
                    etComment.setEnabled(true);
                    Toast.makeText(EpisodePlayerActivity.this,
                        "Gagal kirim: " + e, Toast.LENGTH_SHORT).show();
                }
            });
    }
}
