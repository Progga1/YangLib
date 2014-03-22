package yang.graphics.defaults.geometrycreators;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.geometrycreators.grids.Grid3DCreator;
import yang.math.Geometry;
import yang.math.objects.YangMatrix;

public class SphereCreator extends Grid3DCreator {

	public float mRadiusOffset;

	public SphereCreator(Default3DGraphics graphics) {
		super(graphics);
		mRadiusOffset = 0;
	}

	public void putPositions(float[][] radiusValues,YangMatrix transform,boolean loopNormals) {
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
					mGraphics.putPosition(x,y,z,transform);
				else
					mGraphics.putPosition(x,y,z);
				float d = Geometry.getDistance(x, y, z);
				if(d>0) {
					d = 1/d;
					mGraphics.putNormal(x*d,y*d,z*d);
				}else
					mGraphics.putNormal(0,1,0);
			}
		}
		//mGraphics.fillNormals(0);
//		if(loopNormals)
//			super.mergeNormals();
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
