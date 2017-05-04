package de.aw.radarplott.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import java.util.ArrayList;

import de.aw.radarplott.R;
import de.aw.radarplott.berechnungen.Lage;
import de.aw.radarplott.div.MaxInhaltTextWatcher.ErgebnisListener;
import de.aw.radarplott.div.SimpleTabHost;
import de.aw.radarplott.div.SimpleTabHost.TabCreator;
import de.aw.radarplott.interfaces.BundleServer;
import de.aw.radarplott.interfaces.Konstanten;
import de.aw.radarplott.interfaces.KursWerteListener;

/**
 * @author Alexander Winkler
 */
public class ActivityRadarPlottEingabe extends FragmentActivity implements
        Konstanten, ErgebnisListener, OnClickListener, KursWerteListener,
        BundleServer, TabCreator {
    /**
     * AllOK: Bitleiste, wieviele Felder mit gueltigen Werten erwartet werden.
     * Wird mit eingabeOK verglichen. Bei Uebereinstimmung, wird der
     * Weiter-Button freigeschaltet.
     */
    private final int AllOK = (int) (Math.pow(2, eingabewerteGesamtIDs.length) - 1);
    private final int numberOfTabs = 3;
    /**
     *
     */
    private float aktEigKurs = -1;
    /**
     *
     */
    private Button bWeiter;
    /**
     * eingabenOK: Bitleiste, in der festgehalten wird, welche Felder mit
     * gueltigen Werten befuellt wurden. Werden durch TextWatcher gesetzt.
     */
    private Integer eingabenOKfrag1 = 0;
    /**
     *
     */
    private Integer eingabenOKfrag2 = 0;
    /**
     *
     */
    private Integer eingabenOKfrag3 = 0;
    /**
     *
     */
    private ArrayList<EigenerKursListener> kwl = new ArrayList<EigenerKursListener>();
    /**
     *
     */
    private Bundle lagebundle;
    /**
     *
     */
    private float[] lagewertefrag1 = new float[eingabewerteGesamtIDs.length];
    /**
     *
     */
    private float[] lagewertefrag2 = new float[eingabewerteGesamtIDs.length];
    /**
     *
     */
    private float[] lagewertefrag3 = new float[eingabewerteGesamtIDs.length];

    /*
     * (non-Javadoc)
     *
     * @see
     * de.alexanderwinkler.main.KursWerteListener#addEigenerKursChangedListener
     * (de
     * .alexanderwinkler.main.ActivityRadarPlottEingabe.EigenerKursChangedListener
     * )
     */
    @Override
    public boolean addEigenerKursChangedListener(EigenerKursListener k) {
        // kwl = new ArrayList<EigenerKursChangedListener>();
        return kwl.add(k);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.alexanderwinkler.main.KursWerteListener#getAktKurs()
     */
    @Override
    public float getAktKurs() {
        return aktEigKurs;
    }

    @Override
    public Bundle getBundle() {
        return lagebundle;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.alexanderwinkler.div.SimpleTabHost.TabCreator#getNewTab(de.
     * alexanderwinkler.div.SimpleTabHost, int)
     */
    @Override
    public TabSpec getNewTab(SimpleTabHost mTabHost, int tabNumber) {
        switch (tabNumber) {
            case 1:
                return mTabHost.newTab(SimpleTabHost.TAB1, R.string.tabGegnerB, R.id.tab1);
            case 2:
                return mTabHost.newTab(SimpleTabHost.TAB2, R.string.tabGegnerC, R.id.tab2);
            case 3:
                return mTabHost.newTab(SimpleTabHost.TAB3, R.string.tabGegnerD, R.id.tab3);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        int resId = v.getId();
        switch (resId) {
            case R.id.bWeiter:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                boolean northupOrientierung = false;
                if (sp != null) {
                    String orientierung =
                            sp.getString(Konstanten.PREF_RADARORIENTIERUNG, "northup");
                    northupOrientierung = orientierung.equals("northup");
                }
                Lage lage;
                lagebundle.putFloat(KEYMANOEVERKURSA, lagewertefrag1[0]);
                if (eingabenOKfrag1 == AllOK) {
                    lage = new Lage(northupOrientierung, (lagewertefrag1));
                    lagebundle.putSerializable(KEYLAGE + KEYGEGNERB, lage);
                    lagebundle.putFloatArray(KEYEINGABE + KEYGEGNERB, lagewertefrag1);
                } else {
                    lagebundle.remove(KEYLAGE + KEYGEGNERB);
                    lagebundle.remove(KEYEINGABE + KEYGEGNERB);
                }
                if (eingabenOKfrag2 == AllOK) {
                    lage = new Lage(northupOrientierung, lagewertefrag2);
                    lagebundle.putSerializable(KEYLAGE + KEYGEGNERC, lage);
                    lagebundle.putFloatArray(KEYEINGABE + KEYGEGNERC, lagewertefrag2);
                } else {
                    lagebundle.remove(KEYLAGE + KEYGEGNERC);
                    lagebundle.remove(KEYEINGABE + KEYGEGNERC);
                }
                if (eingabenOKfrag3 == AllOK) {
                    lage = new Lage(northupOrientierung, lagewertefrag3);
                    lagebundle.putSerializable(KEYLAGE + KEYGEGNERD, lage);
                    lagebundle.putFloatArray(KEYEINGABE + KEYGEGNERD, lagewertefrag3);
                } else {
                    lagebundle.remove(KEYLAGE + KEYGEGNERD);
                    lagebundle.remove(KEYEINGABE + KEYGEGNERD);
                }
                Intent intent = new Intent(this, ActivityRadarPlottErgebnis.class);
                intent.putExtra(KEYBUNDLE, lagebundle);
                startActivity(intent);
                break;
            default:
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eingabe_start);
        // setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            lagebundle = new Bundle();
        } else {
            lagebundle = savedInstanceState.getBundle(KEYBUNDLE);
        }
        SimpleTabHost fth = (SimpleTabHost) getSupportFragmentManager()
                .findFragmentById(R.id.extendedTabHost);
        fth.setNumberOfPages(numberOfTabs);
        bWeiter = (Button) findViewById(R.id.bWeiter);
        bWeiter.setOnClickListener(this);
        bWeiter.setEnabled(false);
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
     * @see de.alexanderwinkler.div.MaxInhaltTextWatcher.ErgebnisListener#
     * onEigenerKursChanged(float)
     */
    @Override
    public void onEigenerKursChanged(float kurs) {
        this.aktEigKurs = kurs;
        onKursWertChanged(kurs);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.alexanderwinkler.main.KursWerteListener#onKursWertChanged(float)
     */
    @Override
    public void onKursWertChanged(float kurs) {
        for (EigenerKursListener e : kwl) {
            e.onEigenerKurs(kurs);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
            case R.id.beschreibung:
                // Intension und Hilfe anzeigen
                AlertDialog.Builder beschreibung = new AlertDialog.Builder(this);
                // 2. Chain together various setter methods to set the dialog
                // characteristics
                CharSequence cs = Html.fromHtml((String) getResources().getText(
                        R.string.helptext));
                beschreibung.setMessage(cs).setTitle(R.string.helptitel);
                // 3. Get the AlertDialog from create()
                AlertDialog beschreibungdialog = beschreibung.create();
                beschreibungdialog.show();
                return true;
            case R.id.about:
                // About anzeigen
                final TextView message = new TextView(this);
                // Text im html-Format aufbauen
                final SpannableString s = new SpannableString(
                        Html.fromHtml((String) getResources().getText(
                                R.string.abouttext)));
                // Dafuer sorgen, dass Links als auswaehlberer Link angezeigt werden
                Linkify.addLinks(s, Linkify.WEB_URLS);
                message.setText(s);
                // Textgroesse setzen
                message.setTextSize(20);
                // message modifizieren, damit Links ausgewaehlt werden koennen
                message.setMovementMethod(LinkMovementMethod.getInstance());
                // Dialog erstellen und anzeigen
                new AlertDialog.Builder(this).setTitle(R.string.about)
                        .setCancelable(true)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(message).create().show();
                return true;
            case R.id.settings:
                // Start der Preference-Settings
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
     * @see
     * android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os
     * .Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEYBUNDLE, lagebundle);
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
        // Nichts
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.alexanderwinkler.main.KursWerteListener#removeEigenerKursChangedListener
     * (
     * de.alexanderwinkler.main.ActivityRadarPlottEingabe.EigenerKursChangedListener
     * )
     */
    @Override
    public boolean removeEigenerKursChangedListener(EigenerKursListener k) {
        // kwl = new ArrayList<EigenerKursChangedListener>();
        return kwl.remove(k);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.alexanderwinkler.div.MaxInhaltTextWatcher.ErgebnisListener#setError
     * (android.widget.EditText, float)
     */
    @Override
    public void setError(EditText et, float maxinhalt) {
        String error = String.format(getResources().getString(
                R.string.eingabefehler)
                + " " + maxinhalt);
        et.setError(error);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.alexanderwinkler.div.MaxInhaltTextWatcher.ErgebnisListener#setWertNOK
     * (java.lang.String, int)
     */
    @Override
    public void setWertNOK(String fragmentId, int index) {
        // Feld hat ungueltigen Inhalt (z.B. leer). Falls vorher ein
        // gueltiger Wert gesetzt war, ist das Flag in den Eingaben als
        // gueltig gesetzt. Zuruecksetzen. Loeschen der Eingabe
        // Fuer Pruefung des Ergebnisses wird der index benoetigt, um die
        // Bitleiste im ErgebnisListerner zu setzen.
        int in = (int) Math.pow(2, index);
        if (KEYFRAGMENTEIGSCHIFF.equals(fragmentId)) {
            eingabenOKfrag1 = eingabenOKfrag1 & ~in;
            eingabenOKfrag2 = eingabenOKfrag2 & ~in;
            eingabenOKfrag3 = eingabenOKfrag3 & ~in;
        }
        if (KEYGEGNERB.equals(fragmentId)) {
            eingabenOKfrag1 = eingabenOKfrag1 & ~in;
        }
        if (KEYGEGNERC.equals(fragmentId)) {
            eingabenOKfrag2 = eingabenOKfrag2 & ~in;
        }
        if (KEYGEGNERD.equals(fragmentId)) {
            eingabenOKfrag3 = eingabenOKfrag3 & ~in;
        }
        if (eingabenOKfrag1 != AllOK & eingabenOKfrag2 != AllOK & eingabenOKfrag3 != AllOK) {
            bWeiter.setEnabled(false);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.alexanderwinkler.div.MaxInhaltTextWatcher.ErgebnisListener#setWertOK
     * (java.lang.String, int, float)
     */
    @Override
    public void setWertOK(String fragmentId, int index, float value) {
        // Feld hat ungueltigen Inhalt (z.B. leer). Falls vorher ein
        // gueltiger Wert gesetzt war, ist das Flag in den Eingaben als
        // gueltig gesetzt. Zuruecksetzen. Loeschen der Eingabe
        // Fuer Pruefung des Ergebnisses wird der index benoetigt, um die
        // Bitleiste im ErgebnisListerner zu setzen.
        int in = (int) Math.pow(2, index);
        if (KEYFRAGMENTEIGSCHIFF.equals(fragmentId)) {
            eingabenOKfrag1 = eingabenOKfrag1 | in;
            eingabenOKfrag2 = eingabenOKfrag2 | in;
            eingabenOKfrag3 = eingabenOKfrag3 | in;
            lagewertefrag1[index] = value;
            lagewertefrag2[index] = value;
            lagewertefrag3[index] = value;
        }
        if (KEYGEGNERB.equals(fragmentId)) {
            eingabenOKfrag1 = eingabenOKfrag1 | in;
            lagewertefrag1[index] = value;
        }
        if (KEYGEGNERC.equals(fragmentId)) {
            eingabenOKfrag2 = eingabenOKfrag2 | in;
            lagewertefrag2[index] = value;
        }
        if (KEYGEGNERD.equals(fragmentId)) {
            eingabenOKfrag3 = eingabenOKfrag3 | in;
            lagewertefrag3[index] = value;
        }
        if (eingabenOKfrag1 == AllOK || eingabenOKfrag2 == AllOK || eingabenOKfrag3 == AllOK) {
            bWeiter.setEnabled(true);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.alexanderwinkler.div.SimpleTabHost.TabCreator#updateTab(android.view
     * .View, java.lang.String, int)
     */
    @Override
    public void updateTab(View view, String tabTag, int placeholder) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(tabTag) == null) {
            Bundle args = new Bundle();
            // Setzen der FragmentId fuer die Eingabe der Gegner-Werte. Ist
            // identich mit der jeweiligen tabID
            args.putString(KEYFRAGMENTID, tabTag);
            FragmentEingabeGegnerWerte fe = new FragmentEingabeGegnerWerte();
            fe.setArguments(args);
            fm.beginTransaction().replace(placeholder, fe, tabTag).commit();
        }
    }

    public interface EigenerKursListener {
        void onEigenerKurs(float kurs);
    }
}