package de.alexanderwinkler.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.alexanderwinkler.Math.Punkt2D;
import de.alexanderwinkler.R;
import de.alexanderwinkler.berechnungen.Kurslinie;
import de.alexanderwinkler.views.ViewRadarBasisBild;

/**
 * Created by alex on 08.08.2016.
 */
public class FragmentRadarspinne extends Fragment {
    private static final String FAHRT = "FAHRT", KURS = "KURS", INTERVALL = "INTERVALL";
    private Punkt2D aktPositionEigSchiff = new Punkt2D(0, 0);
    private double intervall;
    private double mFahrt;
    private double mKurs;
    private ViewRadarBasisBild mRadarSpinne;
    private TextView tvEigKurs, tvEigFahrt, tvIntervall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_radarspinne, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putDouble(FAHRT, mFahrt);
        outState.putDouble(KURS, mKurs);
        outState.putDouble(INTERVALL, intervall);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mFahrt = savedInstanceState.getDouble(FAHRT);
            mKurs = savedInstanceState.getDouble(KURS);
            intervall = savedInstanceState.getDouble(INTERVALL);
        } else {
            // Defaultbelegung: Fahrt 6 Knoten, Kurs 0 Grad, Intervall 6 Minuten
            mFahrt = 12;
            mKurs = 182;
            intervall = 6;
        }
        double distanz = mFahrt / intervall;
        Punkt2D startPosition = aktPositionEigSchiff.mitWinkel(-distanz, mKurs);
        Kurslinie eigeneKurslinie = new Kurslinie(startPosition, aktPositionEigSchiff, intervall);
        tvEigKurs = (TextView) view.findViewById(R.id.tvEigKurs);
        tvEigKurs.setText(String.valueOf(mKurs));
        tvEigFahrt = (TextView) view.findViewById(R.id.tvEigFahrt);
        tvEigFahrt.setText(String.valueOf(mFahrt));
        tvIntervall = (TextView) view.findViewById(R.id.tvIntervall);
        tvIntervall.setText(String.valueOf(intervall));
        mRadarSpinne = (ViewRadarBasisBild) view.findViewById(R.id.view_radarspinne);
        mRadarSpinne.setEigeneKurslinie(eigeneKurslinie);
    }
}
