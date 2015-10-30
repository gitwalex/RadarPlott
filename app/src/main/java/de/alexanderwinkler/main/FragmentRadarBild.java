package de.alexanderwinkler.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import de.alexanderwinkler.R;
import de.alexanderwinkler.Math.Punkt2D;
import de.alexanderwinkler.Math.Vektor2D;
import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.interfaces.Konstanten;
import de.alexanderwinkler.interfaces.LageChangeListener;
import de.alexanderwinkler.interfaces.ManoeverServer;
import de.alexanderwinkler.main.ActivityRadarPlottErgebnis.ManoeverListener;
import de.alexanderwinkler.views.ViewLage;
import de.alexanderwinkler.views.ViewRadarBasisBild;
import de.alexanderwinkler.views.ViewRadarBasisBild.OnScaleChangeListener;

public class FragmentRadarBild extends Fragment implements Konstanten,
		OnSharedPreferenceChangeListener, OnScaleChangeListener,
		OnTouchListener, ManoeverListener {
	
	// View des Radarbildes
	private View view;
	private ViewLage vLageB, vLageC, vLageD;
	private ViewRadarBasisBild vRadarbild;
	private ManoeverServer mManoeverServer;
	private int rastergroesse = 3;
	private boolean northupOrientierung;
	private LageChangeListener mLageChangeListener;
	private int angle;
	private ArrayList<ViewLage> mViewLageArray;
	private boolean dual_pane;
	
	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onAttach(android.app.Activity)
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
			mLageChangeListener = (LageChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement LageChangeListener!");
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
		mManoeverServer.addManoeverListener(this);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		mManoeverServer.removeManoeverListener(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewLageArray = new ArrayList<ViewLage>();
		FrameLayout fl = (FrameLayout) view
				.findViewById(R.id.radarbildcontainer);
		Bundle lagebundle = mManoeverServer.getBundle();
		vRadarbild = new ViewRadarBasisBild(getActivity(), this);
		fl.addView(vRadarbild);
		dual_pane = lagebundle.getBoolean(KEYDUALPANE);
		if (dual_pane) {
			vRadarbild.setOnTouchListener(this);
		}
		Lage lage;
		lage = (Lage) lagebundle.getSerializable(KEYLAGE + KEYGEGNERB);
		if (lage != null) {
			vLageB = new ViewLage(getActivity(), KEYGEGNERB);
			mViewLageArray.add(vLageB);
			fl.addView(vLageB);
		}
		lage = (Lage) lagebundle.getSerializable(KEYLAGE +KEYGEGNERC);
		if (lage != null) {
			vLageC = new ViewLage(getActivity(), KEYGEGNERC);
			mViewLageArray.add(vLageC);
			fl.addView(vLageC);
		}
		lage = (Lage) lagebundle.getSerializable(KEYLAGE +KEYGEGNERD );
		if (lage != null) {
			vLageD = new ViewLage(getActivity(), KEYGEGNERD);
			mViewLageArray.add(vLageD);
			fl.addView(vLageD);
		}
		updateRastergroesse();
		updateNorthup();
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		if (sp != null) {
			sp.registerOnSharedPreferenceChangeListener(this);
			rastergroesse = Integer.valueOf(sp.getString(
					Konstanten.PREF_RADARKREISE, "3"));
			String orientierung = sp.getString(
					Konstanten.PREF_RADARORIENTIERUNG, "northup");
			northupOrientierung = false;
			if (orientierung.equals("northup")) {
				northupOrientierung = true;
			}
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
		view = inflater.inflate(R.layout.fragment_radarbild, container, false);
		return view;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// Alle Listener removen
		PreferenceManager.getDefaultSharedPreferences(getActivity())
				.unregisterOnSharedPreferenceChangeListener(this);
		vRadarbild.setOnTouchListener(null);
		mManoeverServer.removeManoeverListener(this);
	}
	
	public int getRastergroesse() {
		return rastergroesse;
	}
	
	public boolean getNorthupOrientierung() {
		return northupOrientierung;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(Konstanten.PREF_RADARKREISE)) {
			rastergroesse = Integer.valueOf(sharedPreferences.getString(
					Konstanten.PREF_RADARKREISE, "3"));
			updateRastergroesse();
		}
		// Orientierung hat sich geaendert
		if (key.equals(Konstanten.PREF_RADARORIENTIERUNG)) {
			String orientierung = sharedPreferences.getString(
					Konstanten.PREF_RADARORIENTIERUNG, "northup");
			if (orientierung.equals("northup")) {
				// Orientierung ist NorthUp, daher Kompass auf 0 Grad drehen
				northupOrientierung = true;
			} else {
				// Orientierung ist HeadUp, daher alle Bilder auf den
				// entsprechenende Kurs drehen
				northupOrientierung = false;
			}
			updateNorthup();
		}
		
	}
	
	public void updateNorthup() {
		for (ViewLage l : mViewLageArray) {
			l.setNorthUpOrientierung(northupOrientierung);
		}
		vRadarbild.setNorthUpOrientierung(northupOrientierung);
	}
	
	@Override
	public void onScaleChanged(float scale) {
		if (scale != 0) {
			for (ViewLage l : mViewLageArray) {
				l.setScale(scale);
			}
		}
	}
	
	public void updateRastergroesse() {
		for (ViewLage l : mViewLageArray) {
			l.setRastergroesse(rastergroesse);
		}
		vRadarbild.setRastergroesse(rastergroesse);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int oldangle = angle;
		boolean result = true;
		float x = event.getX() * 2 - vRadarbild.getWidth();
		float y = -(event.getY() * 2 - vRadarbild.getHeight());
		angle = (int) new Vektor2D(new Punkt2D(0, 0), new Punkt2D(x, y))
				.getWinkelRechtweisendNord();
		if (oldangle != angle) {
			if (event.getAction() == MotionEvent.ACTION_DOWN
					|| event.getAction() == MotionEvent.ACTION_MOVE) {
				mLageChangeListener.onLageChange(SENDEROnTouch, angle);
			}
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.main.ActivityRadarPlottErgebnis.ManoeverListener#
	 * onUpdateManoever(int, de.alexanderwinkler.berechnungen.Manoever)
	 */
	@Override
	public void onUpdateManoever(int senderId, Bundle lagebundle) {
		if (senderId != SENDERLageView) {
			for (ViewLage l : mViewLageArray) {
				l.updateManoever(lagebundle);
			}
		}
	}
	
}
