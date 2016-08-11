package de.alexanderwinkler.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import de.alexanderwinkler.berechnungen.Lage;
import de.alexanderwinkler.interfaces.Konstanten;

public class ViewLage extends View implements Konstanten {
    //	// protected int rastergroesse;
    //	protected boolean isInitialized;
    //	protected volatile boolean isInvalid = true;
    //	protected ArrayList<Kurslinie.Draw> lagekurslinie = new ArrayList<Kurslinie.Draw>();
    //	protected ArrayList<Kurslinie.Draw> manoeverkurslinie = new ArrayList<Kurslinie.Draw>();
    //	protected Paint paint = new Paint(), manoeverpaint;
    protected Lage lage;
    //	protected Bundle lagebundle;
    protected String fragmentId;
    //	// protected RadarRahmenwerteServer mRadarRahmenwerteServer;
    //	protected int rastergroesse;
    //	protected float scale;
    //	protected boolean northupOrientierung;
    //	// private double manoeverkurs, manoeverzeitpunkt;
    //	private Punkt2D positionmanoever;
    //	private Manoever manoever;
    //
    //	{
    //		paint.setAntiAlias(true);
    //		paint.setStyle(Style.STROKE);
    //		paint.setStrokeWidth(0);
    //		paint.setTextSize(25);
    //		paint.setTextAlign(Align.CENTER);
    //		paint.setAlpha(0);
    //	}

    public ViewLage(Context context) {
        super(context);
        lage = null;
    }

    public ViewLage(Context context, AttributeSet attrs) {
        super(context, attrs);
        lage = null;
    }

    public ViewLage(Context context, String fragmentId) {
        super(context);
        this.fragmentId = fragmentId;
    }
    //
    //	public void initLageView() {
    //		if (scale != 0) {
    //			lagekurslinie.clear();
    //			paint.setColor(colorA);
    //			lagekurslinie
    //					.add(lage.a.new Draw(paint, rastergroesse, scale, true));
    //			lagekurslinie.add(lage.b0.new Draw(paint, scale));
    //			paint.setColor(colorB1);
    //			lagekurslinie.add(lage.b1.new Draw(paint, scale));
    //			paint.setColor(colorB2);
    //			lagekurslinie.add(lage.b2.new Draw(paint, rastergroesse, scale,
    //					true));
    //			lagekurslinie.add(lage.b2.new Draw(paint, scale));
    //			lagekurslinie.add(lage.cpa.new Draw(paint, scale));
    //		}
    //	}
    //
    //	public void initManoeverView() {
    //		if (scale != 0 & manoever != null) {
    //			// Paint fuer Manoever, gestrichelte Linie
    //			manoeverpaint = new Paint(paint);
    //			manoeverpaint.setPathEffect(new DashPathEffect(new float[] {
    //					0.25f * scale, 0.2f * scale }, 0));
    //			manoeverkurslinie.clear();
    //			Punkt2D p = manoever.b2.getAktPosition();
    //			positionmanoever = new Punkt2D(p.getX() * scale, p.getY() * scale);
    //			manoeverpaint.setColor(colorA);
    //			manoeverkurslinie.add(manoever.a.new Draw(manoeverpaint,
    //					rastergroesse, scale, true));
    //			manoeverkurslinie.add(manoever.b0.new Draw(manoeverpaint, scale));
    //			manoeverpaint.setColor(colorB1);
    //			manoeverkurslinie.add(manoever.b1.new Draw(manoeverpaint, scale));
    //			manoeverpaint.setColor(colorB2);
    //			// neue Kurslinien zum zeichnen errechnen und setzen.
    //			manoeverkurslinie.add(manoever.b2.new Draw(rastergroesse, scale,
    //					manoeverpaint));
    //			manoeverkurslinie.add(manoever.b3.new Draw(manoeverpaint,
    //					rastergroesse, scale, false));
    //			manoeverkurslinie.add(manoever.cpa.new Draw(manoeverpaint, scale));
    //			manoeverkurslinie.add(manoever.cpa.new Draw(paint, scale));
    //			isInitialized = true;
    //			invalidate();
    //		}
    //	}
    //
    //	/*
    //	 * (non-Javadoc)
    //	 *
    //	 * @see android.view.View#onDraw(android.graphics.Canvas)
    //	 */
    //	@Override
    //	public void onDraw(Canvas canvas) {
    //		if (lage != null) {
    //			isInvalid = true;
    //			canvas.save();
    //			canvas.drawColor(Color.TRANSPARENT);
    //			int w = getWidth();
    //			int h = getHeight();
    //			canvas.translate(w / 2, h / 2);
    //			// canvas.scale(scale, scale);
    //			if (!northupOrientierung) {
    //				canvas.rotate(360 - lage.getKAa());
    //			}
    //			for (Kurslinie.Draw k : lagekurslinie) {
    //				k.onDrawLine(canvas);
    //			}
    //			if (!manoeverkurslinie.isEmpty()) {
    //				for (Kurslinie.Draw k : manoeverkurslinie) {
    //					k.onDrawLine(canvas);
    //				}
    //			}
    //			if (positionmanoever != null) {
    //				paint.setColor(colorB2);
    //				canvas.drawCircle((float) positionmanoever.getX(),
    //						-(float) positionmanoever.getY(), 0.1f * scale, paint);
    //			}
    //		}
    //		canvas.restore();
    //		isInvalid = false;
    //	}
    //
    //
    //
    //	public void setRastergroesse(int rastergroesse) {
    //		this.rastergroesse = rastergroesse;
    //		initLageView();
    //		initManoeverView();
    //	}
    //
    //	public void setNorthUpOrientierung(boolean northupOrientierung) {
    //		this.northupOrientierung = northupOrientierung;
    //		invalidate();
    //	}
    //
    //	public void setScale(float scale) {
    //		this.scale = scale;
    //		initLageView();
    //		initManoeverView();
    //	}
}
