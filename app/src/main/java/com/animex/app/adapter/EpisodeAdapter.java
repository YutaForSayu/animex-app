package com.animex.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.animex.app.R;
import com.animex.app.model.EpisodeItem;
import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {
    public interface OnEpisodeClickListener {
        void onEpisodeClick(EpisodeItem episode);
    }

    private final Context context;
    private List<EpisodeItem> episodes = new ArrayList<>();
    private OnEpisodeClickListener listener;

    public EpisodeAdapter(Context context) { this.context = context; }
    public void setOnEpisodeClickListener(OnEpisodeClickListener l) { this.listener = l; }

    public void setData(List<EpisodeItem> list) {
        episodes = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        EpisodeItem ep = episodes.get(pos);
        String title = ep.getEpisodeTitle();
        h.tvTitle.setText(title != null ? title : "Episode " + (pos + 1));
        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onEpisodeClick(ep); });
    }

    @Override public int getItemCount() { return episodes.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ViewHolder(View v) { super(v); tvTitle = v.findViewById(R.id.tv_episode_title); }
    }
}
