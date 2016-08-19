package de.aw.radarplott.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.aw.radarplott.R;
import de.aw.radarplott.interfaces.BundleServer;
import de.aw.radarplott.interfaces.Konstanten;

public class FragmentErgebnisEingabeTexte extends Fragment implements
		Konstanten {
	double[] eingabedaten;
	private double[] eingabewerte = new double[eingabewerteGesamtIDs.length];
	private Integer[] ergebnisfelder = { R.id.tveEigKurs, R.id.tveEigFahrt,
			R.id.tveIntervall, R.id.tveSeitenpeilung1, R.id.tvAnlKurs1,
			R.id.tveRadarSeitenpeilung1, R.id.tveDistanz1,
			R.id.tveSeitenpeilung2, R.id.tvAnlKurs2,
			R.id.tveRadarSeitenpeilung2, R.id.tveDistanz2 };
	private String fragmentId;
	private boolean isInitialized;
	private Bundle lagebundle;
	private BundleServer mBundleServer;
	private TextView tveEigKurs, tveEigFahrt, tveIntervall, tveSeitenpeilung1,
			tvAnlKurs1, tvRadarSeitenpeilung1, tveDistanz1, tveSeitenpeilung2,
			tvAnlKurs2, tvRadarSeitenpeilung2, tveDistanz2;
	public FragmentErgebnisEingabeTexte() {
		super();
		Log.d("RADAR", "FragmentTextEingabe created");
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
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			fragmentId = savedInstanceState.getString(KEYFRAGMENTID);
		} else {
			fragmentId = getArguments().getString(KEYFRAGMENTID);
		}
		return inflater.inflate(R.layout.fragment_ergebnis_texte_eingabe,
				container, false);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// mBundleServer.removeBundleListener(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		lagebundle = mBundleServer.getBundle();
		update();
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
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		isInitialized = false;
		int wert;
		for (int i = 0; i < ergebnisfelder.length; i++) {
			wert = ergebnisfelder[i];
			switch (wert) {
			case R.id.tveEigKurs:
				tveEigKurs = (TextView) view.findViewById(wert);
				break;
			case R.id.tveEigFahrt:
				tveEigFahrt = (TextView) view.findViewById(wert);
				break;
			case R.id.tveIntervall:
				tveIntervall = (TextView) view.findViewById(wert);
				break;
			case R.id.tveSeitenpeilung1:
				tveSeitenpeilung1 = (TextView) view.findViewById(wert);
				break;
			case R.id.tvAnlKurs1:
				tvAnlKurs1 = (TextView) view.findViewById(wert);
				break;
			case R.id.tveRadarSeitenpeilung1:
				tvRadarSeitenpeilung1 = (TextView) view.findViewById(wert);
				break;
			case R.id.tveDistanz1:
				tveDistanz1 = (TextView) view.findViewById(wert);
				break;
			case R.id.tveSeitenpeilung2:
				tveSeitenpeilung2 = (TextView) view.findViewById(wert);
				break;
			case R.id.tvAnlKurs2:
				tvAnlKurs2 = (TextView) view.findViewById(wert);
				break;
			case R.id.tveRadarSeitenpeilung2:
				tvRadarSeitenpeilung2 = (TextView) view.findViewById(wert);
				break;
			case R.id.tveDistanz2:
				tveDistanz2 = (TextView) view.findViewById(wert);
				break;
			default:
			}
			isInitialized = true;
		}
	}
	
	public void update() {
		if (isInitialized) {
			eingabedaten = lagebundle.getDoubleArray(KEYEINGABE + fragmentId);
			double wert;
			if (eingabedaten != null) {
				for (int i = 0; i < eingabewerte.length; i++) {
					wert = eingabedaten[i];
					switch (Konstanten.eingabewerteGesamtIDs[i]) {
					
					case R.id.eEigKurs:
						tveEigKurs.setText(Double.toString(wert));
						break;
					case R.id.eEigFahrt:
						tveEigFahrt.setText(Double.toString(wert));
						break;
					case R.id.eIntervall:
						tveIntervall.setText(Double.toString(wert));
						break;
					case R.id.eSeitenpeilung1:
						tveSeitenpeilung1.setText(Double.toString(wert));
						break;
					case R.id.anliegenderKurs1:
						tvAnlKurs1.setText(Double.toString(wert));
						break;
					case R.id.eRadarSeitenpeilung1:
						tvRadarSeitenpeilung1.setText(Double.toString(wert));
						break;
					case R.id.eDistanz1:
						tveDistanz1.setText(Double.toString(wert));
						break;
					case R.id.eSeitenpeilung2:
						tveSeitenpeilung2.setText(Double.toString(wert));
						break;
					case R.id.anliegenderKurs2:
						tvAnlKurs2.setText(Double.toString(wert));
						break;
					case R.id.eRadarSeitenpeilung2:
						tvRadarSeitenpeilung2.setText(Double.toString(wert));
						break;
					case R.id.eDistanz2:
						tveDistanz2.setText(Double.toString(wert));
						break;
					default:
					}
				}
			}
		}
	}
}
