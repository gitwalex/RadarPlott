package de.alexanderwinkler.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.alexanderwinkler.R;
import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.interfaces.BundleServer;
import de.alexanderwinkler.interfaces.Konstanten;

public class FragmentErgebnisTexteLage extends Fragment implements Konstanten {
	
	private Integer[] ergebnisfelder = { R.id.KBr, R.id.vBr, R.id.KB, R.id.vB,
			R.id.CPA, R.id.PCPA, R.id.SCPA, R.id.TCPA, R.id.BCR, R.id.BCT };
	private TextView tvKBr, tvvBr, tvKB, tvvB, tvCPAEntfernung, tvPCPA, tvSCPA,
			tvTCPA, tvBCR, tvBCT;
	private boolean isInitialized;
	private Lage lage;
	private Bundle lagebundle;
	private BundleServer mBundleServer;
	private String fragmentId;
	
	public FragmentErgebnisTexteLage() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mBundleServer = (BundleServer) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement BundleServer!");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			fragmentId = savedInstanceState.getString(KEYFRAGMENTID);
		} else {
			fragmentId = getArguments().getString(KEYFRAGMENTID);
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
		return inflater.inflate(R.layout.fragment_ergebnis_texte_lage,
				container, false);
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(KEYFRAGMENTID, fragmentId);
		super.onSaveInstanceState(outState);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		lagebundle = mBundleServer.getBundle();
		update();
	}
	
	/**
	 * @param l
	 */
	public void update() {
		lage = (Lage) lagebundle.getSerializable(KEYLAGE + fragmentId);
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
	
	private double rundeWert(double d) {
		return Math.round(d * 10.0) / 10.0;
	}
}