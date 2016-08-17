package de.alexanderwinkler.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.alexanderwinkler.R;
import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.berechnungen.Manoever;
import de.alexanderwinkler.interfaces.Konstanten;

public class FragmentErgebnisTexteManoever extends Fragment implements Konstanten {
    private boolean dual_pane;
    private Integer[] ergebnisfelder =
            {R.id.nKBr, R.id.nvBr, R.id.Delta, R.id.nRASP, R.id.nCPA, R.id.nPCPA, R.id.nSCPA,
                    R.id.nTCPA, R.id.nECPA, R.id.nBCR, R.id.nBCT, R.id.KB, R.id.vB, R.id.nEBC};
    private String fragmentId;
    private boolean isInitialized;
    private Lage lage;
    private Bundle lagebundle;
    private Manoever manoever;
    private TextView tvnKBr, tvnvBr, tvDelta, tvnRASP, tvnCPA, tvnPCPA, tvnSPCPA, tvnTCPA, tvnECPA,
            tvnBCR, tvnBCT, tvKB, tvvB, tvnBC;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            fragmentId = getArguments().getString(KEYFRAGMENTID);
        } else {
            fragmentId = savedInstanceState.getString(KEYFRAGMENTID);
            isInitialized = savedInstanceState.getBoolean(KEYISINITIALIZED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ergebnis_texte_manoever, container, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        lage = (Lage) lagebundle.getSerializable(KEYLAGE + fragmentId);
        if (lage != null) {
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEYFRAGMENTID, fragmentId);
        outState.putBoolean(KEYISINITIALIZED, isInitialized);
        super.onSaveInstanceState(outState);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int wert;
        for (int i = 0; i < ergebnisfelder.length; i++) {
            wert = ergebnisfelder[i];
            switch (wert) {
                case R.id.nKBr:
                    tvnKBr = (TextView) view.findViewById(wert);
                    break;
                case R.id.nvBr:
                    tvnvBr = (TextView) view.findViewById(wert);
                    break;
                case R.id.Delta:
                    tvDelta = (TextView) view.findViewById(wert);
                    break;
                case R.id.nRASP:
                    tvnRASP = (TextView) view.findViewById(wert);
                    break;
                case R.id.nCPA:
                    tvnCPA = (TextView) view.findViewById(wert);
                    break;
                case R.id.nPCPA:
                    tvnPCPA = (TextView) view.findViewById(wert);
                    break;
                case R.id.nSCPA:
                    tvnSPCPA = (TextView) view.findViewById(wert);
                    break;
                case R.id.nTCPA:
                    tvnTCPA = (TextView) view.findViewById(wert);
                    break;
                case R.id.nECPA:
                    tvnECPA = (TextView) view.findViewById(wert);
                    break;
                case R.id.nBCR:
                    tvnBCR = (TextView) view.findViewById(wert);
                    break;
                case R.id.nBCT:
                    tvnBCT = (TextView) view.findViewById(wert);
                    break;
                case R.id.KB:
                    tvKB = (TextView) view.findViewById(wert);
                    break;
                case R.id.vB:
                    tvvB = (TextView) view.findViewById(wert);
                    break;
                case R.id.nEBC:
                    tvnBC = (TextView) view.findViewById(wert);
                    break;
                default:
            }
        }
        isInitialized = true;
    }

    private double rundeWert(double d) {
        return Math.round(d * 10.0) / 10.0;
    }

    public void update() {
        if (isInitialized && manoever != null) {
            // Belege Felder mit Ergebnissen
            tvnKBr.setText(Double.toString(rundeWert(manoever.getKBr())));
            // vBr
            tvnvBr.setText(Double.toString(rundeWert(manoever.getvBr())));
            // vBr
            tvDelta.setText(Double.toString(rundeWert(manoever.getDelta())));
            // RASP
            tvnRASP.setText(Double.toString(rundeWert(manoever.getRASP2())));
            // CPA
            tvnCPA.setText(Double.toString(rundeWert(manoever.getCPA())));
            // PCPA
            tvnPCPA.setText(Double.toString(rundeWert(manoever.getPCPA())));
            // SCPA
            tvnSPCPA.setText(Double.toString(rundeWert(manoever.getSCPA())));
            // TCPA
            tvnTCPA.setText(Double.toString(rundeWert(manoever.getTCPA())));
            // Entfernung bis CPA
            tvnECPA.setText(Double.toString(rundeWert(manoever.getCPAEntfernung())));
            // Passieren der EigenesSchiff
            // BCR
            tvnBCR.setText(Double.toString(rundeWert(manoever.getBCR())));
            // BCT
            tvnBCT.setText(Double.toString(rundeWert((manoever.getBCT()))));
            // Entfernung bis EigenesSchiff
            tvnBC.setText(Double.toString(rundeWert(manoever.getEntfernungKurslinie())));
            if (dual_pane) {
                // KB
                tvKB.setText(Double.toString(rundeWert((lage.getKB()))));
                // vB
                tvvB.setText(Double.toString(rundeWert((lage.getvB()))));
            }
        }
    }
}
