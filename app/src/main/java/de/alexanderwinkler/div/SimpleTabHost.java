package de.alexanderwinkler.div;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import de.alexanderwinkler.R;
import de.alexanderwinkler.interfaces.Konstanten;

/**
 * <pre class="prettyprint">
 * *
 * 	FragmentManager fm = getFragmentManager();
 * 	if (fm.findFragmentByTag(tabId) == null) {
 * 		Bundle args = new Bundle();
 * 		// Setzen der FragmentId fuer die Eingabe der Gegner-Werte. Ist
 * 		// identisch mit der jeweiligen tabID
 * 		args.putString(KEYFRAGMENTID, tabId);
 * 		FragmentEingabeGegnerWerte fe = new FragmentEingabeGegnerWerte();
 * 		fe.setArguments(args);
 * 		fm.beginTransaction().replace(placeholder, fe, tabId).commit();
 * 	}
 * }
 * 		case R.id.pagertab2:
 * 			if (mPager2 == null) {
 * 				mPager2 = (ViewPager) getView().findViewById(R.id.pagertab2);
 * 				// Alle Seiten des Pagers im Speicher halten.
 * 				mPager2.setOffscreenPageLimit(2);
 * 				mMultiAdapter = new MyPageAdapterMulti(getActivity()
 * 						.getSupportFragmentManager(), KEYGEGNERC);
 * 				mPager2.setAdapter(mMultiAdapter);
 * 			}
 * </pre>
 * 
 * @author Alexander Winkler
 * 
 */
public class SimpleTabHost extends Fragment implements OnTabChangeListener,
		Konstanten {
	/**
	 * @author Alexander Winkler
	 * 
	 */
	public interface TabCreator {
		public TabSpec getNewTab(SimpleTabHost mTabHost, int tabNumber);
		
		public void updateTab(View view, String tabId, int placeholder);
		
		public void onTabSelected(String tabId);
	}
	
	/**
	 * Anzahl der im TabHost anzuzeigenden Tabs. Bis zu 10 Tabs kuennen angezeigt
	 * werden. Alle nicht benoetigten Tabs werden removed.
	 */
	private int numberOfPages;
	
	/**
	 * Maximale Anzahl der Tabs.
	 */
	private final int maxTabs = 10;
	/**
	 * Tags der Tabs im TabHost.
	 * 
	 * @see #maxTabs
	 */
	public static final String TAB1 = "TAB1", TAB2 = "TAB2", TAB3 = "TAB3",
			TAB4 = "TAB4", TAB5 = "TAB5", TAB6 = "TAB6", TAB7 = "TAB7",
			TAB8 = "TAB8", TAB9 = "TAB9", TAB10 = "TAB10";
	private View mRoot;
	/**
	 * 
	 */
	private TabHost mTabHost;
	/**
	 * 
	 */
	private int mCurrentTab;
	
	/**
	 * 
	 */
	private int tabId;
	
	private boolean setNumberOfPagesCalled;
	
	private TabCreator mTabCreator;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mTabCreator = (TabCreator) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TabCreator!");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fragment_eingabe_tabhost, null);
		mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
		return mRoot;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(KEYTABEINGABEFRAGMENTID, tabId);
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * @return
	 */
	public TabHost getTabHost() {
		return mTabHost;
	}
	
	/**
	 * @return
	 */
	public int getTabsFragmentId() {
		return tabId;
	}
	
	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
		if (numberOfPages > maxTabs) {
			throw new IllegalArgumentException(getActivity().toString()
					+ " darf nicht mehr als " + maxTabs + " erwarten!.");
		}
		setNumberOfPagesCalled = true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!setNumberOfPagesCalled) {
			throw new IllegalArgumentException(getActivity().toString()
					+ " hat nicht setNumberOfPages(numberOfPages) gerufen!");
		}
		mTabHost.setup(); // you must call this before adding your tabs!
		setupTabs();
		// Entfernen der nicht benoetigten Tabs
		for (int i = numberOfPages + 1; i < maxTabs; i++) {
			removeTab(i);
		}
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(mCurrentTab);
		// manually start loading stuff in the first tab
		updateTab(TAB1, R.id.tab1);
	}
	
	/**
	 * 
	 */
	private void setupTabs() {
		for (int i = 0; i < numberOfPages; i++) {
			mTabHost.addTab(mTabCreator.getNewTab(this, i + 1));
		}
	}
	
	/**
	 * @param tag
	 * @param labelId
	 * @param tabContentId
	 * @return
	 */
	public TabSpec newTab(String tag, int labelId, int tabContentId) {
		Log.d(LOGTAG, "buildTab(): tag=" + tag);
		TabSpec tabSpec = mTabHost.newTabSpec(tag);
		CharSequence cs = getResources().getText(labelId);
		tabSpec.setIndicator(cs);
		tabSpec.setContent(tabContentId);
		return tabSpec;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	@Override
	public void onTabChanged(String tabId) {
		Log.d(Konstanten.LOGTAG, "onTabChanged(): tabId=" + tabId);
		if (TAB1.equals(tabId)) {
			updateTab(tabId, R.id.tab1);
			mCurrentTab = 0;
			return;
		}
		if (TAB2.equals(tabId)) {
			updateTab(tabId, R.id.tab2);
			mCurrentTab = 1;
			return;
		}
		if (TAB3.equals(tabId)) {
			updateTab(tabId, R.id.tab3);
			mCurrentTab = 2;
			return;
		}
		if (TAB4.equals(tabId)) {
			updateTab(tabId, R.id.tab4);
			mCurrentTab = 3;
			return;
		}
		if (TAB5.equals(tabId)) {
			updateTab(tabId, R.id.tab5);
			mCurrentTab = 4;
			return;
		}
		if (TAB6.equals(tabId)) {
			updateTab(tabId, R.id.tab6);
			mCurrentTab = 5;
			return;
		}
		if (TAB7.equals(tabId)) {
			updateTab(tabId, R.id.tab7);
			mCurrentTab = 6;
			return;
		}
		if (TAB8.equals(tabId)) {
			updateTab(tabId, R.id.tab8);
			mCurrentTab = 7;
			return;
		}
		if (TAB9.equals(tabId)) {
			updateTab(tabId, R.id.tab9);
			mCurrentTab = 8;
			return;
		}
		if (TAB10.equals(tabId)) {
			updateTab(tabId, R.id.tab10);
			mCurrentTab = 9;
			return;
		}
		mTabCreator.onTabSelected(tabId);
	}
	
	/**
	 * @param tabTag
	 * @param placeholder
	 */
	private void updateTab(String tabTag, int placeholder) {
		mTabCreator.updateTab(getView(), tabTag, placeholder);
	}
	
	public void removeTab(int tabIndex) {
		mTabHost.getTabWidget().removeView(
				mTabHost.getTabWidget().getChildTabViewAt(tabIndex));
	}
}
