package de.aw.radarplott.div;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * ImageView, welches skaliert und rotiert werden kann
 * 
 * @author Alexander Winkler
 * 
 */

public class ScalableRotateImageView extends ImageView {
	private final Bitmap origBitmap;
	private Context context;

	public ScalableRotateImageView(Context context) {
		super(context);
		this.context = context;
		Drawable drawing = getDrawable();
		Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();
		origBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
	}

	public ScalableRotateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		Drawable drawing = getDrawable();
		Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();
		origBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
	}

	public ScalableRotateImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		Drawable drawing = getDrawable();
		Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();
		origBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
	}
	
	public static Bitmap rotateImage(Bitmap origBitmap, int winkel) {
		Matrix matrix = new Matrix();
		Bitmap rotatedBitmap = origBitmap;
		if (winkel != 0) {
			// Nur wenn der Winkel != 0 ist, muss ueberhaupt gedreht werden.
			matrix.postRotate(winkel);
			rotatedBitmap = Bitmap
					.createBitmap(origBitmap, 0, 0, origBitmap.getWidth(),
							origBitmap.getHeight(), matrix, true);
		}
		return rotatedBitmap;
	}
	
	public static Bitmap scaleImage(Bitmap origBitmap, int boundBoxInDp) {
		// Get the ImageView and its bitmap
		// Get current dimensions
		int width = origBitmap.getWidth();
		int height = origBitmap.getHeight();

		// Determine how much to scale: the dimension requiring less scaling is
		// closer to the its side. This way the image always stays inside your
		// bounding box AND either x/y axis touches it.
		float xScale = ((float) boundBoxInDp) / width;
		float yScale = ((float) boundBoxInDp) / height;
		float scale = (xScale <= yScale) ? xScale : yScale;

		// Create a matrix for the scaling and add the scaling data
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		// Create a new bitmap and convert it to a format understood by the
		// ImageView
		Bitmap result = Bitmap.createBitmap(origBitmap, 0, 0, width, height,
				matrix, true);
		// Apply the scaled bitmap
		return result;
	}

	/**
	 * Dreht das Imge
	 *
	 * @param winkel
	 *         Winkel in Degrees, um den das Image gedreht werden soll Gedreht wird um den
	 *         Mittelpunkt
	 *
	 * @return das gedrehte ImageView
	 */
	public ScalableRotateImageView rotateMyImage(int winkel) {
		BitmapDrawable result;
		Bitmap rotatedBitmap = rotateImage(origBitmap, winkel);
		result = new BitmapDrawable(context.getResources(), rotatedBitmap);
		setImageDrawable(result);
		return this;
	}

	/**
	 * Skaliert das ImageView auf einen festgelegten Wert
	 *
	 * @param boundBoxInDp
	 *            Wert in dp, auf den das Image skaliert werden soll
	 * @return das skalierte ImageView
	 */
	public ScalableRotateImageView scaleMyImage(int boundBoxInDp) {
		Bitmap scaledBitmap = scaleImage(origBitmap, boundBoxInDp);
		BitmapDrawable result = new BitmapDrawable(context.getResources(),
				scaledBitmap);
		// Apply the scaled bitmap
		setImageDrawable(result);
		// Now change ImageView's dimensions to match the scaled image
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
		params.width = scaledBitmap.getWidth();
		params.height = scaledBitmap.getHeight();
		setLayoutParams(params);
		return this;
	}
	
}
