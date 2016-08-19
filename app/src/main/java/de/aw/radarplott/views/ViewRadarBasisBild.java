package de.aw.radarplott.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import de.aw.radarplott.Math.Gerade2D;
import de.aw.radarplott.Math.Kreis2D;
import de.aw.radarplott.Math.Punkt2D;
import de.aw.radarplott.R;
import de.aw.radarplott.berechnungen.Lage;
import de.aw.radarplott.div.ScalableRotateImageView;
import de.aw.radarplott.interfaces.Konstanten;
import de.aw.radarplott.interfaces.ManoeverServer;

/**
 * Zeichnet ein quadratisches Radarbild.
 * 
 * @author Alexander Winkler
 * 
 */
public class ViewRadarBasisBild extends View implements Konstanten {
	// haelt den Kontext der View
	private Context context;
	private boolean isInitialized = false;
	private Kompassrose kompassrose;
	private Lage lage;
	private Bundle lagebundle;
	private ManoeverServer mManoeverServer;
	private OnScaleChangeListener mScaleChangeListener;
	private boolean northupOrientierung;
	// Pfad fuer Linien
	private Path p = new Path();
	// Variablen zum Zeichnen
	private Paint paint = new Paint();
	private int rastergroesse = 3;
	// Skalierungsfaktor/ Massstab: Abstand der Radarkreise in Pixel. Notwendig,
	// um verschiedene Aufloesungen zu bedienen
	private float scale;
	
	{
		// Holen der Kompassrose
		// Vorbelegungen zum Zeichnen
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setColor(colorRadarLinien);
	}
	
