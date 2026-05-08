package com.animex.app.adapter;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.animex.app.R;
import com.animex.app.model.Comment;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.VH> {
    private final Context ctx;
    private List<Comment> data = new ArrayList<>();
    private final SimpleDateFormat sdf =
        new SimpleDateFormat("HH:mm", Locale.getDefault());

    public CommentAdapter(Context ctx) { this.ctx = ctx; }

    public void setData(List<Comment> list) {
        data = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(ctx).inflate(R.layout.item_comment, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Comment c = data.get(pos);
        String name = c.getUsername() != null ? c.getUsername() : "Anonim";
        h.tvUsername.setText(name);
        h.tvMessage.setText(c.getMessage() != null ? c.getMessage() : "");

        // Avatar initial + color
        if (!name.isEmpty()) {
            h.tvAvatar.setText(String.valueOf(name.charAt(0)).toUpperCase());
            try {
                String[] colors = {"#E53935","#8E24AA","#1E88E5",
                                   "#00897B","#F4511E","#6D4C41","#546E7A"};
                String color = colors[Math.abs(name.hashCode()) % colors.length];
                android.graphics.drawable.GradientDrawable bg =
                    new android.graphics.drawable.GradientDrawable();
                bg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
                bg.setColor(android.graphics.Color.parseColor(color));
                h.tvAvatar.setBackground(bg);
            } catch (Exception ignored) {}
        }

        if (c.getTimestamp() > 0) {
            h.tvTime.setText(sdf.format(new Date(c.getTimestamp())));
        } else {
            h.tvTime.setText("");
        }
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvUsername, tvMessage, tvTime;
        VH(View v) {
            super(v);
            tvAvatar   = v.findViewById(R.id.tv_avatar);
            tvUsername = v.findViewById(R.id.tv_username);
            tvMessage  = v.findViewById(R.id.tv_message);
            tvTime     = v.findViewById(R.id.tv_time);
        }
    }
}
