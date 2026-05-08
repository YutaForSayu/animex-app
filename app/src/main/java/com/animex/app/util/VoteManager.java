package com.animex.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VoteManager {
    public static final String NONE    = "none";
    public static final String LIKE    = "like";
    public static final String DISLIKE = "dislike";

    public static class VoteState {
        public String type;    // "none" | "like" | "dislike"
        public int    likes;
        public int    dislikes;
    }

    private final DatabaseHelper db;

    public VoteManager(Context ctx) { db = DatabaseHelper.getInstance(ctx); }

    public VoteState get(String episodeSlug) {
        SQLiteDatabase r = db.getReadableDatabase();
        Cursor c = r.query(DatabaseHelper.TABLE_VOTE, null,
            DatabaseHelper.COL_VOTE_SLUG + "=?",
            new String[]{episodeSlug}, null, null, null);

        VoteState s = new VoteState();
        s.type = NONE; s.likes = 0; s.dislikes = 0;

        if (c.moveToFirst()) {
            s.type     = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_VOTE_TYPE));
            s.likes    = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_VOTE_LIKES));
            s.dislikes = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_VOTE_DISLIKES));
        }
        c.close();
        return s;
    }

    public VoteState toggleLike(String episodeSlug) {
        VoteState s = get(episodeSlug);
        if (LIKE.equals(s.type)) {
            s.likes = Math.max(0, s.likes - 1);
            s.type  = NONE;
        } else {
            if (DISLIKE.equals(s.type)) s.dislikes = Math.max(0, s.dislikes - 1);
            s.likes++;
            s.type = LIKE;
        }
        save(episodeSlug, s);
        return s;
    }

    public VoteState toggleDislike(String episodeSlug) {
        VoteState s = get(episodeSlug);
        if (DISLIKE.equals(s.type)) {
            s.dislikes = Math.max(0, s.dislikes - 1);
            s.type     = NONE;
        } else {
            if (LIKE.equals(s.type)) s.likes = Math.max(0, s.likes - 1);
            s.dislikes++;
            s.type = DISLIKE;
        }
        save(episodeSlug, s);
        return s;
    }

    private void save(String slug, VoteState s) {
        SQLiteDatabase w = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_VOTE_SLUG,     slug);
        cv.put(DatabaseHelper.COL_VOTE_TYPE,     s.type);
        cv.put(DatabaseHelper.COL_VOTE_LIKES,    s.likes);
        cv.put(DatabaseHelper.COL_VOTE_DISLIKES, s.dislikes);
        w.insertWithOnConflict(DatabaseHelper.TABLE_VOTE, null, cv,
            SQLiteDatabase.CONFLICT_REPLACE);
    }
}
