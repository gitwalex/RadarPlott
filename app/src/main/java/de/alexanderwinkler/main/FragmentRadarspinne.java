package de.alexanderwinkler.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
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
public class FragmentRadarspinne extends Fragment implements View.OnClickListener {
    private static final String FAHRT = "FAHRT", KURS = "KURS", INTERVALL = "INTERVALL";
    private Punkt2D aktPositionEigSchiff = new Punkt2D(0, 0);
    private double mFahrt;
    private double mIntervall;
    private double mKurs;
    private ViewRadarBasisBild mRadarSpinne;
    private TextView tvEigKurs, tvEigFahrt, tvIntervall;

    private void initEigKurslinie(double kurs, double fahrt, double intervall) {
        mKurs = kurs;
        mFahrt = fahrt;
        mIntervall = intervall;
        double distanz = mFahrt / mIntervall;
        Punkt2D startPosition = aktPositionEigSchiff.mitWinkel(-distanz, mKurs);
        Kurslinie eigeneKurslinie = new Kurslinie(startPosition, aktPositionEigSchiff, mIntervall);
        tvEigKurs.setText(String.valueOf(mKurs));
        tvEigFahrt.setText(String.valueOf(mFahrt));
        tvIntervall.setText(String.valueOf(mIntervall));
        mRadarSpinne = (ViewRadarBasisBild) getView().findViewById(R.id.view_radarspinne);
        mRadarSpinne.setEigeneKurslinie(eigeneKurslinie);
    }

    @Override
    public void onClick(View v) {
        DialogEigSchiffDaten dlg =
                DialogEigSchiffDaten.newInstance(this, mKurs, mFahrt, mIntervall);
        dlg.show(getFragmentManager(), null);
    }

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
        outState.putDouble(INTERVALL, mIntervall);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mFahrt = savedInstanceState.getDouble(FAHRT);
            mKurs = savedInstanceState.getDouble(KURS);
            mIntervall = savedInstanceState.getDouble(INTERVALL);
        } else {
            // Defaultbelegung: Fahrt 6 Knoten, Kurs 0 Grad, Intervall 6 Minuten
            mFahrt = 12;
            mKurs = 182;
            mIntervall = 6;
        }
        tvEigKurs = (TextView) view.findViewById(R.id.tvEigKurs);
        tvEigFahrt = (TextView) view.findViewById(R.id.tvEigFahrt);
        tvIntervall = (TextView) view.findViewById(R.id.tvIntervall);
        view.findViewById(R.id.llEigSchiffDaten).setOnClickListener(this);
        initEigKurslinie(mKurs, mFahrt, mIntervall);
    }

    public static class DialogEigSchiffDaten extends AppCompatDialogFragment {
        private FragmentRadarspinne mFragmentRadarspinne;

        public static DialogEigSchiffDaten newInstance(FragmentRadarspinne frag, double kurs,
                                                       double fahrt, double intervall) {
            Bundle args = new Bundle();
            args.putDouble(KURS, kurs);
            args.putDouble(FAHRT, fahrt);
            args.putDouble(INTERVALL, intervall);
            DialogEigSchiffDaten fragment = new DialogEigSchiffDaten();
            fragment.setFragmentRadarSpinne(frag);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.view_daten_eigenes_schiff, null);
            final TextView eKurs = (TextView) view.findViewById(R.id.etEigKurs);
            final TextView eFahrt = (TextView) view.findViewById(R.id.eEigFahrt);
            final TextView eIntervall = (TextView) view.findViewById(R.id.eIntervall);
            Bundle args = getArguments();
            eKurs.setText(String.valueOf(args.getDouble(KURS)));
            eFahrt.setText(String.valueOf(args.getDouble(FAHRT)));
            eIntervall.setText(String.valueOf(args.getDouble(INTERVALL)));
            return new AlertDialog.Builder(getActivity()).setView(view).
                    setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            double kurs = 0, fahrt = 0, intervall = 0;
                            String sKurs = eKurs.getText().toString();
                            if (!TextUtils.isEmpty(sKurs)) {
                                kurs = Double.parseDouble(sKurs);
                            }
                            String sFahrt = eFahrt.getText().toString();
                            if (!TextUtils.isEmpty(sFahrt)) {
                                fahrt = Double.parseDouble(sFahrt);
                            }
                            String sIntervall = eIntervall.getText().toString();
                            if (!TextUtils.isEmpty(sIntervall)) {
                                intervall = Double.parseDouble(sIntervall);
                            }
                            if (intervall != 0) {
                                mFragmentRadarspinne.initEigKurslinie(kurs, fahrt, intervall);
                            }
                        }
                    }).create();
        }

        private void setFragmentRadarSpinne(FragmentRadarspinne frg) {
            mFragmentRadarspinne = frg;
        }
    }
}
