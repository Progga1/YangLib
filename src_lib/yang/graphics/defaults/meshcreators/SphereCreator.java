package yang.graphics.defaults.meshcreators;

import yang.graphics.defaults.Default3DGraphics;
import yang.math.TransformationMatrix;

public class SphereCreator extends Grid3DCreator {

	public float mRadiusOffset;
	
	public SphereCreator(Default3DGraphics graphics) {
		super(graphics);
		mRadiusOffset = 0;
	}

	public void putPositions(float[][] radiusValues,TransformationMatrix transform,boolean loopNormals) {
		compRelations(radiusValues);
		for(int row=0;row<mCurYCount;row++) {
			//float beta = (float)Math.sin((float)row/(mCurYCount-1)*PI/2)*PI-PI/2;
			float beta = (float)row/(mCurYCount-1)*PI-PI/2;
			float sinBeta = (float)Math.sin(beta);
			float cosBeta = (float)Math.cos(beta);
			for(int col=0;col<mCurXCount;col++) {
				float alpha = (float)col/(mCurXCount-1)*2*PI;
				float sinAlpha = (float)Math.sin(alpha);
				float cosAlpha = (float)Math.cos(alpha);
				float r = interpolate(row,col);
				float x = -cosAlpha*cosBeta * r * mCurDimX;
				float y = sinBeta * r * mCurDimY;
				float z = sinAlpha*cosBeta * r * mCurDimZ;
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
	
	public void putPositions(TransformationMatrix transform,boolean loopNormals) {
		putPositions(null,transform,loopNormals);
	}
	
}
