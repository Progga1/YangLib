package yang.graphics.font;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.model.TransformationMatrix;

public class DrawableAnchoredLines extends DrawableString{

	protected float[] mConstantOffsets = null;

	public DrawableAnchoredLines() {
		super();
	}
	
	public DrawableAnchoredLines(String string) {
		allocString(string);
	}
	
	protected void offsetsToPositions(float[] offsets,float[] positionTarget) {
		float charY = -mLineHeight;
		int o = 0;
		int c = 0;
		int count = mRecentCharCount+mRecentLineCount-1;
		for(int i=0;i<count;i++) {
			float charX = offsets[i];
			if(charX!=Float.MIN_VALUE) {
				TextureCoordinatesQuad coords = mTexCoords[o];
				float x2 = charX+(coords.x2-coords.x1)*mFont.mCharNormalizeFactorX;
				float y2 = charY+(coords.y2-coords.y1)*mFont.mCharNormalizeFactorY;
				positionTarget[c++] = charX;
				positionTarget[c++] = charY;
				if(mHasZComponent)
					positionTarget[c++] = 0;
				positionTarget[c++] = x2;
				positionTarget[c++] = charY;
				if(mHasZComponent)
					positionTarget[c++] = 0;
				positionTarget[c++] = charX;
				positionTarget[c++] = y2;
				if(mHasZComponent)
					positionTarget[c++] = 0;
				positionTarget[c++] = x2;
				positionTarget[c++] = y2;
				if(mHasZComponent)
					positionTarget[c++] = 0;
				o++;
			}else{
				charY -= mLineHeight;
			}
		}
	}
	
	public DrawableAnchoredLines setConstant() {
		if(mConstantPositions==null)
			mConstantPositions = new float[mCapacity*8];
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
	


	public void draw(TransformationMatrix transform) {
		float[] positions;
		TransformationMatrix resultTransf;
		if(mConstantPositions==null) {
			positions = staticPositions;
			float[] offsets;
			if(mConstantOffsets==null) {
				offsets = staticOffsets;
				createStringPositions(null,offsets);
			}else
				offsets = mConstantOffsets;
			offsetsToPositions(offsets,positions);
			mGraphics.mInterWorldTransf2.loadIdentity();
			mGraphics.mInterWorldTransf2.translate(0, mRecentStringHeight*mVerticalAnchor);
			mGraphics.mInterWorldTransf2.multiplyLeft(transform);
			resultTransf = mGraphics.mInterWorldTransf2;
		}else{
			positions = mConstantPositions;
			resultTransf = transform;
		}
		
		
		mGraphics.mTranslator.bindTexture(mFont.mTexture);

		putVertexProperties();

		mGraphics.mCurrentVertexBuffer.putTransformedArray(DefaultGraphics.ID_POSITIONS,positions,mRecentCharCount*4,mGraphics.mPositionBytes,resultTransf.asFloatArraySwallow());
	}
	
}
