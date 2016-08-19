package de.aw.radarplott.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;

import de.aw.radarplott.R;
import de.aw.radarplott.div.MaxInhaltTextWatcher;
import de.aw.radarplott.div.MaxInhaltTextWatcher.ErgebnisListener;
import de.aw.radarplott.interfaces.Konstanten;

public class FragmentEingabeEigSchiff extends Fragment implements Konstanten,
		OnFocusChangeListener {
	private final String fragmentId = KEYFRAGMENTEIGSCHIFF;
	// In diesem Bundle werden die Eingaben beseichert. Wichtig fuer restore und
	// Weitergabe der Daten an FolgeActivity
	private Bundle eingabewerte = new Bundle();
	// Eingabefelder
	private EditText etKurs, etFahrt, etIntervall;
	// etext haelt referencen auf die EditTextFelder. Damit wird das
	// zurueckholen der eingegeben Werte in onRestoreInstanceStae einfacher.
	private EditText[] etext;
	private ErgebnisListener mErgebnisListener;
	private View view;
	
	/*
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			eingabewerte = savedInstanceState.getBundle(fragmentId);
			String wert;
			for (int i = 0; i < eingabewerteEigSchifIDs.length; i++) {
				wert = eingabewerte.getString(Integer
						.toString(eingabewerteEigSchifIDs[i]));
				if (wert != null) {
					etext[i].setText(wert);
				}
			}
		}

		etext = new EditText[eingabewerteEigSchifIDs.length];
		int indexEText = 0;
		EditText et;
		eingabewerte.putInt(KEYEINGABEOK, 0);
		for (int i = 0; i < eingabewerteGesamtIDs.length; i++) {
			// Initialisieren der Felder,
			// MaxInhaltTextWatcher/KursTextWatcher
			// registrieren.
			et = (EditText) view.findViewById(eingabewerteGesamtIDs[i]);
			if (et != null) {
				// Merken, wenn eine View gefunden wurde. Wird in
				// Textbelegung
				// benoetigt.
				etext[indexEText] = et;
				indexEText++;
				et.setOnFocusChangeListener(this);
				switch (eingabewerteEigSchifIDs[i]) {
				case R.id.eEigKurs:
					etKurs = et;
					etKurs.addTextChangedListener(new MaxInhaltTextWatcher(
							mErgebnisListener, fragmentId, etKurs, i));
					et.setTag(Double.valueOf(0d));
					break;
				case R.id.eEigFahrt:
					etFahrt = et;
					etFahrt.addTextChangedListener(new MaxInhaltTextWatcher(
							mErgebnisListener, fragmentId, etFahrt, i));
					break;
				case R.id.eIntervall:
					etIntervall = et;
					etIntervall
							.addTextChangedListener(new MaxInhaltTextWatcher(
									mErgebnisListener, fragmentId, etIntervall,
									i));
					break;
				default:
				}
			}
		}
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
			mErgebnisListener = (ErgebnisListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ErgebnisListener!");
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
		view = inflater.inflate(R.layout.fragment_eingabe_eigdaten, container,
				false);
		return view;
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		EditText et = (EditText) v;
		if (hasFocus) {
			et.setSelection(et.getText().length());
		} else {
			eingabewerte.putString(Integer.toString(v.getId()), et.getText().toString());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBundle(fragmentId, eingabewerte);
		outState.putString(KEYFRAGMENTID, fragmentId);
		super.onSaveInstanceState(outState);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onStart() {
		super.onStart();
		testBelegung();
		etKurs.requestFocus();
	}
	
	private void testBelegung() {
		double[] d = { 165, 6, 6 };
		for (int i = 0; i < eingabewerteEigSchifIDs.length; i++) {
			int id = eingabewerteEigSchifIDs[i];
			double wert = d[i];
			eingabewerte.putDouble(Integer.toString(id), wert);
			String text = Double.toString(d[i]);
			etext[i].setText(text);
		}
	}
}
