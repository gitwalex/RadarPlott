package de.aw.radarplott.div;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import de.aw.radarplott.R;

public class MaxInhaltTextWatcher implements TextWatcher {
	
	protected int index, resId;
	protected EditText editText;
	private String fragmentId;
	private ErgebnisListener mErgebnisListener;
	// Array korrespondierend zu edittextID. Je Wert in edittextID wird der
	// maximal einzugebene Wert angegeben. Wird in TextWatcher geprueft.
	private double[] maxeditvalue = { 360d, 100d, 100d, 360d, 360d, 360d, 100d,
			360d, 360d, 360d, 100d };
	private double maxinhalt;

	/**
	 * Konstruktor fuer TextWatcher. Muss als einzelne Instanz an ein EditText-Feld gehaengt
	 * werden.
	 *
	 * @param editText
	 *         EditText-Feld, welches inhaltlich ueberprueft wird
	 * @param index
	 *         Index in maxeditvalue. Wird auch benutzt, um das Flag dero ordentlichen Eingabe zu
	 *         setzen.
	 */
	public MaxInhaltTextWatcher(ErgebnisListener el, String fragmentId, EditText editText,
								int index) {
		// Parameter merken
		mErgebnisListener = el;
		this.fragmentId = fragmentId;
		this.editText = editText;
		this.resId = editText.getId();
		this.index = index;
		// maximalen Wert aus den Klassenvariablen maxeditvalue anhand des
		// index holen
		this.maxinhalt = maxeditvalue[index];
	}

	/**
	 * Prueft den Eingabewert. Ist der Wert nicht ok, wird ein Fehlertext
	 * ausgegeben und die Textfarbe rot. Ist der Wert ok, wird in eingabenOK das
	 * entsprechende Flag gesetzt, ausserdem der Wert in der map mit der R.id.
	 * des EditText als Schluessel gespeichert
	 *
	 * (non-Javadoc)
	 *
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	@Override
	public void afterTextChanged(Editable s) {
		double wert = 0;
		String result = editText.getText().toString();
		try {
			wert = Double.valueOf(result);
		} catch (NumberFormatException n) {
			// Kein Double Wert. Feld hat ungueltigen Inhalt (z.B.
			// leer).
			// falls vorher ein gueltiger Wert gesetzt war, ist das Flag
			// in den Eingaben als gueltig gesetzt. Zuruecksetzen. Loeschen
			// der Eingabe beim ErgebnisListener
			mErgebnisListener.setWertNOK(fragmentId, index);
			// und Abbruch
			return;
		}
		if (wert > maxinhalt) {
			// eingebenener Wert ist zu gross oder ungueltig. Fehler
			// setzen. Fehlertext wird in den String-Ressourcen definiert.
			mErgebnisListener.setError(editText, maxinhalt);
			// falls vorher ein gueltiger Wert gesetzt war, ist das Flag
			// in den Eingaben als gueltig gesetzt. Zuruecksetzen.
			mErgebnisListener.setWertNOK(fragmentId, index);
			// und Abbruch
			return;
		} else {
			mErgebnisListener.setWertOK(fragmentId, index, wert);
			if (resId == R.id.eEigKurs) {
				mErgebnisListener.onEigenerKursChanged(wert);
			}
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// Nichts
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Nichts
	}

	public interface ErgebnisListener {
		void onEigenerKursChanged(double wert);

		void setError(EditText et, double maxinhalt);

		void setWertNOK(String fragmentId, int index);

		void setWertOK(String fragmentId, int index, double value);
	}
}
