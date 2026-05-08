package com.animex.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.animex.app.model.Anime;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private final DatabaseHelper db;

    public FavoritesManager(Context ctx) { db = DatabaseHelper.getInstance(ctx); }

    public boolean isFavorite(String slug) {
        SQLiteDatabase r = db.getReadableDatabase();
        Cursor c = r.query(DatabaseHelper.TABLE_FAV,
            new String[]{"id"},
            DatabaseHelper.COL_FAV_SLUG + "=?",
            new String[]{slug}, null, null, null);
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    /** Returns true if added, false if removed */
    public boolean toggle(Anime anime) {
        if (isFavorite(anime.getSlug())) {
            db.getWritableDatabase().delete(DatabaseHelper.TABLE_FAV,
                DatabaseHelper.COL_FAV_SLUG + "=?", new String[]{anime.getSlug()});
            return false;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COL_FAV_SLUG,   anime.getSlug());
            cv.put(DatabaseHelper.COL_FAV_TITLE,  anime.getTitle());
            cv.put(DatabaseHelper.COL_FAV_IMAGE,  anime.getImage());
            cv.put(DatabaseHelper.COL_FAV_EP,     anime.getEpisode());
            cv.put(DatabaseHelper.COL_FAV_STATUS, anime.getStatus());
            cv.put(DatabaseHelper.COL_FAV_RATING, anime.getRating());
            db.getWritableDatabase().insertWithOnConflict(DatabaseHelper.TABLE_FAV, null, cv, android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE);
            return true;
        }
    }

    public List<Anime> getAll() {
        List<Anime> list = new ArrayList<>();
        SQLiteDatabase r = db.getReadableDatabase();
        Cursor c = r.query(DatabaseHelper.TABLE_FAV, null, null, null, null, null,
            DatabaseHelper.COL_FAV_ADDED + " DESC");
        while (c.moveToNext()) {
            list.add(cursorToAnime(c));
        }
        c.close();
        return list;
    }

    private Anime cursorToAnime(Cursor c) {
        // Use Gson to build Anime from cursor data
        com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
        obj.addProperty("slug",    getString(c, DatabaseHelper.COL_FAV_SLUG));
        obj.addProperty("title",   getString(c, DatabaseHelper.COL_FAV_TITLE));
        obj.addProperty("image",   getString(c, DatabaseHelper.COL_FAV_IMAGE));
        obj.addProperty("episode", getString(c, DatabaseHelper.COL_FAV_EP));
        obj.addProperty("status",  getString(c, DatabaseHelper.COL_FAV_STATUS));
        obj.addProperty("rating",  getString(c, DatabaseHelper.COL_FAV_RATING));
        return new com.google.gson.Gson().fromJson(obj, Anime.class);
    }

    private String getString(Cursor c, String col) {
        int idx = c.getColumnIndex(col);
        return idx >= 0 ? c.getString(idx) : "";
    }
}
