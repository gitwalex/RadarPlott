package de.aw.radarplott.interfaces;

import android.graphics.Color;

import de.aw.radarplott.R;

public interface Konstanten {
	
	/**
	 * 
	 */
	String Version = "2.0";
	/**
	 * 
	 */
	String ERGEBNISACTIVITY = "RADAR_PLOTT_ERGEBNIS";
	/**
	 * ID's der Eingabefelder. Werden zum Initialisieren und pruefen benutzt.
	 */
	int[] eingabewerteGesamtIDs = {R.id.eEigKurs, R.id.eEigFahrt,
			R.id.eIntervall, R.id.eSeitenpeilung1, R.id.anliegenderKurs1,
			R.id.eRadarSeitenpeilung1, R.id.eDistanz1, R.id.eSeitenpeilung2,
			R.id.anliegenderKurs2, R.id.eRadarSeitenpeilung2, R.id.eDistanz2 };
	/**
	 * ID's der Eingabefelder eigenes Schiff. Werden zum Initialisieren und
	 * pruefen benutzt.
	 */
	int[] eingabewerteEigSchifIDs = {R.id.eEigKurs, R.id.eEigFahrt,
			R.id.eIntervall };
	/**
	 * ID's der Eingabefelder Gegener. Werden zum Initialisieren und pruefen
	 * benutzt.
	 */
	int[] eingabewerteGegnerIDs = {R.id.eSeitenpeilung1,
			R.id.anliegenderKurs1, R.id.eRadarSeitenpeilung1, R.id.eDistanz1,
			R.id.eSeitenpeilung2, R.id.anliegenderKurs2,
			R.id.eRadarSeitenpeilung2, R.id.eDistanz2 };
	/**
	 * Logtag.
	 */
	String LOGTAG = "de.alexanderwinkler";
	/**
	 * DebugMode einschalten? Derzeit wirkunglos...
	 */
	boolean debugMode = true;
	
	// Konstanten zum Zeichnen
	/**
	 * 
	 */
	int zeichnelagefett = 5;
	/**
	 * 
	 */
	int zeichnelagenormal = 1;
	/**
	 * 
	 */
	int colorA = Color.GREEN;
	/**
	 * 
	 */
	int colorB1 = Color.BLUE;
	/**
	 * 
	 */
	int colorB2 = Color.RED;
	/**
	 * 
	 */
	int colorCPA = R.color.Brown;
	/**
	 * 
	 */
	int colorRadarLinien = Color.LTGRAY;
	/**
	 * 
	 */
	int colorRadarBackground = Color.WHITE;
	
	// Konstanten fuer Preferencs
	/**
	 * 
	 */
	String PREF_RADARKREISE = "pref_1";
	/**
	 * 
	 */
	String PREF_RADARORIENTIERUNG = "pref_2";
	
	// Konstanten fuer Tabs
	/**
	 * 
	 */
	String KEYGEGNERB = TabHostKonstanten.TAB1;
	/**
	 * 
	 */
	String KEYGEGNERC = TabHostKonstanten.TAB2;
	/**
	 * 
	 */
	String KEYGEGNERD = TabHostKonstanten.TAB3;
	
	// Konstanten fur Zugriff auf Bundle
	/**
	 * 
	 */
	String KEYLAGE = "KEYLAGE";
	/**
	 * 
	 */
	String KEYBUNDLE = "LAGEBUNDLE";
	/**
	 * 
	 */
	String KEYMANOEVER = "MANOEVER";
	/**
	 * Key fuer FragmentId der Fragmente, die mehrmals benutzt werden
	 */
	String KEYFRAGMENTID = "frag_Id";
	/**
	 * Key fuer FragmentId des TabHost fuer die Eingabe von Daten
	 */
	String KEYTABEINGABEFRAGMENTID = "frag_TabHost";
	
	/**
	 * Key fuer FragmentId des Fragments zur Eingabe der Daten des eigenen
	 * Schiffes.
	 * 
	 */
	String KEYFRAGMENTEIGSCHIFF = "frag_eigSchiff";
	
	// Konstanten fuer Sender Manoever
	/**
	 * 
	 */
	int SENDEROnTouch = -1;
	/**
	 * 
	 */
	int SENDERLageView = 1;
	
	// Sonstige Konstanten
	/**
	 * 
	 */
	String KEYEINGABEOK = "1";
	/**
	 * 
	 */
	String KEYMANOEVERMINUTEN = "2";
	/**
	 * 
	 */
	String KEYMANOEVERKURSA = "3";
	/**
	 * 
	 */
	String KEYMANOEVERCPA = "4";
	/**
	 * 
	 */
	String KEYDUALPANE = "5";
	/**
	 * 
	 */
	String KEYEINGABE = "6";
	/**
	 * 
	 */
	String KEYISINITIALIZED = "7";
	
	/**
	 * Key fuer retten Anzahl Tabs.
	 */
	String KEYNUMBEROFTABS = "numberOfTabs";
}
