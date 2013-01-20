package com.dodola.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewRounded extends ImageView {

	public ImageViewRounded(Context context) {
		super(context);
	}

	public ImageViewRounded(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageViewRounded(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		BitmapDrawable drawable = (BitmapDrawable) getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}

		Bitmap fullSizeBitmap = drawable.getBitmap();

		int scaledWidth = getMeasuredWidth();
		int scaledHeight = getMeasuredHeight();

		Bitmap mScaledBitmap;
		if (scaledWidth == fullSizeBitmap.getWidth()
				&& scaledHeight == fullSizeBitmap.getHeight()) {
			mScaledBitmap = fullSizeBitmap;
		} else {
			mScaledBitmap = Bitmap.createScaledBitmap(fullSizeBitmap,
					scaledWidth, scaledHeight, true /* filter */);
		}

		Bitmap roundBitmap = Utils.getRoundedCornerBitmap(mScaledBitmap, 50);
		canvas.drawBitmap(roundBitmap, 0, 0, null);

	}

}