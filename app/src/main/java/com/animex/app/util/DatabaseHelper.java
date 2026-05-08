package com.animex.app.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "animex.db";
    private static final int    DB_VERSION = 1;

    // Table: favorites
    public static final String TABLE_FAV       = "favorites";
    public static final String COL_FAV_SLUG    = "anime_slug";
    public static final String COL_FAV_TITLE   = "title";
    public static final String COL_FAV_IMAGE   = "image";
    public static final String COL_FAV_EP      = "episode";
    public static final String COL_FAV_STATUS  = "status";
    public static final String COL_FAV_RATING  = "rating";
    public static final String COL_FAV_ADDED   = "added_at";

    // Table: votes (like/dislike per episode)
    public static final String TABLE_VOTE      = "votes";
    public static final String COL_VOTE_SLUG   = "episode_slug";
    public static final String COL_VOTE_TYPE   = "vote_type"; // "like" | "dislike" | "none"
    public static final String COL_VOTE_LIKES  = "likes";
    public static final String COL_VOTE_DISLIKES = "dislikes";

    // Table: watch_history
    public static final String TABLE_HIST      = "watch_history";
    public static final String COL_HIST_EP_SLUG  = "episode_slug";
    public static final String COL_HIST_EP_TITLE = "episode_title";
    public static final String COL_HIST_AN_SLUG  = "anime_slug";
    public static final String COL_HIST_AN_TITLE = "anime_title";
    public static final String COL_HIST_AN_IMAGE = "anime_image";
    public static final String COL_HIST_WATCHED  = "watched_at";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null)
            instance = new DatabaseHelper(ctx.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context ctx) { super(ctx, DB_NAME, null, DB_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FAV + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_FAV_SLUG   + " TEXT UNIQUE NOT NULL," +
            COL_FAV_TITLE  + " TEXT," +
            COL_FAV_IMAGE  + " TEXT," +
            COL_FAV_EP     + " TEXT," +
            COL_FAV_STATUS + " TEXT," +
            COL_FAV_RATING + " TEXT," +
            COL_FAV_ADDED  + " INTEGER DEFAULT (strftime('%s','now'))" +
            ")");

        db.execSQL("CREATE TABLE " + TABLE_VOTE + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_VOTE_SLUG     + " TEXT UNIQUE NOT NULL," +
            COL_VOTE_TYPE     + " TEXT DEFAULT 'none'," +
            COL_VOTE_LIKES    + " INTEGER DEFAULT 0," +
            COL_VOTE_DISLIKES + " INTEGER DEFAULT 0" +
            ")");

        db.execSQL("CREATE TABLE " + TABLE_HIST + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_HIST_EP_SLUG  + " TEXT NOT NULL," +
            COL_HIST_EP_TITLE + " TEXT," +
            COL_HIST_AN_SLUG  + " TEXT," +
            COL_HIST_AN_TITLE + " TEXT," +
            COL_HIST_AN_IMAGE + " TEXT," +
            COL_HIST_WATCHED  + " INTEGER DEFAULT (strftime('%s','now'))" +
            ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIST);
        onCreate(db);
    }
}
