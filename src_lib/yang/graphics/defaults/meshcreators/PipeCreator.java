package yang.graphics.defaults.meshcreators;

import yang.graphics.defaults.Default3DGraphics;
import yang.math.objects.matrix.YangMatrix;

public class PipeCreator extends Grid3DCreator {

	public float mRadiusOffset;
	
	public PipeCreator(Default3DGraphics graphics) {
		super(graphics);
		mRadiusOffset = 0;
	}
	
	public void putPositions(float[][] radiusValues,YangMatrix transform,boolean loopNormals) {
		compRelations(radiusValues);
		float top = mCurDimY*0.5f;
		for(int row=0;row<mCurYCount;row++) {
			for(int col=0;col<mCurXCount;col++) {
				//float uCol = mSwapXY?row:col;
				float alpha = (float)col/(mCurXCount-1)*2*PI;
				float sinAlpha = (float)Math.sin(alpha);
				float cosAlpha = (float)Math.cos(alpha);
				float r;
				//r = radiusValues[row*mCurStepSize][col*mCurStepSize] + mRadiusOffset;
				r = interpolate(row,col);
				float x = -cosAlpha * r * mCurDimX;
				float y = top - mCurDimY + (float)row/(mCurYCount-1)*mCurDimY;
				float z = sinAlpha * r * mCurDimZ;
				if(transform!=null)
					mGraphics.putTransformedPosition(x,y,z,transform);
				else
					mGraphics.putPosition(x,y,z);
			}
		}
		mGraphics.fillNormals(0);
		if(loopNormals)
			super.mergeNormals();
	}
	
	public void putPositions(float[][] radiusValues,boolean loopNormals) {
		putPositions(radiusValues,null,loopNormals);
	}
	
	public void putPositions(YangMatrix transform,boolean loopNormals) {
		putPositions(null,transform,loopNormals);
	}
	
	public void putPositions() {
		putPositions(null,null,true);
	}
	
}
