package com.animex.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.animex.app.ui.home.HomeFragment;
import com.animex.app.ui.ongoing.OngoingFragment;
import com.animex.app.ui.search.SearchFragment;
import com.animex.app.ui.history.HistoryFragment;
import com.animex.app.ui.favorites.FavoritesFragment;
import com.animex.app.util.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("AniMex");

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            int id = item.getItemId();
            if      (id == R.id.nav_home)      f = new HomeFragment();
            else if (id == R.id.nav_ongoing)   f = new OngoingFragment();
            else if (id == R.id.nav_search)    f = new SearchFragment();
            else if (id == R.id.nav_history)   f = new HistoryFragment();
            else if (id == R.id.nav_favorites) f = new FavoritesFragment();
            if (f != null) getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, f)
                    .commit();
            return f != null;
        });

        if (savedInstanceState == null)
            bottomNav.setSelectedItemId(R.id.nav_home);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem t = menu.findItem(R.id.action_toggle_theme);
        if (t != null) t.setTitle(ThemeManager.isDarkMode(this) ? "☀ Light" : "☾ Dark");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_theme) {
            ThemeManager.toggleTheme(this);
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
