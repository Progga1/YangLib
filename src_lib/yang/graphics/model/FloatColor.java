package yang.graphics.model;

import java.nio.ByteBuffer;

import yang.math.objects.Quadruple;


public class FloatColor extends Quadruple {

	public static int R = 0;
	public static int G = 1;
	public static int B = 2;
	public static int A = 3;
	
	public static final float SILVERVALUE = 0.8f;
	public static final float GRAYVALUE = 0.5f;
	public static final FloatColor WHITE = new FloatColor(1,1,1);
	public static final FloatColor BLACK = new FloatColor(0,0,0);
	public static final FloatColor SILVER = new FloatColor(SILVERVALUE,SILVERVALUE,SILVERVALUE);
	public static final FloatColor GRAY = new FloatColor(GRAYVALUE,GRAYVALUE,GRAYVALUE);
	public static final FloatColor RED = new FloatColor(1,0,0);
	public static final FloatColor GREEN = new FloatColor(0,1,0);
	public static final FloatColor BLUE = new FloatColor(0,0,1);
	public static final FloatColor YELLOW = new FloatColor(1,1,0);
	
	public FloatColor(float red,float green,float blue,float alpha) {
		set(red,green,blue,alpha);
	}
	
	public FloatColor(float red,float green,float blue) {
		this(red,green,blue,1);
	}
	
	public FloatColor(float brightness) {
		this(brightness,brightness,brightness,1);
	}
	
	public FloatColor(float[] values) {
		set(values);
	}
	
	public FloatColor(FloatColor preface) {
		set(preface);
	}
	
	public FloatColor() {
		this(1,1,1,1);
	}

	public float getRed()
	{
		return mValues[R];
	}
	
	public float getGreen()
	{
		return mValues[G];
	}
	
	public float getBlue()
	{
		return mValues[B];
	}
	
	public float getAlpha()
	{
		return mValues[A];
	}
	
	public byte getRedByte()
	{
		return (byte)(mValues[R]*255);
	}
	
	public byte getGreenByte()
	{
		return (byte)(mValues[G]*255);
	}
	
	public byte getBlueByte()
	{
		return (byte)(mValues[B]*255);
	}
	
	public byte getAlphaByte()
	{
		return (byte)(mValues[A]*255);
	}
	
	public void premultiplyAlpha() {
		mValues[R] *= mValues[A];
		mValues[G] *= mValues[A];
		mValues[B] *= mValues[A];
	}
		
	public void writeIntoBuffer(ByteBuffer source,boolean includeAlpha) {
		source.put((byte)(mValues[0]*255));
		source.put((byte)(mValues[1]*255));
		source.put((byte)(mValues[2]*255));
		if(includeAlpha)
			source.put((byte)(mValues[3]*255));
	}
	
	public void writeIntoBufferRGB(ByteBuffer source) {
		source.put((byte)(mValues[0]*255));
		source.put((byte)(mValues[1]*255));
		source.put((byte)(mValues[2]*255));
	}

	public void copyToArray(float[] target) {
		System.arraycopy(mValues, 0, target, 0, 4);
	}

	public float[] createArray() {
		float[] result = new float[4];
		copyToArray(result);
		return result;
	}
	
	public FloatColor clone() {
		return new FloatColor(this);
	}

	public void set(float brightness) {
		set(brightness,brightness,brightness,1);
	}

	public void setAlpha(float alpha) {
		mValues[3] = alpha;
	}
	
}
