package yang.graphics.textures;

import yang.graphics.translator.GraphicsTranslator;
import yang.math.TransformationMatrix;

public class TextureCoordinatesQuadMatrix extends TextureCoordinatesQuad {

	public static final TextureCoordinatesQuadMatrix FULL_TEXTURE = (TextureCoordinatesQuadMatrix)(new TextureCoordinatesQuadMatrix().init(0,0,1,1));
	
	private TransformationMatrix mTrafoMatrix;
	
	@Override
	public TextureCoordinatesQuadMatrix initBiased(float x1, float y1, float x2, float y2, float biasX, float biasY, boolean rotate) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.mWidth = x2-x1;
		this.mHeight = y2-y1;
		mTrafoMatrix = GraphicsTranslator.newTransformationMatrix();
		mTrafoMatrix.setRectBias(x1, y1, x2, y2, -biasX, -biasY);
		mAppliedCoordinates = new float[8];
		mTrafoMatrix.applyToRect2DInvertY(mAppliedCoordinates);
		mRatio = 1;
		if(rotate)
			super.rotateCoords();
		return this;
	}
	
	public final TransformationMatrix getTransformationMatrix() {
		return mTrafoMatrix;
	}
	
	public final float[] getTransformation() {
		return mTrafoMatrix.asFloatArraySwallow();
	}
	
}
