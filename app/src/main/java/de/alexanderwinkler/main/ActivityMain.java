package de.alexanderwinkler.main;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import de.alexanderwinkler.R;

/**
 * Created by alex on 05.08.2016.
 */
public class ActivityMain extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private DrawerToggle mDrawerToggle;

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        FragmentEingabeDaten frg = new FragmentEingabeDaten();
        FragmentErgebnisTexteLage frgErgebnis = new FragmentErgebnisTexteLage();
        FragmentRadarSpinne radar = new FragmentRadarSpinne();
        getSupportFragmentManager().beginTransaction().
                add(R.id.fragradarspinne, radar).
                add(R.id.navigation_view, frg).
                add(R.id.fragmentcontainer, frgErgebnis).
                commit();
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle("Radarplott");
        bar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        bar.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new DrawerToggle(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDrawerLayout.removeDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private class DrawerToggle extends ActionBarDrawerToggle
            implements DrawerLayout.DrawerListener {
        private CharSequence savedSubtitel;

        public DrawerToggle(Activity activity) {
            super(activity, mDrawerLayout, R.string.app_name, R.string.Bearbeiten);
        }

        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            ActionBar bar = getSupportActionBar();
            bar.setTitle(R.string.app_name);
            bar.setSubtitle(savedSubtitel);
        }

        /**
         * Called when a drawer has settled in a completely open state.
         */
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            ActionBar bar = getSupportActionBar();
            bar.setTitle(R.string.Bearbeiten);
            savedSubtitel = bar.getSubtitle();
            bar.setSubtitle(null);
        }
    }
}
