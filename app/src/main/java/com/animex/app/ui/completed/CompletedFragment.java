package com.animex.app.ui.completed;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.animex.app.R;
import com.animex.app.adapter.AnimeAdapter;
import com.animex.app.api.ApiClient;
import com.animex.app.model.Anime;
import com.animex.app.model.ApiResponse;
import com.animex.app.ui.detail.AnimeDetailActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedFragment extends Fragment {
    private AnimeAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvError;
    private Button btnLoadMore;
    private int currentPage = 1;
    private boolean loading = false;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anime_list, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        tvError = view.findViewById(R.id.tv_error);
        btnLoadMore = view.findViewById(R.id.btn_load_more);

        adapter = new AnimeAdapter(requireContext());
        adapter.setOnItemClickListener(anime -> {
            Intent intent = new Intent(getActivity(), AnimeDetailActivity.class);
            intent.putExtra("slug", anime.getSlug());
            intent.putExtra("title", anime.getTitle());
            intent.putExtra("image", anime.getImage());
            startActivity(intent);
        });

        RecyclerView rv = view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setAdapter(adapter);
        swipeRefresh.setColorSchemeResources(R.color.red_accent);
        swipeRefresh.setOnRefreshListener(() -> { currentPage = 1; adapter.setData(null); loadPage(); });
        btnLoadMore.setOnClickListener(v -> { currentPage++; loadPage(); });
        loadPage();
    }

    private void loadPage() {
        if (loading) return;
        loading = true;
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getInstance().getApiService().getCompletedPage(currentPage)
            .enqueue(new Callback<ApiResponse<List<Anime>>>() {
                @Override public void onResponse(Call<ApiResponse<List<Anime>>> call, Response<ApiResponse<List<Anime>>> response) {
                    loading = false;
                    swipeRefresh.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Anime> data = response.body().getData();
                        if (currentPage == 1) adapter.setData(data);
                        else adapter.addData(data);
                        btnLoadMore.setVisibility(data != null && !data.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                }
                @Override public void onFailure(Call<ApiResponse<List<Anime>>> call, Throwable t) {
                    loading = false; swipeRefresh.setRefreshing(false); progressBar.setVisibility(View.GONE);
                }
            });
    }
}
