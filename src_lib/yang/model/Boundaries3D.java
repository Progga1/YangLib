package yang.model;

import java.nio.FloatBuffer;

import yang.math.objects.Point3f;

public class Boundaries3D {

	public static float INF_POS = Float.MAX_VALUE;
	public static float INF_NEG = -Float.MAX_VALUE;

	public float mMinX,mMaxX;
	public float mMinY,mMaxY;
	public float mMinZ,mMaxZ;

	public Boundaries3D() {
		this(INF_NEG,INF_POS, INF_NEG,INF_POS, INF_NEG,INF_POS);
	}

	public Boundaries3D(float minX,float maxX,float minY,float maxY,float minZ,float maxZ) {
		mMinX = minX;
		mMaxX = maxX;
		mMinY = minY;
		mMaxY = maxY;
		mMinZ = minZ;
		mMaxZ = maxZ;
	}

	public Boundaries3D(Boundaries3D template) {
		set(template);
	}

	public Boundaries3D(float dimX,float dimY,float dimZ) {
		this(-dimX*0.5f,dimX*0.5f, -dimY*0.5f,dimY*0.5f, -dimZ*0.5f,dimZ*0.5f);
	}

	public Boundaries3D(float size) {
		this(size,size,size);
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
		return set(-dimX*0.5f,dimX*0.5f, -dimY*0.5f,dimY*0.5f, -dimZ*0.5f,dimZ*0.5f);
	}

	public Boundaries3D set(float size) {
		return set(size,size,size);
	}

	public void set(Boundaries3D template) {
		mMinX = template.mMinX;
		mMaxX = template.mMaxX;
		mMinY = template.mMinY;
		mMaxY = template.mMaxY;
		mMinZ = template.mMinZ;
		mMaxZ = template.mMaxZ;
	}

	public Boundaries3D setCentered(float x,float y,float z, float width,float height,float depth) {
		mMinX = x - width*0.5f;
		mMaxX = x + width*0.5f;
		mMinY = y - height*0.5f;
		mMaxY = y + height*0.5f;
		mMinZ = z - depth*0.5f;
		mMaxZ = z + depth*0.5f;
		return this;
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

	public boolean within(Point3f point) {
		return mMinX<=point.mX && point.mX<=mMaxX && mMinY<=point.mY && point.mY<=mMaxY && mMinZ<=point.mZ && point.mZ<=mMaxZ;
	}

	@Override
	public String toString() {
		return mMinX + "<=X<=" + mMaxX+"; " + mMinY + "<=Y<=" + mMaxY+"; " + mMinZ + "<=Z<=" + mMaxZ;
	}

	public boolean isFinite() {
		return mMinX!=INF_NEG && mMaxX!=INF_POS && mMinY!=INF_NEG && mMaxY!=INF_POS && mMinZ!=INF_NEG && mMaxZ!=INF_POS;
	}

	public void setCornerPoints(Point3f[] pnts) {
		pnts[0].set(mMinX,mMinY,mMinZ);
		pnts[1].set(mMaxX,mMinY,mMinZ);
		pnts[2].set(mMaxX,mMaxY,mMinZ);
		pnts[3].set(mMinX,mMaxY,mMinZ);
		pnts[4].set(mMinX,mMinY,mMaxZ);
		pnts[5].set(mMaxX,mMinY,mMaxZ);
		pnts[6].set(mMaxX,mMaxY,mMaxZ);
		pnts[7].set(mMinX,mMaxY,mMaxZ);
	}

}
