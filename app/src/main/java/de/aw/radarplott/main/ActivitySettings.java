package de.aw.radarplott.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import de.aw.radarplott.interfaces.Konstanten;

public class ActivitySettings extends Activity implements Konstanten {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new FragmentPreferences())
				.commit();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
