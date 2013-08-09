package yang.math.objects;


public class Bounds extends Quadruple {

	public static final int ID_LEFT = 0;
	public static final int ID_BOTTOM = 1;
	public static final int ID_RIGHT = 2;
	public static final int ID_TOP = 3;
	
	public Bounds() {
		super();
	}
	
	public Bounds(float v1,float v2,float v3,float v4) {
		this();
		set(v1,v2,v3,v4);
	}
	
	public Bounds(float[] values) {
		this();
		set(values);
	}

	public float getLeft() {
		return mValues[ID_LEFT];
	}
	
	public float getBottom() {
		return mValues[ID_BOTTOM];
	}
	
	public float getRight() {
		return mValues[ID_RIGHT];
	}
	
	public float getTop() {
		return mValues[ID_TOP];
	}
	
}
