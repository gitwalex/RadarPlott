package de.alexanderwinkler.main;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import de.alexanderwinkler.R;
import de.alexanderwinkler.Math.Punkt2D;
import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.berechnungen.Manoever;
import de.alexanderwinkler.interfaces.FragmentIdServer;
import de.alexanderwinkler.interfaces.Konstanten;
import de.alexanderwinkler.interfaces.LageChangeListener;
import de.alexanderwinkler.interfaces.ManoeverServer;
import de.alexanderwinkler.main.ActivityRadarPlottErgebnis.FragmentChangeListener;
import de.alexanderwinkler.main.ActivityRadarPlottErgebnis.ManoeverListener;
import de.alexanderwinkler.main.FragmentNumberPicker.ErgebnisListener;

/**
 * Fragment, um die Auswahlmoeglichkeiten darzustellen. Auswahlmoeglichkeiten
 * sind: Gegner sowie Kurs-/Fahrtaenderung A
 * 
 * @author Alexander Winkler
 * 
 */
public class FragmentErgebnisAuswahlButtons extends Fragment implements
		Konstanten, ErgebnisListener, FragmentChangeListener, ManoeverListener,
		OnClickListener {
	private LageChangeListener mLageChangeListener;
	private ManoeverServer mManoeverServer;
	private LinkedHashMap<Integer, FragmentNumberPicker> npNPMap = new LinkedHashMap<Integer, FragmentNumberPicker>();
	private FragmentIdServer mFragmentIdServer;
	private Lage lage;
	private Bundle lagebundle;
	private String fragmentId;
	private TextView tvManoeverText;
	private boolean isKurs;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mLageChangeListener = (LageChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement LageChangeListener!");
		}
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
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FragmentNumberPicker np;
		int[] npIds = { R.id.npmanoeverAbstand, R.id.npmanoeverCPA,
				R.id.npmanoeverKurs, R.id.npmanoeverZeit, R.id.npmanoeverFahrt };
		View view = inflater.inflate(
				R.layout.fragment_ergebnis_auswahl_buttons, container, false);
		for (Integer id : npIds) {
			
			np = (FragmentNumberPicker) getFragmentManager().findFragmentById(
					id);
			npNPMap.put(id, np);
			np.setErgebnisHandler(this);
			switch (id) {
			case R.id.npmanoeverCPA:
				np.toggleActive();
				break;
			case R.id.npmanoeverKurs:
				break;
			case R.id.npmanoeverAbstand:
				break;
			case R.id.npmanoeverZeit:
				np.toggleActive();
				break;
			case R.id.npmanoeverFahrt:
				View v = np.getView().findViewById(R.id.npmanoeverFahrt);
				v.setVisibility(View.GONE);
			}
		}
		tvManoeverText = (TextView) view.findViewById(R.id.manoeverText);
		tvManoeverText.setOnClickListener(this);
		return view;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lagebundle = mManoeverServer.getBundle();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		mFragmentIdServer.addFragmentChangeListener(this);
		mManoeverServer.addManoeverListener(this);
		String fragmentId = mFragmentIdServer.getFragmentIdAktuell();
		onFragmentChange(fragmentId);
		lage = (Lage) lagebundle.getSerializable(KEYLAGE + fragmentId);
		onUpdateManoever(0, lagebundle);
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
		mManoeverServer.removeManoeverListener(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.main.FragmentNumberPicker.MyOnTouchListener#onTouch
	 * (de.alexanderwinkler.main.FragmentNumberPicker, android.view.MotionEvent)
	 */
	@Override
	public void onTouch(FragmentNumberPicker np, MotionEvent event) {
		if (!np.isActive()) {
			int id = np.getId();
			FragmentNumberPicker mnp;
			switch (id) {
			case R.id.npmanoeverCPA:
			case R.id.npmanoeverKurs:
				mnp = npNPMap.get(R.id.npmanoeverKurs);
				mnp.toggleActive();
				mnp = npNPMap.get(R.id.npmanoeverCPA);
				mnp.toggleActive();
				break;
			case R.id.npmanoeverAbstand:
			case R.id.npmanoeverZeit:
				mnp = npNPMap.get(R.id.npmanoeverAbstand);
				mnp.toggleActive();
				mnp = npNPMap.get(R.id.npmanoeverZeit);
				mnp.toggleActive();
				break;
			default:
			}
		}
	}
	
	@Override
	public boolean onMessage(int what, FragmentNumberPicker.NPValues npvalues) {
		switch (what) {
		case FragmentNumberPicker.SETMANOEVER:
			double newValue = npvalues.value;
			int id = npvalues.id;
			switch (id) {
			case R.id.npmanoeverAbstand:
				try {
					Punkt2D p = lage.b2
							.getPunktinFahrtrichtungmitAbstand(newValue);
					newValue = lage.b2.getDauer(p);
				} catch (IllegalArgumentException ex) {
					Log.d(LOGTAG,
							"Der aktuell ausgewaehlte Wert fuer den Abstand "
									+ newValue + " wird ignoriert.");
					ex.printStackTrace();
				}
				break;
			case R.id.npmanoeverZeit:
				break;
			case R.id.npmanoeverCPA:
				newValue = lage.newCPA(newValue);
				break;
			case R.id.npmanoeverKurs:
				break;
			case R.id.npmanoeverFahrt:
				break;
			default:
			}
			mLageChangeListener.onLageChange(id, newValue);
			return true;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.main.ActivityRadarPlottErgebnis.ManoeverListener#
	 * onUpdateManoever(int, android.os.Bundle,
	 * de.alexanderwinkler.berechnungen.Manoever)
	 */
	@Override
	public void onUpdateManoever(int senderId, Bundle lagebundle) {
		Manoever manoever = (Manoever) lagebundle.getSerializable(KEYMANOEVER
				+ fragmentId);
		for (Entry<Integer, FragmentNumberPicker> entry : npNPMap.entrySet()) {
			FragmentNumberPicker.NPValues npvalues = new FragmentNumberPicker.NPValues();
			int key = entry.getKey();
			FragmentNumberPicker np = entry.getValue();
			switch (key) {
			case R.id.npmanoeverCPA:
				npvalues.value = Math.abs(manoever.getCPA());
				doUpdateManoeverMessage(senderId, np, npvalues);
				break;
			case R.id.npmanoeverAbstand:
				npvalues.value = Math.abs(manoever.a.getAp().abstand(
						manoever.b2.getAp()));
				doUpdateManoeverMessage(senderId, np, npvalues);
				break;
			case R.id.npmanoeverKurs:
				npvalues.value = manoever.a.getWinkelRW();
				doUpdateManoeverMessage(senderId, np, npvalues);
				break;
			case R.id.npmanoeverZeit:
				break;
			case R.id.npmanoeverFahrt:
				break;
			}
		}
	}
	
	private void doUpdateManoeverMessage(int senderId, FragmentNumberPicker np,
			FragmentNumberPicker.NPValues npvalues) {
		np.onMessage(senderId, FragmentNumberPicker.SETMANOEVER, npvalues);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.main.ActivityRadarPlottErgebnis.FragmentChangeListener
	 * #onFragmentChange(java.lang.String)
	 */
	@Override
	public void onFragmentChange(String fragmentId) {
		this.fragmentId = fragmentId;
		lage = (Lage) lagebundle.getSerializable(KEYLAGE + fragmentId);
		for (Entry<Integer, FragmentNumberPicker> entry : npNPMap.entrySet()) {
			FragmentNumberPicker.NPValues npvalues = new FragmentNumberPicker.NPValues();
			int key = entry.getKey();
			FragmentNumberPicker np = entry.getValue();
			switch (key) {
			case R.id.npmanoeverCPA:
			case R.id.npmanoeverAbstand:
				npvalues.minValue = Math.abs(lage.getCPA());
				npvalues.maxValue = Math.abs(lage.a.getAp().abstand(
						lage.b2.getAp()));
				break;
			case R.id.npmanoeverKurs:
				npvalues.minValue = 0d;
				npvalues.maxValue = 359.0;
				break;
			case R.id.npmanoeverZeit:
				npvalues.minValue = 0d;
				npvalues.maxValue = 30d;
				break;
			case R.id.npmanoeverFahrt:
				npvalues.maxValue = 10;
				npvalues.minValue = 0;
				break;
			}
			
			if (npvalues.maxValue < npvalues.minValue) {
				double temp = npvalues.minValue;
				npvalues.minValue = npvalues.maxValue;
				npvalues.maxValue = temp;
			}
			np.onMessage(FragmentNumberPicker.INIT,
					FragmentNumberPicker.SETVALUES, npvalues);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.manoeverText:
			if (isKurs) {
				getFragmentManager().findFragmentById(R.id.npmanoeverKurs)
						.getView().setVisibility(View.VISIBLE);
				getFragmentManager().findFragmentById(R.id.npmanoeverFahrt)
						.getView().setVisibility(View.GONE);
			} else {
				getFragmentManager().findFragmentById(R.id.npmanoeverKurs)
						.getView().setVisibility(View.GONE);
				getFragmentManager().findFragmentById(R.id.npmanoeverFahrt)
						.getView().setVisibility(View.VISIBLE);
			}
			isKurs = !isKurs;
		}
	}
}