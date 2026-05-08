package com.animex.app.api;

import androidx.annotation.NonNull;
import com.animex.app.model.Comment;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Realtime comments via Firebase RTDB.
 *
 * Structure:
 *   /comments/{episodeSlug}/{pushId}/
 *       username  : String
 *       message   : String
 *       timestamp : long
 */
public class FirebaseCommentHelper {

    private static final String ROOT = "comments";
    private static FirebaseCommentHelper instance;

    private final FirebaseDatabase db;

    public interface CommentsCallback {
        void onChanged(List<Comment> comments);
    }

    public interface PostCallback {
        void onSuccess();
        void onError(String msg);
    }

    private FirebaseCommentHelper() {
        db = FirebaseDatabase.getInstance();
        // Keep synced so comments load even with brief offline moments
        db.getReference(ROOT).keepSynced(true);
    }

    public static synchronized FirebaseCommentHelper getInstance() {
        if (instance == null) instance = new FirebaseCommentHelper();
        return instance;
    }

    /**
     * Listen to comments for a specific episode.
     * Returns a ValueEventListener so the caller can remove it on destroy.
     */
    public ValueEventListener listenComments(String episodeSlug, CommentsCallback cb) {
        DatabaseReference ref = db.getReference(ROOT)
                .child(sanitizeSlug(episodeSlug));

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Comment> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Comment c = child.getValue(Comment.class);
                    if (c != null) {
                        c.setId(child.getKey());
                        list.add(c);
                    }
                }
                cb.onChanged(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cb.onChanged(new ArrayList<>());
            }
        };

        ref.orderByChild("timestamp").addValueEventListener(listener);
        return listener;
    }

    /** Stop listening — call in onDestroy */
    public void removeListener(String episodeSlug, ValueEventListener listener) {
        db.getReference(ROOT)
          .child(sanitizeSlug(episodeSlug))
          .removeEventListener(listener);
    }

    /** Post a new comment */
    public void postComment(String episodeSlug, String username, String message,
                            PostCallback cb) {
        DatabaseReference ref = db.getReference(ROOT)
                .child(sanitizeSlug(episodeSlug))
                .push();

        Comment c = new Comment(username, message);
        ref.setValue(c)
           .addOnSuccessListener(v -> cb.onSuccess())
           .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    /** Firebase keys cannot contain . # $ [ ] */
    private String sanitizeSlug(String slug) {
        return slug.replaceAll("[.#$\\[\\]]", "_");
    }
}
