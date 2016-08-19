package de.aw.radarplott.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.aw.radarplott.R;
import de.aw.radarplott.interfaces.Konstanten;

/**
 * Eigener, horizontaler NumberPicker mit TextFeld und Button (+/-) rechts und
 * links. Bietet alle Funktionen inklusive eines Android-Numberpickers, um Werte
 * ueber die Tastatur zu erhalten. Nutzende Aktivity muss das Interface
 * OnNPValueChangeListener implementieren.
 * 
 * @author Alexander Winkler
 * 
 */
public class FragmentNumberPicker extends Fragment implements Konstanten,
		OnTouchListener, Handler.Callback {
	
	public static final int TOGGLE = 1, SETMANOEVER = 2, SETVALUES = 3,
			LONGPRESS = 4, INIT = 5;
	protected Button np_btnMinus, np_btnPlus;
	protected TextView np_tvDescription;
	private Handler handler;
	private boolean isActive = true;
	private boolean isInitialized;
	private ErgebnisListener mErgebnisListener;
	private MyGestureListener mGestureDetector;
	private GestureDetectorCompat mGestureDetectorCompat;
	private String mTextPrev = "n/a", mTextLast = "n/a";
	private int maxValue = Integer.MAX_VALUE;
	private int minValue = Integer.MIN_VALUE;
	private int oldVal = 0;
	private int quotient = 1;
	private boolean setQuotient = false;
	private int value = 0;
	
	public FragmentNumberPicker() {
		handler = new Handler(this);

	}

	public void doToggleActive() {
		if (isInitialized) {
			if (isActive()) {
				np_tvDescription.setEnabled(true);
				if (value < minValue) {
					valueChange(value, minValue);
					value = minValue;
					setValueText(value);
				}
				if (value > minValue) {
					valueChange(value, maxValue);
					value = maxValue;
					setValueText(value);
				}
			} else {
				np_tvDescription.setEnabled(false);
			}
		}
	}

	/**
	 * Aktueller Wert
	 *
	 * @return liefert den aktuell gesetzen Wert zurueck {@link #value}
	 */
	public double getValue() {
		return value;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.Handler.Callback#handleMessage(android.os.Message)
	 */
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case LONGPRESS:
				value = msg.arg1;
				setValueText(value);
				valueChange(oldVal, value);
				return true;
		}
		return false;
	}

	public boolean isActive() {
		return isActive;
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
		View view = inflater.inflate(R.layout.fragment_numberpicker_single,
				container, false);
		np_btnMinus = (Button) view.findViewById(R.id.np_btn_minus);
		np_btnPlus = (Button) view.findViewById(R.id.np_btn_plus);
		np_tvDescription = (TextView) view.findViewById(R.id.np_tvDescription);
		mGestureDetector = new MyGestureListener();
		mGestureDetectorCompat = new GestureDetectorCompat(getActivity(),
				mGestureDetector);
		isInitialized = true;
		return view;
	}

	public boolean onMessage(int senderId, int what, NPValues npvalues) {
		if (getId() != senderId) {
			switch (what) {
				case SETMANOEVER:
					value = (int) (npvalues.value * quotient);
					setValueText(value);
					return true;
				case SETVALUES:
					maxValue = (int) Math.floor(npvalues.maxValue * quotient);
					minValue = (int) Math.ceil(npvalues.minValue * quotient);
					return true;
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		np_btnMinus.setOnTouchListener(this);
		np_btnPlus.setOnTouchListener(this);
		np_tvDescription.setOnTouchListener(this);
		// Belegung der TextView, festlegen des Quotienten
		setQuotient = false;
		switch (getId()) {
		case R.id.npmanoeverAbstand:
			// Werte fuer den Abstand, in dem der Gegner und A sind setzen
			setQuotient(10);
			setText((String) getResources().getText(R.string.npAbstand),
					(String) getResources().getText(R.string.sm));
			break;
		case R.id.npmanoeverKurs:
			setText((String) getResources().getText(R.string.npKurs),
					(String) getResources().getText(R.string.angle));
			break;
		// Werte fuer Kurs A setzen
		case R.id.npmanoeverCPA:
			// Werte fuer den CPA, in dem der Gegner und A sind setzen
			setQuotient(10);
			setText((String) getResources().getText(R.string.npCPA),
					(String) getResources().getText(R.string.sm));
			break;
		case R.id.npmanoeverZeit:
			// Werte fuer den Zeitpunkt des Manoevers (akt. Zeit + value in
			// Minuten).
			setText((String) getResources().getText(R.string.npZeitPunkt),
					(String) getResources().getText(R.string.minutes));
			break;
		case R.id.npmanoeverFahrt:
			setText((String) getResources().getText(R.string.npFahrt),
					(String) getResources().getText(R.string.speed));
			break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onStop() {
		super.onStop();
		np_btnMinus.setOnClickListener(null);
		np_btnPlus.setOnClickListener(null);
		np_tvDescription.setOnClickListener(null);
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int id = view.getId();
		int action = MotionEventCompat.getActionMasked(event);
		this.mGestureDetector.setViewId(id);
		boolean result = this.mGestureDetectorCompat.onTouchEvent(event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mErgebnisListener != null) {
				mErgebnisListener.onTouch(this, event);
			}
			switch (id) {
			case R.id.np_btn_minus:
				oldVal = value;
				value--;
				valueChange(oldVal, value);
				result = true;
				setValueText(value);
				break;
			case R.id.np_btn_plus:
				oldVal = value;
				value++;
				valueChange(oldVal, value);
				result = true;
				setValueText(value);
				break;
			case R.id.np_tvDescription:
				Toast.makeText(getActivity(),
						minValue + " / " + value + " / " + maxValue,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			break;
		case MotionEvent.ACTION_UP:
			mGestureDetector.interruptThread();
			result = true;
			break;
		default:
		}
		return result;
	}

	private double rundeWert(double wert) {
		return Math.round(wert * 10d) / 10d;
	}

	public void setErgebnisHandler(ErgebnisListener l) {
		mErgebnisListener = l;
	}

	public void setOnTouchListener(ErgebnisListener l) {
		mErgebnisListener = l;
	}
	
	/**
	 * Schritt, in denen der Wert des Numberpickers geaendert werden kann.
	 * Default 1. Darf nur einmal gesetzt werden.
	 *
	 * @param delta
	 * @throws IllegalArgumentException
	 */
	public void setQuotient(int quotient) {
		if (setQuotient) {
			throw new IllegalArgumentException(
					"Delta darf nur einmal gesetzt werden!");
		}
		setQuotient = true;
		this.quotient = quotient;
	}

	/**
	 * Setzt den Text des Numberpickers
	 *
	 * @param text
	 *            Text, der im Textfeld erscheinen soll-. Wird durch den
	 *            aktuellen Wert (value) ergaenzt.
	 */
	public void setText(String textPrev, String textLast) {
		mTextPrev = textPrev;
		mTextLast = textLast;
		setValueText(value / quotient);
	}

	/**
	 * Setzt den Text gemeinsam mit Value des Numberpickers. Wird aus {@link #mTextPrev} gelesen.
	 */
	protected void setValueText(double value) {
		np_tvDescription.setText(mTextPrev + Double.toString(rundeWert(value / quotient)) + " " + mTextLast);
	}
	
	public void toggleActive() {
		isActive = !isActive;
		doToggleActive();
	}

	/**
	 * @param id
	 * @param d
	 * @param e
	 */
	private void valueChange(int oldVal, int newVal) {
		NPValues npvalues = new NPValues();
		if (newVal > maxValue) {
			newVal = maxValue;
		}
		if (newVal < minValue) {
			newVal = minValue;
		}
		value = newVal;
		npvalues.value = (double) newVal / quotient;
		npvalues.oldValue = (double) oldVal / quotient;
		npvalues.id = getId();
		mErgebnisListener.onMessage(SETMANOEVER, npvalues);
	}

	public interface ErgebnisListener {
		boolean onMessage(int what, FragmentNumberPicker.NPValues npvalues);

		void onTouch(FragmentNumberPicker np, MotionEvent event);
	}

	static public class NPValues {
		public double minValue;
		public double maxValue;
		public double value;
		public double oldValue;
		public int id;
	}
	
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		private boolean isRunning;
		private int longPressDelta;
		private Thread thread;
		private int viewId;

		/**
		 *
		 */
		public void interruptThread() {
			if (isRunning) {
				isRunning = false;
				thread.interrupt();
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * android.view.GestureDetector.OnDoubleTapListener#onDoubleTap(android
		 * .view.MotionEvent)
		 */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * android.view.GestureDetector.OnGestureListener#onDown(android.view
		 * .MotionEvent)
		 */
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * android.view.GestureDetector.OnGestureListener#onLongPress(android
		 * .view.MotionEvent)
		 */
		@Override
		public void onLongPress(MotionEvent e) {
			switch (viewId) {
			case R.id.np_btn_minus:
				oldVal = value;
				longPressDelta = -1;
				break;
			case R.id.np_btn_plus:
				oldVal = value;
				longPressDelta = 1;
				break;
			default:
			}
			thread = new Thread() {
				int mValue = value;
				int lp = longPressDelta;
				int min = minValue;
				int max = maxValue;

				@Override
				public void run() {
					long verzoegerung = 300;
					while (!isInterrupted()) {

						long endTime = System.currentTimeMillis()
								+ verzoegerung;
						if (verzoegerung > 100) {
							verzoegerung = (long) (verzoegerung * 0.95);
						}
						while (System.currentTimeMillis() < endTime) {
							synchronized (this) {
								try {
									wait(endTime - System.currentTimeMillis());
								} catch (Exception e) {
									interrupt();
								}
							}
						}
						mValue = mValue + lp;
						if (mValue < min) {
							mValue = max;
						}
						if (mValue > max) {
							mValue = min;
						}
						Message msg = Message.obtain(handler, LONGPRESS,
								mValue, 0);
						if (!isInterrupted()) {
							handler.sendMessage(msg);
						}
					}

				}
			};
			thread.start();
			isRunning = true;
		}

		/**
		 * @param viewId
		 *            the viewId to set
		 */
		public void setViewId(int viewId) {
			this.viewId = viewId;
		}
	}
}
