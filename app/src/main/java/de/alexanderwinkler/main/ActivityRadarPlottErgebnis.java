package de.alexanderwinkler.main;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost.TabSpec;
import de.alexanderwinkler.R;
import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.berechnungen.Manoever;
import de.alexanderwinkler.div.SimpleTabHost;
import de.alexanderwinkler.div.SimpleTabHost.TabCreator;
import de.alexanderwinkler.interfaces.FragmentIdServer;
import de.alexanderwinkler.interfaces.Konstanten;
import de.alexanderwinkler.interfaces.LageChangeListener;
import de.alexanderwinkler.interfaces.ManoeverServer;

public class ActivityRadarPlottErgebnis extends FragmentActivity implements
		Konstanten, TabCreator, ManoeverServer, LageChangeListener,
		FragmentIdServer {
	
	public interface FragmentChangeListener {
		public void onFragmentChange(String fragmentId);
	}
	
	public interface ManoeverListener {
		/**
		 * @author Alexander Winkler
		 * 
		 */
		public void onUpdateManoever(final int senderId, final Bundle lagebundle);
	}
	
	private ViewPager mPager1, mPager2, mPager3;
	private SimpleTabHost fth;
	// Values for SaveInstance
	/**
	 * 
	 */
	private boolean dual_pane = false;
	/**
	 * Bundle, welches zwischen den Activities/ Fragmenten ausgetauscht wird!
	 * 
	 */
	private Bundle lagebundle = new Bundle();
	/**
	 * 
	 */

	private double fahrt, manoeverminuten, manoeverkurs;
	private final int dual_pane_numberOfTabs = 3;
	private final int single_pane_numberOfTabs = 5;
	private int numberOfTabs = 3;
	private ArrayList<ManoeverListener> mManoeverListener;
	private ArrayList<FragmentChangeListener> mFragmentChangeListener;
	private String aktuellesFragmentId;
	private final String[] fragmentIdArray = { KEYGEGNERB, KEYGEGNERC,
			KEYGEGNERD };
	private ArrayList<Lage> lageListe = new ArrayList<Lage>();
	private ArrayList<Manoever> manoeverListe = new ArrayList<Manoever>();
	private Manoever manoever;
	private final int INIT = 0;
	
	/**
	 * Um die Fragmente wiederzufinden, muss der Tag ermittelt werden. Bei
	 * Konstruktion der Fragmente ueber den ViewPager werden die Fragmente mit
	 * Tags versehen:
	 * 
	 * @param viewId
	 *            ID des Fragments
	 * @param index
	 *            Index des Fragments im PageViewer
	 * @return Tag: Name des Fragments, wie er auch vom ViewPager generiert wird
	 *         "android:switcher:" + viewId + ":" + index
	 */
	static String makeFragmentName(int viewId, int index) {
		return "android:switcher:" + viewId + ":" + index;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	
	@Override
	public void onCreate(Bundle saveStateInstance) {
		super.onCreate(saveStateInstance);
		setContentView(R.layout.activity_ergebnis);
		if (saveStateInstance != null) {
			lagebundle = saveStateInstance.getBundle(KEYBUNDLE);
			numberOfTabs = saveStateInstance.getInt(KEYNUMBEROFTABS);
			manoeverkurs = lagebundle.getDouble(KEYMANOEVERKURSA);
			manoeverminuten = lagebundle.getDouble(KEYMANOEVERMINUTEN);
			dual_pane = lagebundle.getBoolean(KEYDUALPANE);
			
		} else {// saveStateInstance == null --> erster Start
			lagebundle = getIntent().getExtras().getBundle(KEYBUNDLE);
			// Aufbau des lagebundle
			manoeverkurs = lagebundle.getDouble(KEYMANOEVERKURSA);
			lagebundle.putDouble(KEYMANOEVERKURSA, manoeverkurs);
			lagebundle.putDouble(KEYMANOEVERMINUTEN, manoeverminuten);
			if (findViewById(R.id.dual_paneLayout) != null) {
				dual_pane = true;
				numberOfTabs = dual_pane_numberOfTabs;
			} else {
				numberOfTabs = single_pane_numberOfTabs;
			}
			lagebundle.putBoolean(KEYDUALPANE, dual_pane);
		} // End: saveStateInstance != null
		
		fth = (SimpleTabHost) getSupportFragmentManager().findFragmentById(
				R.id.extendedTabHost);
		Lage lage = null;
		for (String fragmentID : fragmentIdArray) {
			lage = (Lage) lagebundle.getSerializable(KEYLAGE + fragmentID);
			if (lage != null) {
				fahrt = lage.a.getGeschwindigkeit();
				lage.setTag(fragmentID);
				lageListe.add(lage);
			}
		}
		numberOfTabs = numberOfTabs - 3 + lageListe.size();
		fth.setNumberOfPages(numberOfTabs);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	
	@Override
	protected void onStart() {
		super.onStart();
		new FragmentRadarBild();
		onLageChange(INIT, manoeverkurs);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os
	 * .Bundle)
	 */
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBundle(KEYBUNDLE, lagebundle);
		outState.putInt(KEYNUMBEROFTABS, numberOfTabs);
		super.onSaveInstanceState(outState);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.radarmenu, menu);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.beschreibung:
			AlertDialog.Builder beschreibungbuilder = new AlertDialog.Builder(
					this);
			// Inhalt aufbauen
			CharSequence cs = Html.fromHtml((String) getResources().getText(
					R.string.usetext));
			beschreibungbuilder.setMessage(cs).setTitle(R.string.usetitel);
			
			// 3. Get the AlertDialog from create()
			AlertDialog beschreibungalertdialog = beschreibungbuilder.create();
			beschreibungalertdialog.show();
			return true;
		case R.id.about:
			// 1. Instantiate an AlertDialog.Builder with its constructor
			AlertDialog.Builder aboutbuilder = new AlertDialog.Builder(this);
			// Inhalt aufbauen
			CharSequence csabout = Html.fromHtml((String) getResources()
					.getText(R.string.abouttext));
			aboutbuilder.setMessage(csabout).setTitle(R.string.about);
			// 3. Get the AlertDialog from create()
			AlertDialog aboutalertdialog = aboutbuilder.create();
			aboutalertdialog.show();
			return true;
		case R.id.settings:
			Intent intent = new Intent(this, ActivitySettings.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	/**
	 * @param picker
	 * @param oldVal
	 * @param newVal
	 */
	
	@Override
	public void onLageChange(int senderId, double newVal) {
		String tag;
		manoeverListe.clear();
		switch (senderId) {
		case R.id.npmanoeverFahrt:
			fahrt = newVal;
			break;
		case R.id.npmanoeverZeit:
		case R.id.npmanoeverAbstand:
			manoeverminuten = newVal;
			lagebundle.putDouble(KEYMANOEVERMINUTEN, manoeverminuten);
			break;
		case R.id.npmanoeverKurs:
		case R.id.npmanoeverCPA:
		case SENDEROnTouch:
			manoeverkurs = newVal;
			lagebundle.putDouble(KEYMANOEVERKURSA, manoeverkurs);
		case INIT:
			break;
		default:
			return;
		}
		for (Lage lage : lageListe) {
			manoever = new Manoever(lage, manoeverkurs, manoeverminuten, fahrt);
			tag = lage.getTag();
			manoever.setTag(tag);
			lagebundle.putSerializable(KEYMANOEVER + tag, manoever);
			manoeverListe.add(manoever);
		}
		if (mManoeverListener != null) {
			// Alle Manoever verschicken
			for (ManoeverListener b : mManoeverListener) {
				b.onUpdateManoever(senderId, lagebundle);
			}
		}
	}
	
	@Override
	public Bundle getBundle() {
		return lagebundle;
	}
	
	@Override
	public TabSpec getNewTab(SimpleTabHost mTabHost, int tabNumber) {
		switch (tabNumber) {
		case 1:
			return mTabHost.newTab(KEYGEGNERB, R.string.tabGegnerB, R.id.tab1);
		case 2:
			return mTabHost.newTab(KEYGEGNERC, R.string.tabGegnerC, R.id.tab2);
		case 3:
			return mTabHost.newTab(KEYGEGNERD, R.string.tabGegnerD, R.id.tab3);
		case 4:
			return mTabHost.newTab(SimpleTabHost.TAB4, R.string.tabRadarBild,
					R.id.tab4);
		case 5:
			return mTabHost.newTab(SimpleTabHost.TAB5, R.string.tabSpinner,
					R.id.tab5);
		}
		return null;
	}
	
	@Override
	public void updateTab(View view, String tabTag, int placeholder) {
		MyPageAdapterMulti mMultiAdapter;
		FragmentManager fm;
		switch (placeholder) {
		case R.id.tab1:
			if (mPager1 == null) {
				mPager1 = (ViewPager) view.findViewById(R.id.pagertab1);
				// Alle Seiten des Pagers im Speicher halten.
				mPager1.setOffscreenPageLimit(2);
				mMultiAdapter = new MyPageAdapterMulti(
						getSupportFragmentManager(), KEYGEGNERB);
				mPager1.setAdapter(mMultiAdapter);
			}
			aktuellesFragmentId = KEYGEGNERB;
			break;
		case R.id.tab2:
			if (mPager2 == null) {
				mPager2 = (ViewPager) view.findViewById(R.id.pagertab2);
				// Alle Seiten des Pagers im Speicher halten.
				mPager2.setOffscreenPageLimit(2);
				mMultiAdapter = new MyPageAdapterMulti(
						getSupportFragmentManager(), KEYGEGNERC);
				mPager2.setAdapter(mMultiAdapter);
			}
			aktuellesFragmentId = KEYGEGNERC;
			break;
		case R.id.tab3:
			if (mPager3 == null) {
				mPager3 = (ViewPager) view.findViewById(R.id.pagertab3);
				// Alle Seiten des Pagers im Speicher halten.
				mPager3.setOffscreenPageLimit(2);
				mMultiAdapter = new MyPageAdapterMulti(
						getSupportFragmentManager(), KEYGEGNERD);
				mPager3.setAdapter(mMultiAdapter);
			}
			aktuellesFragmentId = KEYGEGNERD;
			break;
		case R.id.tab4:
			fm = getSupportFragmentManager();
			if (fm.findFragmentByTag(tabTag) == null) {
				FragmentRadarBild rb = new FragmentRadarBild();
				fm.beginTransaction().replace(placeholder, rb, tabTag).commit();
			}
			break;
		case R.id.tab5:
			fm = getSupportFragmentManager();
			if (fm.findFragmentByTag(tabTag) == null) {
				FragmentErgebnisAuswahlButtons ab = new FragmentErgebnisAuswahlButtons();
				fm.beginTransaction().replace(placeholder, ab, tabTag).commit();
			}
			break;
		}
		switch (placeholder) {
		case R.id.tab1:
		case R.id.tab2:
		case R.id.tab3:
			updateFragmentListener();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.div.SimpleTabHost.TabCreator#onTabSelected(java.lang
	 * .String)
	 */
	@Override
	public void onTabSelected(String tabId) {
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.interfaces.ManoeverServer#addManoeverListener(de.
	 * alexanderwinkler.interfaces.ManoeverListener)
	 */
	@Override
	public boolean addManoeverListener(ManoeverListener b) {
		if (mManoeverListener == null) {
			mManoeverListener = new ArrayList<ManoeverListener>();
		}
		return mManoeverListener.add(b);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.interfaces.ManoeverServer#removeManoeverListener(
	 * de.alexanderwinkler.interfaces.ManoeverListener)
	 */
	@Override
	public boolean removeManoeverListener(ManoeverListener b) {
		return mManoeverListener.remove(b);
	}
	
	public void updateFragmentListener() {
		if (aktuellesFragmentId != null & mFragmentChangeListener != null) {
			for (FragmentChangeListener b : mFragmentChangeListener) {
				b.onFragmentChange(aktuellesFragmentId);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.interfaces.FragmentIdServer#addFragmentChangeListener
	 * (
	 * de.alexanderwinkler.main.ActivityRadarPlottErgebnis.FragmentChangeListener
	 * )
	 */
	@Override
	public boolean addFragmentChangeListener(
			FragmentChangeListener fragmentChangeListener) {
		if (mFragmentChangeListener == null) {
			mFragmentChangeListener = new ArrayList<FragmentChangeListener>();
		}
		return mFragmentChangeListener.add(fragmentChangeListener);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.interfaces.FragmentIdServer#removeFragmentChangeListener
	 * (
	 * de.alexanderwinkler.main.ActivityRadarPlottErgebnis.FragmentChangeListener
	 * )
	 */
	@Override
	public boolean removeFragmentChangeListener(
			FragmentChangeListener fragmentChangeListener) {
		return mFragmentChangeListener.remove(fragmentChangeListener);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.alexanderwinkler.interfaces.FragmentIdServer#getFragmentIdAktuell()
	 */
	@Override
	public String getFragmentIdAktuell() {
		return aktuellesFragmentId;
	}
	
	private class MyPageAdapterMulti extends FragmentPagerAdapter {
		private final int NUM_PAGES;
		private String fragmentId;
		{
			NUM_PAGES = dual_pane_numberOfTabs;
			
		}
		
		public MyPageAdapterMulti(FragmentManager fm, String fragmentId) {
			super(fm);
			this.fragmentId = fragmentId;
		}
		
		@Override
		public Fragment getItem(int position) {
			Bundle args = new Bundle();
			switch (position) {
			case 0:
				FragmentErgebnisEingabeTexte fte = new FragmentErgebnisEingabeTexte();
				args.putString(KEYFRAGMENTID, fragmentId);
				fte.setArguments(args);
				return fte;
			case 1:
				FragmentErgebnisTexteLage ftl = new FragmentErgebnisTexteLage();
				args.putString(KEYFRAGMENTID, fragmentId);
				ftl.setArguments(args);
				return ftl;
			case 2:
				FragmentErgebnisTexteManoever ftm = new FragmentErgebnisTexteManoever();
				args.putString(KEYFRAGMENTID, fragmentId);
				ftm.setArguments(args);
				return ftm;
			default:
				
				return null;
			}
		}
		
		@Override
		public int getCount() {
			return NUM_PAGES;
		}
		
	}
}
