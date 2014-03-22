package yang.graphics.font;

import yang.math.objects.YangMatrix;

public abstract class TransformAnimatedString extends AnimatedString {

	protected YangMatrix mCurTransform;
	
	protected abstract void onPutLetter(int letterId);
	
	public TransformAnimatedString() {
		mCurTransform = new YangMatrix();
	}
	
	protected void putLetter(float[] target, int c, int letter, int letterNo, float x, float y) {
		onPutLetter(letterNo);
		
		if(mProperties.mHasZComponent)
			mCurTransform.applyToArray(mProperties.mFont.mPositions3D[letter], 4, true, 0, 0, x, y, target, c);
		else
			mCurTransform.applyToArray(mProperties.mFont.mPositions2D[letter], 4, false, 0, 0, x, y, target, c);
	}
	
}
