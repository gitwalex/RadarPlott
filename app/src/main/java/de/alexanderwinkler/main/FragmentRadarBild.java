package de.alexanderwinkler.main;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.alexanderwinkler.Math.Punkt2D;
import de.alexanderwinkler.Math.Vektor2D;
import de.alexanderwinkler.R;
import de.alexanderwinkler.interfaces.Konstanten;
import de.alexanderwinkler.main.ActivityRadarPlottErgebnis.ManoeverListener;
import de.alexanderwinkler.views.ViewLage;
import de.alexanderwinkler.views.ViewRadarBasisBild;
import de.alexanderwinkler.views.ViewRadarBasisBild.OnScaleChangeListener;

public class FragmentRadarBild extends Fragment
        implements Konstanten, OnSharedPreferenceChangeListener, OnScaleChangeListener,
        OnTouchListener, ManoeverListener {
    private int angle;
    private boolean dual_pane;
    private ArrayList<ViewLage> mViewLageArray = new ArrayList<>();
    private boolean northupOrientierung;
    private int rastergroesse = 3;
    private ViewLage vLageB, vLageC, vLageD;
    private ViewRadarBasisBild vRadarbild;
    // View des Radarbildes
    private View view;

    public boolean getNorthupOrientierung() {
        return northupOrientierung;
    }

    public int getRastergroesse() {
        return rastergroesse;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sp != null) {
            sp.registerOnSharedPreferenceChangeListener(this);
            rastergroesse = Integer.valueOf(sp.getString(Konstanten.PREF_RADARKREISE, "3"));
            String orientierung = sp.getString(Konstanten.PREF_RADARORIENTIERUNG, "northup");
            northupOrientierung = orientierung.equals("northup");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_radarbild, container, false);
        vRadarbild = (ViewRadarBasisBild) view.findViewById(R.id.radarspinne);
        updateRastergroesse();
        updateNorthup();
        return view;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Alle Listener removen
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onScaleChanged(float scale) {
        if (scale != 0) {
            for (ViewLage l : mViewLageArray) {
                l.setScale(scale);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Konstanten.PREF_RADARKREISE)) {
            rastergroesse =
                    Integer.valueOf(sharedPreferences.getString(Konstanten.PREF_RADARKREISE, "3"));
            updateRastergroesse();
        }
        // Orientierung hat sich geaendert
        if (key.equals(Konstanten.PREF_RADARORIENTIERUNG)) {
            String orientierung =
                    sharedPreferences.getString(Konstanten.PREF_RADARORIENTIERUNG, "northup");
            // Orientierung ist NorthUp, daher Kompass auf 0 Grad drehen
            // Orientierung ist HeadUp, daher alle Bilder auf den
            // entsprechenende Kurs drehen
            northupOrientierung = orientierung.equals("northup");
            updateNorthup();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int oldangle = angle;
        boolean result = true;
        float x = event.getX() * 2 - vRadarbild.getWidth();
        float y = -(event.getY() * 2 - vRadarbild.getHeight());
        angle = (int) new Vektor2D(new Punkt2D(0, 0), new Punkt2D(x, y))
                .getWinkelRechtweisendNord();
        if (oldangle != angle) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event
                    .getAction() == MotionEvent.ACTION_MOVE) {
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.alexanderwinkler.main.ActivityRadarPlottErgebnis.ManoeverListener#
     * onUpdateManoever(int, de.alexanderwinkler.berechnungen.Manoever)
     */
    @Override
    public void onUpdateManoever(int senderId, Bundle lagebundle) {
        if (senderId != SENDERLageView) {
            for (ViewLage l : mViewLageArray) {
                l.updateManoever(lagebundle);
            }
        }
    }

    public void updateNorthup() {
        for (ViewLage l : mViewLageArray) {
            l.setNorthUpOrientierung(northupOrientierung);
        }
        vRadarbild.setNorthUpOrientierung(northupOrientierung);
    }

    public void updateRastergroesse() {
        for (ViewLage l : mViewLageArray) {
            l.setRastergroesse(rastergroesse);
        }
        vRadarbild.setRastergroesse(rastergroesse);
    }
}
