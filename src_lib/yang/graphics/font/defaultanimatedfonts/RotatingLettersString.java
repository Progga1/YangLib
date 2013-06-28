package yang.graphics.font.defaultanimatedfonts;

import yang.graphics.font.TransformAnimatedString;

public class RotatingLettersString extends TransformAnimatedString {

	public float mFrequency,mLetterTimeOffset,mIntensity;
	
	public RotatingLettersString(float frequency,float letterTimeOffset,float intensity) {
		mFrequency = frequency;
		mLetterTimeOffset = letterTimeOffset;
		mIntensity = intensity;
	}
	
	@Override
	protected void onPutLetter(int letterId) {
		mCurTransform.loadIdentity();
		float t = mProperties.mGraphics.mTranslator.mTimer;
		mCurTransform.rotateZ((float)Math.sin(t*mFrequency+letterId*mLetterTimeOffset)*mIntensity);
	}

	
	
}
