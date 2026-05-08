package com.animex.app.ui.favorites;

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
import com.animex.app.model.Anime;
import com.animex.app.ui.detail.AnimeDetailActivity;
import com.animex.app.util.FavoritesManager;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private AnimeAdapter adapter;
    private TextView tvEmpty;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.recycler_view);
        tvEmpty = view.findViewById(R.id.tv_empty);

        adapter = new AnimeAdapter(requireContext());
        adapter.setOnItemClickListener(anime -> {
            Intent i = new Intent(getActivity(), AnimeDetailActivity.class);
            i.putExtra("slug",  anime.getSlug());
            i.putExtra("title", anime.getTitle());
            i.putExtra("image", anime.getImage());
            startActivity(i);
        });
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setAdapter(adapter);
    }

    @Override public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        List<Anime> list = new FavoritesManager(requireContext()).getAll();
        adapter.setData(list);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
