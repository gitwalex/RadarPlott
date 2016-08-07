package de.alexanderwinkler.main;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import de.alexanderwinkler.R;

/**
 * Created by alex on 05.08.2016.
 */
public class ActivityMain extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain_simple);
        FragmentErgebnisTexteLage frgErgebnis = new FragmentErgebnisTexteLage();
        getSupportFragmentManager().beginTransaction().
                add(R.id.fragmentcontainer, frgErgebnis).commit();
        ActionBar bar = getActionBar();
        assert bar != null;
        bar.setTitle("Radarplott");
    }
}
