package de.alexanderwinkler.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import de.alexanderwinkler.R;
import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.div.MaxInhaltTextWatcher;
import de.alexanderwinkler.div.MaxInhaltTextWatcher.ErgebnisListener;
import de.alexanderwinkler.interfaces.Konstanten;
import de.alexanderwinkler.interfaces.KursWerteListener;
import de.alexanderwinkler.main.ActivityRadarPlottEingabe.EigenerKursListener;

public class FragmentEingabeGegnerWerte extends Fragment implements Konstanten,
		OnClickListener, OnFocusChangeListener, EigenerKursListener {
	
	private String fragmentId;
	
	// Eingabefelder
	private EditText etSP1, etAnlKurs1, etRASP1, etDinstanz1, etSP2,
			etAnlKurs2, etRASP2, etDistanz2;
	// etext haelt referencen auf die EditTextFelder. Damit wird das
	// zurueckholen der eingegeben Werte in onRestoreInstanceState einfacher.
	private EditText[] etext;
	// In diesem Bundle werden die Eingaben gesichert. Wichtig fuer restore und
	// Weitergabe der Daten an FolgeActivity
	private Bundle eingabewerte = new Bundle();
	private View view;
	
	private ErgebnisListener mErgListener;
	private KursWerteListener mKursWerteListener;
	private double eigKurs, oldEigKurs;
	public FragmentEingabeGegnerWerte() {
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
			mErgListener = (ErgebnisListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ErgebnisListener!");
		}
		try {
			mKursWerteListener = (KursWerteListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement KursWerteListener!");
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
		String wert;
		if (savedInstanceState != null) {
			fragmentId = savedInstanceState.getString(KEYFRAGMENTID);
			eingabewerte = savedInstanceState.getBundle(fragmentId);
			for (int i = 0; i < eingabewerteGegnerIDs.length; i++) {
				wert = eingabewerte.getString(Integer
						.toString(eingabewerteGegnerIDs[i]));
				if (wert != null) {
					etext[i].setText(wert);
				}
			}
			// testBelegung();
		} else {
			fragmentId = getArguments().getString(KEYFRAGMENTID);
		}
		
		view = inflater.inflate(R.layout.fragment_eingabe_gegnerwerte,
				container, false);
		RadioButton rb;
		rb = (RadioButton) view.findViewById(R.id.radioButtonRASP1);
		rb.setOnClickListener(FragmentEingabeGegnerWerte.this);
		rb = (RadioButton) view.findViewById(R.id.radioButtonRASP2);
		rb.setOnClickListener(FragmentEingabeGegnerWerte.this);
		rb = (RadioButton) view.findViewById(R.id.radioButtonSP1);
		rb.setOnClickListener(FragmentEingabeGegnerWerte.this);
		rb = (RadioButton) view.findViewById(R.id.radioButtonSP2);
		rb.setOnClickListener(FragmentEingabeGegnerWerte.this);
		etext = new EditText[eingabewerteGegnerIDs.length];
		int indexEText = 0;
		EditText et;
		eingabewerte.putInt(KEYEINGABEOK, 0);
		for (int i = 0; i < eingabewerteGesamtIDs.length; i++) {
			// Initialisieren der Felder,
			// MaxInhaltTextWatcher/KursTextWatcher
			// registrieren.
			et = (EditText) view.findViewById(eingabewerteGesamtIDs[i]);
			if (et != null) {
				// Merken, wenn eine View gefunden wurde. Wird in Textbelegung
				// benoetigt.
				etext[indexEText] = et;
				indexEText++;
				et.setOnFocusChangeListener(FragmentEingabeGegnerWerte.this);
				switch (eingabewerteGesamtIDs[i]) {
				case R.id.eSeitenpeilung1:
					etSP1 = et;
					etSP1.addTextChangedListener(new KursTextWatcher(
							mErgListener, fragmentId, etSP1, i));
					break;
				case R.id.anliegenderKurs1:
					etAnlKurs1 = et;
					etAnlKurs1.addTextChangedListener(new KursTextWatcher(
							mErgListener, fragmentId, etAnlKurs1, i));
					break;
				case R.id.eRadarSeitenpeilung1:
					et.setInputType(InputType.TYPE_NULL);
					etRASP1 = et;
					etRASP1.addTextChangedListener(new KursTextWatcher(
							mErgListener, fragmentId, etRASP1, i));
					break;
				case R.id.eDistanz1:
					etDinstanz1 = et;
					etDinstanz1
							.addTextChangedListener(new MaxInhaltTextWatcher(
									mErgListener, fragmentId, etDinstanz1, i));
					break;
				case R.id.eSeitenpeilung2:
					etSP2 = et;
					etSP2.addTextChangedListener(new KursTextWatcher(
							mErgListener, fragmentId, etSP2, i));
					break;
				case R.id.anliegenderKurs2:
					etAnlKurs2 = et;
					etAnlKurs2.addTextChangedListener(new KursTextWatcher(
							mErgListener, fragmentId, etAnlKurs2, i));
					break;
				case R.id.eRadarSeitenpeilung2:
					etRASP2 = et;
					etRASP2.addTextChangedListener(new KursTextWatcher(
							mErgListener, fragmentId, etRASP2, i));
					break;
				case R.id.eDistanz2:
					etDistanz2 = et;
					etDistanz2.addTextChangedListener(new MaxInhaltTextWatcher(
							mErgListener, fragmentId, etDistanz2, i));
					break;
				default:
				}
			}
		}			
			// RadioButtons: Per Default ist Seitenpeilung editierbar
			// (checked)
			// Daher die EditFelder RASPx disablen
			etRASP1.setEnabled(false);
			etRASP2.setEnabled(false);
			Button bClear = (Button) view.findViewById(R.id.bClear);
			bClear.setOnClickListener(FragmentEingabeGegnerWerte.this);
			return view;
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
		double k = mKursWerteListener.getAktKurs();
		if (k != -1) {
			etAnlKurs1.setText(Double.toString(k));
			etAnlKurs2.setText(Double.toString(k));
			
		}
		mKursWerteListener.addEigenerKursChangedListener(this);
		testBelegung();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		mKursWerteListener.removeEigenerKursChangedListener(this);
	}
	
	private void testBelegung() {
		double[] d = {};
		if (KEYGEGNERB.equals(fragmentId)) {
			double[] dd = { 302, 2.3, 295, 1.8 };
			d = dd;
		}
		if (KEYGEGNERC.equals(fragmentId)) {
			double[] dd = { 186, 1.9, 172, 1.7 };
			d = dd;
		}
		if (KEYGEGNERD.equals(fragmentId)) {
			double[] dd = { 31, 2.3, 61, 1.8 };
			d = dd;
		}
		int index = 0;
		for (int i = 0; i < eingabewerteGegnerIDs.length; i++) {
			int id = eingabewerteGegnerIDs[i];
			double wert = d[index];
			String text = Double.toString(d[index]);
			switch (id) {
			case R.id.eSeitenpeilung1:
				eingabewerte.putDouble(Integer.toString(id), wert);
				etext[i].setText(text);
				index++;
				break;
			case R.id.eSeitenpeilung2:
				eingabewerte.putDouble(Integer.toString(id), wert);
				etext[i].setText(text);
				index++;
				break;
			case R.id.eDistanz1:
				eingabewerte.putDouble(Integer.toString(id), wert);
				etext[i].setText(text);
				index++;
				break;
			case R.id.eDistanz2:
				eingabewerte.putDouble(Integer.toString(id), wert);
				etext[i].setText(text);
				index++;
				break;
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.radioButtonSP1:
			setEnabled(etSP1);
			etSP1.requestFocus();
			setEnabled(etAnlKurs1);
			setDisabled(etRASP1);
			break;
		case R.id.radioButtonRASP1:
			setDisabled(etSP1);
			setDisabled(etAnlKurs1);
			setEnabled(etRASP1);
			etRASP1.requestFocus();
			break;
		case R.id.radioButtonSP2:
			setEnabled(etSP2);
			etSP2.requestFocus();
			setEnabled(etAnlKurs2);
			setDisabled(etRASP2);
			break;
		case R.id.radioButtonRASP2:
			setDisabled(etSP2);
			setDisabled(etAnlKurs2);
			setEnabled(etRASP2);
			etRASP2.requestFocus();
			break;
		case R.id.bClear:
			for (int i = 0; i < eingabewerteGegnerIDs.length; i++) {
				etext[i].setText("");
			}
			etext[1].setText(Double.toString(eigKurs));
			etext[5].setText(Double.toString(eigKurs));
			break;
		default:
		}
	}
	
	private void setEnabled(EditText et) {
		et.setEnabled(true);
		et.setFocusableInTouchMode(true);
		et.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_VARIATION_NORMAL);
		et.setFocusable(true);
		et.setFocusable(true);
	}
	
	private void setDisabled(EditText et) {
		et.setEnabled(false);
		et.setFocusable(false);
		
	}
	
	public void enableTextField(EditText view) {
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		EditText et = (EditText) v;
		if (hasFocus) {
			et.setSelection(et.getText().length());
		} else {
			eingabewerte.putString(Integer.toString(v.getId()), et.getText()
					.toString());
		}
	}
	
	@Override
	public void onEigenerKurs(double kurs) {
		double anlKurs1, anlKurs2, sp1, sp2, rasp1, rasp2;
		// Aendert sich der eigene Kurs, aendert sich auch entsprechend der
		// anliegende Kurs und die Seitenpeilung
		try {
			anlKurs1 = Double.parseDouble(etAnlKurs1.getText().toString());
		} catch (NumberFormatException e) {
			anlKurs1 = 0d;
		}
		try {
			anlKurs2 = Double.parseDouble(etAnlKurs2.getText().toString());
		} catch (NumberFormatException e) {
			anlKurs2 = 0d;
		}
		
		this.oldEigKurs = eigKurs;
		this.eigKurs = kurs;
		double diffanl1 = oldEigKurs - anlKurs1;
		double diffanl2 = oldEigKurs - anlKurs2;
		double newAnlKurs1 = Lage.korrigiereWinkel(eigKurs - diffanl1);
		double newAnlKurs2 = Lage.korrigiereWinkel(eigKurs - diffanl2);
		
		if (anlKurs1 != newAnlKurs1) {
			etAnlKurs1.setText(((Double) newAnlKurs1).toString());
		}
		if (anlKurs2 != newAnlKurs2) {
			etAnlKurs2.setText(((Double) newAnlKurs2).toString());
		}
		try {
			rasp1 = Double.parseDouble(etRASP1.getText().toString());
		} catch (NumberFormatException e) {
			rasp1 = 0d;
		}
		try {
			rasp2 = Double.parseDouble(etRASP2.getText().toString());
		} catch (NumberFormatException e) {
			rasp2 = 0d;
		}
		try {
			sp1 = Double.parseDouble(etSP1.getText().toString());
		} catch (NumberFormatException e) {
			sp1 = 0d;
		}
		try {
			sp2 = Double.parseDouble(etSP2.getText().toString());
		} catch (NumberFormatException e) {
			sp2 = 0d;
		}
		double newsp1 = Lage.korrigiereWinkel(rasp1 - newAnlKurs1);
		double newsp2 = Lage.korrigiereWinkel(rasp2 - newAnlKurs2);
		if (sp1 != newsp1) {
			etSP1.setText(((Double) newsp1).toString());
		}
		if (sp2 != newsp2) {
			etSP2.setText(((Double) newsp2).toString());
		}
		double newrasp1 = Lage.korrigiereWinkel(sp1 + newAnlKurs1);
		double newrasp2 = Lage.korrigiereWinkel(sp2 + newAnlKurs2);
		if (rasp1 != newrasp1) {
			etRASP1.setText(((Double) newsp1).toString());
		}
		if (rasp2 != newrasp2) {
			etRASP2.setText(((Double) newsp2).toString());
		}
	}
	
	private class KursTextWatcher extends MaxInhaltTextWatcher {
		
		public KursTextWatcher(ErgebnisListener el, String fragmentId,
				EditText etext, int index) {
			super(el, fragmentId, etext, index);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			super.afterTextChanged(s);
			switch (resId) {
			case R.id.eSeitenpeilung1:
			case R.id.anliegenderKurs1:
				eingabeSPoAnlKurs(R.id.radioButtonSP1,
						R.id.eRadarSeitenpeilung1, R.id.eSeitenpeilung1,
						R.id.anliegenderKurs1);
				break;
			case R.id.eSeitenpeilung2:
			case R.id.anliegenderKurs2:
				eingabeSPoAnlKurs(R.id.radioButtonSP2,
						R.id.eRadarSeitenpeilung2, R.id.eSeitenpeilung2,
						R.id.anliegenderKurs2);
				break;
			case R.id.eRadarSeitenpeilung1:
				eingabeRASP(R.id.radioButtonRASP1, R.id.eRadarSeitenpeilung1,
						R.id.eSeitenpeilung1, R.id.anliegenderKurs1);
				break;
			case R.id.eRadarSeitenpeilung2:
				eingabeRASP(R.id.radioButtonRASP2, R.id.eRadarSeitenpeilung2,
						R.id.eSeitenpeilung2, R.id.anliegenderKurs2);
			}
		}
		
		public void eingabeSPoAnlKurs(int resIDRadioButton, int resIDRASP,
				int resIDSP, int resIDAnlKurs) {
			RadioButton rb = (RadioButton) view.findViewById(resIDRadioButton);
			if (rb.isChecked()) {
				EditText raspET = (EditText) view.findViewById(resIDRASP);
				EditText spET = (EditText) view.findViewById(resIDSP);
				EditText anlKursET = (EditText) view.findViewById(resIDAnlKurs);
				double sp, rasp, anlkurs;
				try {
					rasp = Double.parseDouble(raspET.getText().toString());
				} catch (NumberFormatException e) {
					rasp = 0d;
				}
				try {
					sp = Double.parseDouble(spET.getText().toString());
				} catch (NumberFormatException e) {
					sp = 0d;
				}
				try {
					anlkurs = Double
							.parseDouble(anlKursET.getText().toString());
				} catch (NumberFormatException e) {
					anlkurs = 0d;
				}
				double newrasp = Lage.korrigiereWinkel(sp + anlkurs);
				
				if (rasp != newrasp) {
					raspET.setText(((Double) newrasp).toString());
				}
			}
		}
		
		private void eingabeRASP(int resIDRadioButton, int resIDRASP,
				int resIDSP, int resIDAnlKurs) {
			RadioButton rb = (RadioButton) view.findViewById(resIDRadioButton);
			if (rb.isChecked()) {
				EditText raspET = (EditText) view.findViewById(resIDRASP);
				EditText spET = (EditText) view.findViewById(resIDSP);
				EditText anlKursET = (EditText) view.findViewById(resIDAnlKurs);
				double sp, rasp, anlkurs;
				try {
					rasp = Double.parseDouble(raspET.getText().toString());
				} catch (NumberFormatException e) {
					rasp = 0d;
				}
				try {
					sp = Double.parseDouble(spET.getText().toString());
				} catch (NumberFormatException e) {
					sp = 0d;
				}
				try {
					anlkurs = Double
							.parseDouble(anlKursET.getText().toString());
				} catch (NumberFormatException e) {
					anlkurs = 0d;
				}
				double newsp = Lage.korrigiereWinkel(rasp - anlkurs);
				if (sp != newsp) {
					spET.setText(((Double) newsp).toString());
				}
			}
		}
	}
}
