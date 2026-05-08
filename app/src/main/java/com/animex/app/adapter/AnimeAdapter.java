package com.animex.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.animex.app.R;
import com.animex.app.model.Anime;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Anime anime);
    }

    private final Context context;
    private List<Anime> animeList = new ArrayList<>();
    private OnItemClickListener listener;

    public AnimeAdapter(Context context) { this.context = context; }

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    public void setData(List<Anime> list) {
        animeList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addData(List<Anime> list) {
        if (list == null) return;
        int start = animeList.size();
        animeList.addAll(list);
        notifyItemRangeInserted(start, list.size());
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_anime_card, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Anime anime = animeList.get(pos);
        h.tvTitle.setText(anime.getTitle());
        h.tvEpisode.setText(anime.getEpisode() != null ? anime.getEpisode() : "");
        String statusText = anime.getStatus() != null ? anime.getStatus() : "";
        h.tvStatus.setText(statusText);
        h.tvStatus.setTextColor(context.getResources().getColor(
            "Ongoing".equals(statusText) ? R.color.red_accent : R.color.text_secondary, null));
        if (anime.getRating() != null && !anime.getRating().isEmpty()) {
            h.tvRating.setVisibility(View.VISIBLE);
            h.tvRating.setText("★ " + anime.getRating());
        } else {
            h.tvRating.setVisibility(View.GONE);
        }
        Glide.with(context).load(anime.getImage())
                .placeholder(R.drawable.placeholder_anime)
                .centerCrop().into(h.ivThumb);
        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(anime); });
    }

    @Override public int getItemCount() { return animeList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle, tvEpisode, tvStatus, tvRating;
        ViewHolder(View v) {
            super(v);
            ivThumb = v.findViewById(R.id.iv_thumb);
            tvTitle = v.findViewById(R.id.tv_title);
            tvEpisode = v.findViewById(R.id.tv_episode);
            tvStatus = v.findViewById(R.id.tv_status);
            tvRating = v.findViewById(R.id.tv_rating);
        }
    }
}
