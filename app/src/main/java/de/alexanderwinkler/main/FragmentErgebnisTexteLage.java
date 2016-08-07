package de.alexanderwinkler.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.alexanderwinkler.R;
import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.interfaces.Konstanten;

public class FragmentErgebnisTexteLage extends Fragment implements Konstanten {
    private Integer[] ergebnisfelder =
            {R.id.KBr, R.id.vBr, R.id.KB, R.id.vB, R.id.CPA, R.id.PCPA, R.id.SCPA, R.id.TCPA,
                    R.id.BCR, R.id.BCT};
    private String fragmentId;
    private boolean isInitialized;
    private Lage lage;
    private TextView tvKBr, tvvBr, tvKB, tvvB, tvCPAEntfernung, tvPCPA, tvSCPA, tvTCPA, tvBCR,
            tvBCT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ergebnis_texte_lage, container, false);
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
        isInitialized = false;
        int wert;
        for (int i = 0; i < ergebnisfelder.length; i++) {
            wert = ergebnisfelder[i];
            switch (wert) {
                case R.id.KBr:
                    tvKBr = (TextView) view.findViewById(wert);
                    break;
                case R.id.vBr:
                    tvvBr = (TextView) view.findViewById(wert);
                    break;
                case R.id.KB:
                    tvKB = (TextView) view.findViewById(wert);
                    break;
                case R.id.vB:
                    tvvB = (TextView) view.findViewById(wert);
                    break;
                case R.id.CPA:
                    tvCPAEntfernung = (TextView) view.findViewById(wert);
                    break;
                case R.id.PCPA:
                    tvPCPA = (TextView) view.findViewById(wert);
                    break;
                case R.id.SCPA:
                    tvSCPA = (TextView) view.findViewById(wert);
                    break;
                case R.id.TCPA:
                    tvTCPA = (TextView) view.findViewById(wert);
                    break;
                case R.id.BCR:
                    tvBCR = (TextView) view.findViewById(wert);
                    break;
                case R.id.BCT:
                    tvBCT = (TextView) view.findViewById(wert);
                    break;
                default:
            }
            isInitialized = true;
        }
    }

    private double rundeWert(double d) {
        return Math.round(d * 10.0) / 10.0;
    }

    /**
     */
    public void update() {
        if (isInitialized && lage != null) {
            // Belege Felder mit Ergebnissen
            // KBr
            tvKBr.setText(Double.toString(rundeWert(lage.getKBr())));
            // vBr
            tvvBr.setText(Double.toString(rundeWert(lage.getvBr())));
            // KB
            tvKB.setText(Double.toString(rundeWert(lage.getKB())));
            // vB
            tvvB.setText(Double.toString(rundeWert(lage.getvB())));
            // CPA
            tvCPAEntfernung.setText(Double.toString(rundeWert(lage.getCPA())));
            // PCPA
            tvPCPA.setText(Double.toString(rundeWert(lage.getPCPA())));
            // SCPA
            tvSCPA.setText(Double.toString(rundeWert(lage.getSCPA())));
            // TCPA
            tvTCPA.setText(Double.toString(rundeWert(lage.getTCPA())));
            // Passieren der Kurslinie
            // BCR
            tvBCR.setText(Double.toString(rundeWert(lage.getBCR())));
            // BCT
            tvBCT.setText(Double.toString(rundeWert((lage.getBCT()))));
        }
    }
}