package de.alexanderwinkler.main;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import de.alexanderwinkler.R;
import de.alexanderwinkler.interfaces.Konstanten;

public class FragmentPreferences extends PreferenceFragment implements
		Konstanten {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences,
				false);
	}
}
