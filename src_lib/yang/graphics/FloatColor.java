package yang.graphics;

import java.nio.ByteBuffer;


public class FloatColor {

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
	
	public float[] mValues;
	
	public FloatColor(float red,float green,float blue,float alpha)
	{
		mValues = new float[4];
		mValues[0] = red;
		mValues[1] = green;
		mValues[2] = blue;
		mValues[3] = alpha;
	}
	
	public FloatColor(float red,float green,float blue) {
		this(red,green,blue,1);
	}
	
	public FloatColor(float brightness) {
		this(brightness,brightness,brightness,1);
	}
	
	public FloatColor(FloatColor preface) {
		mValues = new float[4];
		set(preface);
	}
	
	public FloatColor() {
		this(1,1,1,1);
	}

	public float getRed()
	{
		return mValues[0];
	}
	
	public float getGreen()
	{
		return mValues[1];
	}
	
	public float getBlue()
	{
		return mValues[2];
	}
	
	public float getAlpha()
	{
		return mValues[3];
	}
	
	public byte getRedByte()
	{
		return (byte)(mValues[0]*255);
	}
	
	public byte getGreenByte()
	{
		return (byte)(mValues[1]*255);
	}
	
	public byte getBlueByte()
	{
		return (byte)(mValues[2]*255);
	}
	
	public byte getAlphaByte()
	{
		return (byte)(mValues[3]*255);
	}
	
	@Override
	public String toString()
	{
		return "("+getRed()+", "+getGreen()+", "+getBlue()+", "+getAlpha()+")";
	}

	public void set(float r, float g, float b, float a) {
		mValues[0] = r;
		mValues[1] = g;
		mValues[2] = b;
		mValues[3] = a;
	}
	
	public void set(float r, float g, float b) {
		mValues[0] = r;
		mValues[1] = g;
		mValues[2] = b;
	}
	
	public void set(FloatColor color) {
		mValues[0] = color.mValues[0];
		mValues[1] = color.mValues[1];
		mValues[2] = color.mValues[2];
		mValues[3] = color.mValues[3];
	}
	
	public void set(FloatColor color1,FloatColor color2,float weight) {
		float dWeight = 1-weight;
		mValues[0] = color1.mValues[0]*dWeight+color2.mValues[0]*weight;
		mValues[1] = color1.mValues[1]*dWeight+color2.mValues[1]*weight;
		mValues[2] = color1.mValues[2]*dWeight+color2.mValues[2]*weight;
		mValues[3] = color1.mValues[3]*dWeight+color2.mValues[3]*weight;
	}
		
	public void writeIntoBuffer(ByteBuffer source,boolean includeAlpha) {
		source.put((byte)(mValues[0]*255));
		source.put((byte)(mValues[1]*255));
		source.put((byte)(mValues[2]*255));
		if(includeAlpha)
			source.put((byte)(mValues[3]*255));
	}
	
	public void writeIntoBufferRGBA(ByteBuffer source) {
		source.put((byte)(mValues[0]*255));
		source.put((byte)(mValues[1]*255));
		source.put((byte)(mValues[2]*255));
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
	
}
