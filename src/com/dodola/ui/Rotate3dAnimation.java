package com.dodola.ui;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3dAnimation extends Animation {
	private final float mFromDegrees;
	private final float mToDegrees;

	private Camera mCamera;
	private final float mCenterX;
	private final float mCenterY;

	public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX,
			float centerY) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		float degrees = fromDegrees
				+ ((mToDegrees - fromDegrees) * interpolatedTime);
		float Ydegrees = 50 * interpolatedTime;
		final Camera camera = mCamera;

		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Matrix matrix = t.getMatrix();

		camera.save();
		camera.translate(0.0f, Ydegrees, 0.0f);
		camera.rotateX(degrees);
		camera.getMatrix(matrix);
		camera.restore();

		matrix.preTranslate(0, 0);
		matrix.postTranslate(0, centerY);

	}
}
