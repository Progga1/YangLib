package yang.graphics.font;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.math.objects.YangMatrix;

public class DrawableAnchoredLines extends DrawableString{

	protected float[] mConstantOffsets = null;

	public DrawableAnchoredLines() {
		super();
	}

	public DrawableAnchoredLines(String string) {
		allocString(string);
	}

	protected void offsetsToPositions(float[] offsets,float[] positionTarget) {
		float charY = -mProperties.mLineHeight;
		int o = 0;
		int c = 0;
		final int count = mRecentCharCount+mRecentLineCount-1;
		for(int i=0;i<count;i++) {
			final float charX = offsets[i];
			if(charX!=LINEBREAK_FLOAT) {
				final TextureCoordinatesQuad coords = mTexCoords[o];
				final float x2 = charX+(coords.mWidth)*mProperties.mFont.mCharNormalizeFactorX;
				final float y2 = charY+(coords.mHeight)*mProperties.mFont.mCharNormalizeFactorY;
				positionTarget[c++] = charX;
				positionTarget[c++] = charY;
				if(mProperties.mHasZComponent)
					positionTarget[c++] = 0;
				positionTarget[c++] = x2;
				positionTarget[c++] = charY;
				if(mProperties.mHasZComponent)
					positionTarget[c++] = 0;
				positionTarget[c++] = charX;
				positionTarget[c++] = y2;
				if(mProperties.mHasZComponent)
					positionTarget[c++] = 0;
				positionTarget[c++] = x2;
				positionTarget[c++] = y2;
				if(mProperties.mHasZComponent)
					positionTarget[c++] = 0;
				o++;
			}else{
				charY -= mProperties.mLineHeight;
			}
		}
	}

	@Override
	public DrawableAnchoredLines setConstant() {
		final int len = mCapacity*(mProperties.mPosDim)*4;
		if(mConstantPositions==null || mConstantPositions.length<len)
			mConstantPositions = new float[len];
		createStringPositions(null,staticOffsets);
		offsetsToPositions(staticOffsets,mConstantPositions);
		applyAnchors(0,mVerticalAnchor,mConstantPositions);
		return this;
	}

	public DrawableAnchoredLines setConstantOffsets() {
		if(mConstantOffsets==null)
			mConstantOffsets = new float[mCapacity];
		createStringPositions(null,mConstantOffsets);
		return this;
	}



	@Override
	public void draw(YangMatrix transform) {
		float[] positions;
		YangMatrix resultTransf;
		if(mConstantPositions==null) {
			positions = staticPositions;
			float[] offsets;
			if(mConstantOffsets==null) {
				offsets = staticOffsets;
				createStringPositions(null,offsets);
			}else
				offsets = mConstantOffsets;
			offsetsToPositions(offsets,positions);
			interMatrix.loadIdentity();
			interMatrix.translate(0, mRecentStringHeight*mVerticalAnchor);
			interMatrix.multiplyLeft(transform);
			resultTransf = interMatrix;
		}else{
			positions = mConstantPositions;
			resultTransf = transform;
		}

		mProperties.mGraphics.mTranslator.bindTexture(mProperties.mFont.mTexture);

		putVertexProperties();

		mProperties.mGraphics.mCurrentVertexBuffer.putTransformedArray(DefaultGraphics.ID_POSITIONS,positions,mRecentCharCount*4,mProperties.mGraphics.mPositionDimension,resultTransf.mValues, 0,0,0);
	}

}