	/**
	 *
	 * @param context
	 */
	public ViewRadarBasisBild(Context context) {
		super(context);
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
	public ViewRadarBasisBild(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ViewRadarBasisBild(Context context,
			OnScaleChangeListener onScaleChangeListener) {
		super(context);
		this.context = context;
		mScaleChangeListener = onScaleChangeListener;
		try {
			mManoeverServer = (ManoeverServer) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement ManoeverServer!");
		}
		try {
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnKurslinieTouchedListener!");
		}
		lagebundle = mManoeverServer.getBundle();
		lage = (Lage) lagebundle.getSerializable(KEYLAGE);
		initRadarBild();
	}
	
	/**
	 * Festlegen der Groesse des Radarbildes.
	 *
	 * @param rastergroesse
	 *            Anzahl der gewuenschten Rasterkreise
	 */
	public void initRadarBild() {

		// Radarbild erstellen, nur wenn Breite und Hoehe ungleich
		// 0 sind.
		if (getWidth() * getHeight() != 0) {
			// Festlegen Skalierungsfaktor
			float newscale = Math.min((getHeight() - 10) / rastergroesse / 2,
					(getWidth() - 10) / rastergroesse / 2);
			if (scale != newscale) {
				scale = newscale;
				mScaleChangeListener.onScaleChanged(scale);
			}
			// festlegen der Laenge der Linien Kreissektoren. Laenge 0.1f
			// heisst: 0.1 sm
			// Alle 2 Grad: Masstab (Pixel) / 40
			// Alle 10 Grad: Masstab (Pixel) / 20
			// Alle 30 Grad: Linie vom Mittelpunkt zum aeusseren Radarkreis
			float sektorlinienlaenge = 0.1f * scale;
			// mp: Mittelpunkt des Radarbildes. hp1, hp2: Hilfspunkte fuer
			// Zeichnen
			Punkt2D mp = new Punkt2D(), hp;
			// Array fuer Linien zur Kennzeichnung der Sektoren
			Punkt2D[] s = new Punkt2D[2];
			// Pfad fuer die Linien leeren
			p.reset();
			// Zeichnen der Sektoren
			for (int winkel = 0; winkel < 360; winkel += 2) {
				// Berechnen der Linien - Abstand 2 Grad
				hp = mp.mitWinkel(rastergroesse * scale, winkel);
				// Berechnen der Linien - Abstand 30 Grad, Linie ab Nullpunkt
				if (winkel % 30 == 0) {
					p.moveTo(0, 0);
					p.lineTo((float) hp.getX(), (float) hp.getY());
				}
				if (!hp.equals(mp)) {
					Gerade2D g = new Gerade2D(mp, hp);
					// Berechnen der Linien - Abstand 10 Grad
					if (winkel % 10 == 0) {
						Kreis2D k = new Kreis2D(hp, sektorlinienlaenge * 2);
						s = g.getSchnittpunkt(k);
						p.moveTo((float) s[0].getX(), (float) s[0].getY());
						p.lineTo((float) s[1].getX(), (float) s[1].getY());
					}
					Kreis2D k = new Kreis2D(hp, sektorlinienlaenge);
					s = g.getSchnittpunkt(k);
					p.moveTo((float) s[0].getX(), (float) s[0].getY());
					p.lineTo((float) s[1].getX(), (float) s[1].getY());
				}
			}
			isInitialized = true;
			invalidate();
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	public void onDraw(Canvas canvas) {
		if (isInitialized) {
			canvas.save();
			// Hintergrundfarbe setzen
			canvas.drawColor(colorRadarBackground);
			// Kompassrose zeichnen
			kompassrose.whenDraw(canvas);
			// Mittelpunkt des Radars liegt in der Mitte des Bildes (Canvas).
			canvas.translate(getWidth() / 2, getHeight() / 2);
			// Zeichnen der Radarkreise
			for (int i = 0; i < rastergroesse; i++) {
				canvas.drawCircle(0, 0, scale * (i + 1), paint);
			}
			canvas.drawPath(p, paint);
			canvas.restore();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		// neuinitialisieren des Radarbildes, da sich die Skalierung geaendert
		// hat.
		if (w * h * rastergroesse != 0) {
			initRadarBild();
			kompassrose = new Kompassrose();
		}
	}

	public void setNorthUpOrientierung(boolean northupOrientierung) {
		this.northupOrientierung = northupOrientierung;
		kompassrose = new Kompassrose();
		invalidate();
	}

	public void setRastergroesse(int rastergroesse) {
		this.rastergroesse = rastergroesse;
		initRadarBild();
	}

	public interface OnScaleChangeListener {
		void onScaleChanged(float scale);
	}
	
	public class Kompassrose {
		private Rect bounds;
		private Bitmap kompassbitmap, kompassorig;
		private Drawable kompassdrawable;
		
		public Kompassrose() {
			int w = ViewRadarBasisBild.this.getWidth();
			int h = ViewRadarBasisBild.this.getHeight();
			if (h * w > 0) {
				Drawable drawing = getResources().getDrawable(
						R.drawable.kompassrose);
				kompassorig = ((BitmapDrawable) drawing).getBitmap();
				if (northupOrientierung) {
					kompassbitmap = ScalableRotateImageView.rotateImage(
							kompassorig, 0);
				} else {
					// Orientierung ist HeadUp, daher alle Bilder auf den
					// entsprechenende Kurs drehen
					// Kompass auf RadarNord drehen
					kompassbitmap = ScalableRotateImageView.rotateImage(
							kompassorig, 360 - lage.getKAa());
					
				}
				int left = w - 160;
				int top = 0;
				int right = w;
				int bottom = (right - left <= h) ? right - left : h;
				if (bottom > 160) {
					bottom = 160;
				}
				Bitmap kompassscaled = ScalableRotateImageView.scaleImage(
						kompassbitmap, bottom / 2);
				kompassdrawable = new BitmapDrawable(
						ViewRadarBasisBild.this.context.getResources(),
						kompassscaled);
				
				bounds = new Rect(left, top, right, bottom);
				kompassdrawable.setBounds(bounds);
			}
		}
		
		protected void whenDraw(Canvas canvas) {
			kompassdrawable.draw(canvas);
		}
		
	}
}
