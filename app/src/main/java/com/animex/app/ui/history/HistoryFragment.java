package com.animex.app.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.animex.app.R;
import com.animex.app.ui.player.EpisodePlayerActivity;
import com.animex.app.util.HistoryManager;
import java.text.SimpleDateFormat;
import java.util.*;

public class HistoryFragment extends Fragment {

    private HistoryAdapter adapter;
    private TextView tvEmpty;
    private HistoryManager histManager;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        histManager = new HistoryManager(requireContext());
        RecyclerView rv = view.findViewById(R.id.recycler_view);
        tvEmpty = view.findViewById(R.id.tv_empty);
        Button btnClear = view.findViewById(R.id.btn_clear);

        adapter = new HistoryAdapter(requireContext(), item -> {
            Intent i = new Intent(getActivity(), EpisodePlayerActivity.class);
            i.putExtra("slug",        item.episodeSlug);
            i.putExtra("title",       item.episodeTitle);
            i.putExtra("anime_slug",  item.animeSlug);
            i.putExtra("anime_title", item.animeTitle);
            i.putExtra("anime_image", item.animeImage);
            startActivity(i);
        });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        btnClear.setOnClickListener(v ->
            new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Riwayat")
                .setMessage("Hapus semua riwayat tontonan?")
                .setPositiveButton("Hapus", (d, w) -> {
                    histManager.clear();
                    loadHistory();
                })
                .setNegativeButton("Batal", null)
                .show());
    }

    @Override public void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        List<HistoryManager.HistoryItem> list = histManager.getAll();
        adapter.setData(list);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
