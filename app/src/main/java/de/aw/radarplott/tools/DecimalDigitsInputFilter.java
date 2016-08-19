package de.aw.radarplott.tools;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Klasse kommentieren
public class DecimalDigitsInputFilter implements InputFilter {
	
	Pattern mPattern = Pattern
			.compile("([1-9]{1}[0-9]{0,2}([0-9]{3})*(\\.[0-9]{0,2})?|[1-9]{1}[0-9]{0,}(\\.[0-9]{0,2})?|0(\\.[0-9]{0,2})?|(\\.[0-9]{1,2})?)");
	
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		String formatedSource = source.subSequence(start, end).toString();
		String destPrefix = dest.subSequence(0, dstart).toString();
		String destSuffix = dest.subSequence(dend, dest.length()).toString();
		String result = destPrefix + formatedSource + destSuffix;
		result = result.replace(",", ".");
		Matcher matcher = mPattern.matcher(result);
		
		if (matcher.matches()) {
			// null: Alles ok, passt
			return null;
		}
		// Passt nicht: Wert verwerfen
		return "";
	}
}
