package de.aw.radarplott.main;

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

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import de.aw.radarplott.Math.Punkt2D;
import de.aw.radarplott.R;
import de.aw.radarplott.berechnungen.Lage;
import de.aw.radarplott.berechnungen.Manoever;
import de.aw.radarplott.interfaces.FragmentIdServer;
import de.aw.radarplott.interfaces.Konstanten;
import de.aw.radarplott.interfaces.LageChangeListener;
import de.aw.radarplott.interfaces.ManoeverServer;
import de.aw.radarplott.main.ActivityRadarPlottErgebnis.FragmentChangeListener;
import de.aw.radarplott.main.ActivityRadarPlottErgebnis.ManoeverListener;
import de.aw.radarplott.main.FragmentNumberPicker.ErgebnisListener;

/**
 * Fragment, um die Auswahlmoeglichkeiten darzustellen. Auswahlmoeglichkeiten
 * sind: Gegner sowie Kurs-/Fahrtaenderung A
 *
 * @author Alexander Winkler
 */
public class FragmentErgebnisAuswahlButtons extends Fragment implements
        Konstanten, ErgebnisListener, FragmentChangeListener, ManoeverListener,
        OnClickListener {
    private String fragmentId;
    private boolean isKurs;
    private Lage lage;
    private Bundle lagebundle;
    private FragmentIdServer mFragmentIdServer;
    private LageChangeListener mLageChangeListener;
    private ManoeverServer mManoeverServer;
    private LinkedHashMap<Integer, FragmentNumberPicker> npNPMap =
            new LinkedHashMap<Integer, FragmentNumberPicker>();
    private TextView tvManoeverText;

    private void doUpdateManoeverMessage(int senderId, FragmentNumberPicker np,
                                         FragmentNumberPicker.NPValues npvalues) {
        np.onMessage(senderId, FragmentNumberPicker.SETMANOEVER, npvalues);
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
                    getFragmentManager().findFragmentById(R.id.npmanoeverKurs).getView()
                            .setVisibility(View.VISIBLE);
                    getFragmentManager().findFragmentById(R.id.npmanoeverFahrt).getView()
                            .setVisibility(View.GONE);
                } else {
                    getFragmentManager().findFragmentById(R.id.npmanoeverKurs).getView()
                            .setVisibility(View.GONE);
                    getFragmentManager().findFragmentById(R.id.npmanoeverFahrt).getView()
                            .setVisibility(View.VISIBLE);
                }
                isKurs = !isKurs;
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
        int[] npIds = {R.id.npmanoeverAbstand, R.id.npmanoeverCPA,
                R.id.npmanoeverKurs, R.id.npmanoeverZeit, R.id.npmanoeverFahrt};
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
                    npvalues.maxValue = Math.abs(lage.a.getAktuellerStandort()
                            .getDistance(lage.b2.getAktuellerStandort()));
                    break;
                case R.id.npmanoeverKurs:
                    npvalues.minValue = 0f;
                    npvalues.maxValue = 359.0f;
                    break;
                case R.id.npmanoeverZeit:
                    npvalues.minValue = 0f;
                    npvalues.maxValue = 30f;
                    break;
                case R.id.npmanoeverFahrt:
                    npvalues.maxValue = 10;
                    npvalues.minValue = 0;
                    break;
            }
            if (npvalues.maxValue < npvalues.minValue) {
                float temp = npvalues.minValue;
                npvalues.minValue = npvalues.maxValue;
                npvalues.maxValue = temp;
            }
            np.onMessage(FragmentNumberPicker.INIT, FragmentNumberPicker.SETVALUES, npvalues);
        }
    }

    @Override
    public boolean onMessage(int what, FragmentNumberPicker.NPValues npvalues) {
        switch (what) {
            case FragmentNumberPicker.SETMANOEVER:
                float newValue = npvalues.value;
                int id = npvalues.id;
                switch (id) {
                    case R.id.npmanoeverAbstand:
                        try {
                            Punkt2D p = lage.b2.getPunktinFahrtrichtungmitAbstand(newValue);
                            newValue = lage.b2.getDauer(p);
                        } catch (IllegalArgumentException ex) {
                            Log.d(LOGTAG,
                                    "Der aktuell ausgewaehlte Wert fuer den Abstand " + newValue +
                                            " wird ignoriert.");
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
                    npvalues.value = Math.abs(manoever.a.getAktuellerStandort().getDistance(
                            manoever.b2.getAktuellerStandort()));
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
}