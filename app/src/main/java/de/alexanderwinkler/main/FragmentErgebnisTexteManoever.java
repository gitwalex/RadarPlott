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
import de.alexanderwinkler.berechnungen.Manoever;
import de.alexanderwinkler.interfaces.FragmentIdServer;
import de.alexanderwinkler.interfaces.Konstanten;
import de.alexanderwinkler.interfaces.ManoeverServer;
import de.alexanderwinkler.main.ActivityRadarPlottErgebnis.FragmentChangeListener;
import de.alexanderwinkler.main.ActivityRadarPlottErgebnis.ManoeverListener;

public class FragmentErgebnisTexteManoever extends Fragment implements
		Konstanten, ManoeverListener, FragmentChangeListener {
	
	private Integer[] ergebnisfelder = { R.id.nKBr, R.id.nvBr, R.id.Delta,
			R.id.nRASP, R.id.nCPA, R.id.nPCPA, R.id.nSCPA, R.id.nTCPA,
			R.id.nECPA, R.id.nBCR, R.id.nBCT, R.id.KB, R.id.vB, R.id.nEBC };
	private TextView tvnKBr, tvnvBr, tvDelta, tvnRASP, tvnCPA, tvnPCPA,
			tvnSPCPA, tvnTCPA, tvnECPA, tvnBCR, tvnBCT, tvKB, tvvB, tvnBC;
	private boolean isInitialized;
	private Manoever manoever;
	private Bundle lagebundle;
	private boolean dual_pane;
	private String fragmentId;
	private Lage lage;
	private ManoeverServer mManoeverServer;
	private FragmentIdServer mFragmentIdServer;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mManoeverServer = (ManoeverServer) activity;
			
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ManoeverServer!");
		}
		try {
			mFragmentIdServer = (FragmentIdServer) activity;
			
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement FragmentIdServer!");
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
		return inflater.inflate(R.layout.fragment_ergebnis_texte_manoever,
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		lagebundle = mManoeverServer.getBundle();
		lage = (Lage) lagebundle.getSerializable(KEYLAGE + fragmentId);
		if (lage != null) {
			mFragmentIdServer.addFragmentChangeListener(this);
			dual_pane = lagebundle.getBoolean(KEYDUALPANE);
			onFragmentChange(mFragmentIdServer.getFragmentIdAktuell());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		mFragmentIdServer.removeFragmentChangeListener(this);
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
			tvnECPA.setText(Double.toString(rundeWert(manoever
					.getCPAEntfernung())));
			// Passieren der Kurslinie
			// BCR
			tvnBCR.setText(Double.toString(rundeWert(manoever.getBCR())));
			// BCT
			tvnBCT.setText(Double.toString(rundeWert((manoever.getBCT()))));
			// Entfernung bis Kurslinie
			tvnBC.setText(Double.toString(rundeWert(manoever
					.getEntfernungKurslinie())));
			if (dual_pane) {
				// KB
				tvKB.setText(Double.toString(rundeWert((lage.getKB()))));
				// vB
				tvvB.setText(Double.toString(rundeWert((lage.getvB()))));
			}
		}
	}
	
	private double rundeWert(double d) {
		return Math.round(d * 10.0) / 10.0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.interfaces.ManoeverListener#onUpdateManoever(int,
	 * java.lang.String, de.alexanderwinkler.berechnungen.Manoever)
	 */
	@Override
	public void onUpdateManoever(int senderId, Bundle lagebundle) {
		this.manoever = (Manoever) lagebundle.getSerializable(KEYMANOEVER
				+ fragmentId);
		update();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.main.ActivityRadarPlottErgebnis.FragmentChangeListener
	 * #onFragmentChange(java.lang.String,
	 * de.alexanderwinkler.berechnungen.Manoever)
	 */
	@Override
	public void onFragmentChange(String fragmentId) {
		if (fragmentId.equals(this.fragmentId)) {
			mManoeverServer.addManoeverListener(this);
			lagebundle = mManoeverServer.getBundle();
			manoever = (Manoever) lagebundle.getSerializable(KEYMANOEVER
					+ fragmentId);
			update();
		} else {
			mManoeverServer.removeManoeverListener(this);
		}
	}
}
