package yang.graphics.font;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.math.objects.matrix.YangMatrix;

public class DrawableAnchoredLines extends DrawableString{

	protected float[] mConstantOffsets = null;

	public DrawableAnchoredLines() {
		super();
	}
	
	public DrawableAnchoredLines(String string) {
		allocString(string);
	}
	
	protected void offsetsToPositions(float[] offsets,float[] positionTarget) {
		float charY = -mSettings.mLineHeight;
		int o = 0;
		int c = 0;
		int count = mRecentCharCount+mRecentLineCount-1;
		for(int i=0;i<count;i++) {
			float charX = offsets[i];
			if(charX!=LINEBREAK_FLOAT) {
				TextureCoordinatesQuad coords = mTexCoords[o];
				float x2 = charX+(coords.x2-coords.x1)*mSettings.mFont.mCharNormalizeFactorX;
				float y2 = charY+(coords.y2-coords.y1)*mSettings.mFont.mCharNormalizeFactorY;
				positionTarget[c++] = charX;
				positionTarget[c++] = charY;
				if(mSettings.mHasZComponent)
					positionTarget[c++] = 0;
				positionTarget[c++] = x2;
				positionTarget[c++] = charY;
				if(mSettings.mHasZComponent)
					positionTarget[c++] = 0;
				positionTarget[c++] = charX;
				positionTarget[c++] = y2;
				if(mSettings.mHasZComponent)
					positionTarget[c++] = 0;
				positionTarget[c++] = x2;
				positionTarget[c++] = y2;
				if(mSettings.mHasZComponent)
					positionTarget[c++] = 0;
				o++;
			}else{
				charY -= mSettings.mLineHeight;
			}
		}
	}
	
	public DrawableAnchoredLines setConstant() {
		if(mConstantPositions==null)
			mConstantPositions = new float[mCapacity*(mSettings.mPosDim)*4];
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
		
		
		mSettings.mGraphics.mTranslator.bindTexture(mSettings.mFont.mTexture);

		putVertexProperties();

		mSettings.mGraphics.mCurrentVertexBuffer.putTransformedArray(DefaultGraphics.ID_POSITIONS,positions,mRecentCharCount*4,mSettings.mGraphics.mPositionDimension,resultTransf.mMatrix, 0,0,0);
	}
	
}
