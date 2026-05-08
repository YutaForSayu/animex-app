package com.animex.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.animex.app.R;
import com.animex.app.model.Genre;
import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    public interface OnGenreClickListener { void onGenreClick(Genre genre); }
    private final Context context;
    private List<Genre> genres = new ArrayList<>();
    private OnGenreClickListener listener;
    public GenreAdapter(Context context) { this.context = context; }
    public void setOnGenreClickListener(OnGenreClickListener l) { this.listener = l; }
    public void setData(List<Genre> list) { genres = list != null ? list : new ArrayList<>(); notifyDataSetChanged(); }
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new ViewHolder(v);
    }
    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Genre g = genres.get(pos);
        h.tvTitle.setText(g.getTitle());
        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onGenreClick(g); });
    }
    @Override public int getItemCount() { return genres.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ViewHolder(View v) { super(v); tvTitle = v.findViewById(R.id.tv_genre_title); }
    }
}
