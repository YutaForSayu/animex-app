package com.animex.app.ui.home;

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

public class HomeFragment extends Fragment {
    private AnimeAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvError;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anime_list, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        tvError = view.findViewById(R.id.tv_error);
        View loadMore = view.findViewById(R.id.btn_load_more);
        if (loadMore != null) loadMore.setVisibility(View.GONE);

        adapter = new AnimeAdapter(requireContext());
        adapter.setOnItemClickListener(anime -> {
            Intent intent = new Intent(getActivity(), AnimeDetailActivity.class);
            intent.putExtra("slug", anime.getSlug());
            intent.putExtra("title", anime.getTitle());
            intent.putExtra("image", anime.getImage());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);
        swipeRefresh.setColorSchemeResources(R.color.red_accent);
        swipeRefresh.setOnRefreshListener(this::loadData);
        loadData();
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        ApiClient.getInstance().getApiService().getHome().enqueue(new Callback<ApiResponse<List<Anime>>>() {
            @Override public void onResponse(Call<ApiResponse<List<Anime>>> call, Response<ApiResponse<List<Anime>>> response) {
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setData(response.body().getData());
                } else {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Gagal memuat data");
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<Anime>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Error: " + t.getMessage());
            }
        });
    }
}
