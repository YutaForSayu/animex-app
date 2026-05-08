package com.animex.app.ui.history;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.animex.app.R;
import com.animex.app.util.HistoryManager;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.*;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

    public interface OnClickListener {
        void onClick(HistoryManager.HistoryItem item);
    }

    private final Context ctx;
    private List<HistoryManager.HistoryItem> data = new ArrayList<>();
    private final OnClickListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm",
        new Locale("id", "ID"));

    public HistoryAdapter(Context ctx, OnClickListener l) {
        this.ctx = ctx; this.listener = l;
    }

    public void setData(List<HistoryManager.HistoryItem> list) {
        data = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_history, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        HistoryManager.HistoryItem item = data.get(pos);
        h.tvEpTitle.setText(item.episodeTitle != null ? item.episodeTitle : "Episode");
        h.tvAnimeTitle.setText(item.animeTitle != null ? item.animeTitle : "");
        h.tvTime.setText(sdf.format(new Date(item.watchedAt * 1000)));
        Glide.with(ctx).load(item.animeImage)
            .placeholder(R.drawable.placeholder_anime)
            .centerCrop().into(h.ivThumb);
        h.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvEpTitle, tvAnimeTitle, tvTime;
        VH(View v) {
            super(v);
            ivThumb     = v.findViewById(R.id.iv_thumb);
            tvEpTitle   = v.findViewById(R.id.tv_episode_title);
            tvAnimeTitle= v.findViewById(R.id.tv_anime_title);
            tvTime      = v.findViewById(R.id.tv_time);
        }
    }
}
