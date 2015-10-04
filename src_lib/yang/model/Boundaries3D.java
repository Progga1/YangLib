package yang.model;

import java.nio.FloatBuffer;

public class Boundaries3D {

	public float mMinX,mMaxX;
	public float mMinY,mMaxY;
	public float mMinZ,mMaxZ;

	public Boundaries3D() {

	}

	public Boundaries3D(float minX,float maxX,float minY,float maxY,float minZ,float maxZ) {
		mMinX = minX;
		mMaxX = maxX;
		mMinY = minY;
		mMaxY = maxY;
		mMinZ = minZ;
		mMaxZ = maxZ;
	}

	public Boundaries3D(float dimX,float dimY,float dimZ) {
		this(-dimX/2,dimX/2, -dimY,dimY, -dimZ,dimZ);
	}

	public Boundaries3D set(float minX,float maxX,float minY,float maxY,float minZ,float maxZ) {
		mMinX = minX;
		mMaxX = maxX;
		mMinY = minY;
		mMaxY = maxY;
		mMinZ = minZ;
		mMaxZ = maxZ;
		return this;
	}

	public Boundaries3D set(float dimX,float dimY,float dimZ) {
		return set(-dimX/2,dimX/2, -dimY,dimY, -dimZ,dimZ);
	}

	public float deltaX() {
		return mMaxX-mMinX;
	}

	public float deltaY() {
		return mMaxY-mMinY;
	}

	public float deltaZ() {
		return mMaxZ-mMinZ;
	}

	public float centerX() {
		return (mMaxX-mMinX)*0.5f;
	}

	public float centerY() {
		return (mMaxY-mMinY)*0.5f;
	}

	public float centerZ() {
		return (mMaxZ-mMinZ)*0.5f;
	}

	public float getRandomX() {
		return mMinX + (float)Math.random()*(mMaxX-mMinX);
	}

	public float getRandomY() {
		return mMinY + (float)Math.random()*(mMaxY-mMinY);
	}

	public float getRandomZ() {
		return mMinZ + (float)Math.random()*(mMaxZ-mMinZ);
	}

	public float getVolume() {
		return (mMaxX-mMinX)*(mMaxY-mMinY)*(mMaxZ-mMinZ);
	}

	public void setInitMinMax() {
		mMinX = Float.MAX_VALUE;
		mMaxX = -Float.MAX_VALUE;
		mMinY = Float.MAX_VALUE;
		mMaxY = -Float.MAX_VALUE;
		mMinZ = Float.MAX_VALUE;
		mMaxZ = -Float.MAX_VALUE;
	}

	public void setMinMaxByXYZArray(float[] values) {
		setInitMinMax();
		for(int i=0;i<values.length;i+=3) {
			float x = values[i];
			float y = values[i+1];
			float z = values[i+2];
			if(x<mMinX)
				mMinX = x;
			if(x>mMaxX)
				mMaxX = x;
			if(y<mMinY)
				mMinY = y;
			if(y>mMaxY)
				mMaxY = y;
			if(z<mMinZ)
				mMinZ = z;
			if(z>mMaxZ)
				mMaxZ = z;
		}
	}

	public void setMinMaxByXYZBuffer(FloatBuffer floatBuffer) {
		setInitMinMax();
		floatBuffer.position(0);
		int c = floatBuffer.capacity();
		for(int i=0;i<c;i+=3) {
			float x = floatBuffer.get();
			float y = floatBuffer.get();
			float z = floatBuffer.get();
			if(x<mMinX)
				mMinX = x;
			if(x>mMaxX)
				mMaxX = x;
			if(y<mMinY)
				mMinY = y;
			if(y>mMaxY)
				mMaxY = y;
			if(z<mMinZ)
				mMinZ = z;
			if(z>mMaxZ)
				mMaxZ = z;
		}
	}

	@Override
	public String toString() {
		return mMinX + "<=X<=" + mMaxX+"; " + mMinY + "<=Y<=" + mMaxY+"; " + mMinZ + "<=Z<=" + mMaxZ;
	}

}
