package yang.math.objects;

public class Plane {

	public Point3f mBase;
	public Vector3f mNormal;

	public Plane(Point3f basePoint,Vector3f normal) {
		mBase = basePoint;
		mNormal = normal;
	}

	public Plane(float baseX,float baseY,float baseZ, float normX,float normY,float normZ) {
		mBase = new Point3f(baseX,baseY,baseZ);
		mNormal = new Vector3f(normX,normY,normZ);
	}

	public Plane() {
		this(0,0,0, 0,1,0);
	}

	public void normalize() {
		mNormal.normalize();
	}

	public void reset() {
		mBase.set(0,0,0);
		mNormal.set(0,1,0);
	}

	public void set(Plane template) {
		mBase.set(template.mBase);
		mNormal.set(template.mNormal);
	}

}
