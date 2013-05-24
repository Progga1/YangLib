package yang.model;

public class Boundaries3D {

	public float mMinX,mMaxX;
	public float mMinY,mMaxY;
	public float mMinZ,mMaxZ;
	
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
	
}
