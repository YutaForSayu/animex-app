package com.animex.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private final DatabaseHelper db;

    public static class HistoryItem {
        public String episodeSlug, episodeTitle, animeSlug, animeTitle, animeImage;
        public long   watchedAt;
    }

    public HistoryManager(Context ctx) { db = DatabaseHelper.getInstance(ctx); }

    public void record(String epSlug, String epTitle, String anSlug, String anTitle, String anImage) {
        // Remove old entry for same episode first (avoid duplicates)
        db.getWritableDatabase().delete(DatabaseHelper.TABLE_HIST,
            DatabaseHelper.COL_HIST_EP_SLUG + "=?", new String[]{epSlug});

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_HIST_EP_SLUG,  epSlug);
        cv.put(DatabaseHelper.COL_HIST_EP_TITLE, epTitle);
        cv.put(DatabaseHelper.COL_HIST_AN_SLUG,  anSlug);
        cv.put(DatabaseHelper.COL_HIST_AN_TITLE, anTitle);
        cv.put(DatabaseHelper.COL_HIST_AN_IMAGE, anImage);
        cv.put(DatabaseHelper.COL_HIST_WATCHED,  System.currentTimeMillis() / 1000);
        db.getWritableDatabase().insert(DatabaseHelper.TABLE_HIST, null, cv);
    }

    public List<HistoryItem> getAll() {
        List<HistoryItem> list = new ArrayList<>();
        Cursor c = db.getReadableDatabase().query(DatabaseHelper.TABLE_HIST,
            null, null, null, null, null,
            DatabaseHelper.COL_HIST_WATCHED + " DESC");
        while (c.moveToNext()) {
            HistoryItem h = new HistoryItem();
            h.episodeSlug  = getStr(c, DatabaseHelper.COL_HIST_EP_SLUG);
            h.episodeTitle = getStr(c, DatabaseHelper.COL_HIST_EP_TITLE);
            h.animeSlug    = getStr(c, DatabaseHelper.COL_HIST_AN_SLUG);
            h.animeTitle   = getStr(c, DatabaseHelper.COL_HIST_AN_TITLE);
            h.animeImage   = getStr(c, DatabaseHelper.COL_HIST_AN_IMAGE);
            h.watchedAt    = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_HIST_WATCHED));
            list.add(h);
        }
        c.close();
        return list;
    }

    public void clear() {
        db.getWritableDatabase().delete(DatabaseHelper.TABLE_HIST, null, null);
    }

    private String getStr(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getString(i) : "";
    }
}
