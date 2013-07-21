package yang.math.objects;

import java.nio.ByteBuffer;


public class Quadruple {

	public static final Quadruple ZERO = new Quadruple(0,0,0,0);
	public static final Quadruple ONE = new Quadruple(1,1,1,1);
	public static final Quadruple Q0011 = new Quadruple(0,0,1,1);
	public static final Quadruple Q0101 = new Quadruple(0,1,0,1);
	
	public float[] mValues;
	
	public Quadruple() {
		mValues = new float[4];
	}
	
	public Quadruple(float v1,float v2,float v3,float v4) {
		this();
		set(v1,v2,v3,v4);
	}
	
	@Override
	public String toString()
	{
		return "("+mValues[0]+", "+mValues[1]+", "+mValues[2]+", "+mValues[3]+")";
	}

	public void set(float v1, float v2, float v3, float v4) {
		mValues[0] = v1;
		mValues[1] = v2;
		mValues[2] = v3;
		mValues[3] = v4;
	}
	
	public void set(float v1, float v2, float v3) {
		mValues[0] = v1;
		mValues[1] = v2;
		mValues[2] = v3;
	}
	
	public void set(Quadruple preface) {
		mValues[0] = preface.mValues[0];
		mValues[1] = preface.mValues[1];
		mValues[2] = preface.mValues[2];
		mValues[3] = preface.mValues[3];
	}
	
	public void set(float[] prefaceValues) {
		mValues[0] = prefaceValues[0];
		mValues[1] = prefaceValues[1];
		mValues[2] = prefaceValues[2];
		mValues[3] = prefaceValues[3];
	}
	
	public void add(float v1,float v2,float v3,float v4) {
		mValues[0] += v1;
		mValues[1] += v2;
		mValues[2] += v3;
		mValues[3] += v4;
	}
	
	public void add(Quadruple values) {
		mValues[0] += values.mValues[0];
		mValues[1] += values.mValues[1];
		mValues[2] += values.mValues[2];
		mValues[3] += values.mValues[3];
	}
	
	public void subtract(float v1,float v2,float v3,float v4) {
		mValues[0] -= v1;
		mValues[1] -= v2;
		mValues[2] -= v3;
		mValues[3] -= v4;
	}
	
	public void subtract(Quadruple values) {
		mValues[0] -= values.mValues[0];
		mValues[1] -= values.mValues[1];
		mValues[2] -= values.mValues[2];
		mValues[3] -= values.mValues[3];
	}
	
	public void writeIntoBuffer(ByteBuffer source) {
		source.put((byte)(mValues[0]*255));
		source.put((byte)(mValues[1]*255));
		source.put((byte)(mValues[2]*255));
		source.put((byte)(mValues[3]*255));
	}
	
	/**
	 * weight=0 => result=quad1
	 * weight=1 => result=quad2
	 */
	public void set(Quadruple quad1,Quadruple quad2,float weight) {
		float dWeight = 1-weight;
		mValues[0] = quad1.mValues[0]*dWeight+quad2.mValues[0]*weight;
		mValues[1] = quad1.mValues[1]*dWeight+quad2.mValues[1]*weight;
		mValues[2] = quad1.mValues[2]*dWeight+quad2.mValues[2]*weight;
		mValues[3] = quad1.mValues[3]*dWeight+quad2.mValues[3]*weight;
	}
	
}
