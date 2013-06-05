package yang.graphics.font;


public abstract class AnimatedString extends DrawableAnchoredLines {

	protected abstract void putLetter(float[] target, int c, int letter, int letterNo, float x, float y);
	
	protected void newLine(int lineCount) {
		
	}
	
	protected void beginDraw() {
		
	}
	
	protected void endDraw() {
		
	}
	
	@Override
	protected void offsetsToPositions(float[] offsets,float[] positionTarget) {
		float charY = -mSettings.mLineHeight;
		int o = 0;
		int c = 0;
		int lineCount = 0;
		beginDraw();
		newLine(0);
		int count = mRecentCharCount+mRecentLineCount-1;
		for(int i=0;i<count;i++) {
			float charX = offsets[i];
			if(charX!=Float.MIN_VALUE) {
//				TextureCoordinatesQuad coords = mTexCoords[o];
//				putLetter(positionTarget,c,charX,charY,charX+(coords.x2-coords.x1)*mFont.mCharNormalizeFactorY,charY+(coords.y2-coords.y1)*mFont.mCharNormalizeFactorY,i);
				int val = mLetters[o];
				putLetter(positionTarget,c,val,o,charX+mSettings.mFont.mWidths[val]*0.5f,charY+mSettings.mFont.mHeights[val]*0.5f);
				if(mSettings.mHasZComponent)
					c+=12;
				else
					c+=8;
				o++;
			}else{
				charY -= mSettings.mLineHeight;
				newLine(++lineCount);
			}
		}
		endDraw();
	}
	
}
