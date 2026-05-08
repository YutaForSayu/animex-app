package com.animex.app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class SearchFragment extends Fragment {
    private AnimeAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private EditText etSearch;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etSearch = view.findViewById(R.id.et_search);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        ImageButton btnSearch = view.findViewById(R.id.btn_search);

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

        btnSearch.setOnClickListener(v -> performSearch());
        etSearch.setOnEditorActionListener((tv, action, event) -> { performSearch(); return true; });
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (query.isEmpty()) return;
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        ApiClient.getInstance().getApiService().searchAnime(query)
            .enqueue(new Callback<ApiResponse<List<Anime>>>() {
                @Override public void onResponse(Call<ApiResponse<List<Anime>>> call, Response<ApiResponse<List<Anime>>> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Anime> data = response.body().getData();
                        adapter.setData(data);
                        tvEmpty.setVisibility(data == null || data.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                }
                @Override public void onFailure(Call<ApiResponse<List<Anime>>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Error: " + t.getMessage());
                }
            });
    }
}
