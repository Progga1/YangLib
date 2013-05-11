package yang.graphics.font;

import yang.graphics.defaults.DefaultGraphics;
import yang.math.TransformationMatrix;

public abstract class TransformAnimatedString extends AnimatedString {

	protected TransformationMatrix mCurTransform;
	
	protected abstract void onPutLetter(int letterId);
	
	public TransformAnimatedString setGraphics(DefaultGraphics<?> graphics) {
		super.setGraphics(graphics);
		mCurTransform = graphics.mTranslator.createTransformationMatrix();
		return this;
	}

	protected void putLetter(float[] target, int c, int letter, int letterNo, float x, float y) {
		onPutLetter(letterNo);
		
		mCurTransform.applyToArray(mFont.mPositions[letter], 4, mHasZComponent, 0, 0, x, y, target, c);
	}
	
}
