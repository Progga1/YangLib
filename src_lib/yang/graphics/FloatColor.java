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
	
	public float[] values;
	
	public FloatColor(float red,float green,float blue,float alpha)
	{
		values = new float[4];
		values[0] = red;
		values[1] = green;
		values[2] = blue;
		values[3] = alpha;
	}
	
	public FloatColor(float red,float green,float blue) {
		this(red,green,blue,1);
	}
	
	public FloatColor(float brightness) {
		this(brightness,brightness,brightness,1);
	}
	
	public FloatColor(FloatColor preface) {
		values = new float[4];
		set(preface);
	}
	
	public FloatColor() {
		this(1,1,1,1);
	}

	public float getRed()
	{
		return values[0];
	}
	
	public float getGreen()
	{
		return values[1];
	}
	
	public float getBlue()
	{
		return values[2];
	}
	
	public float getAlpha()
	{
		return values[3];
	}
	
	public byte getRedByte()
	{
		return (byte)(values[0]*255);
	}
	
	public byte getGreenByte()
	{
		return (byte)(values[1]*255);
	}
	
	public byte getBlueByte()
	{
		return (byte)(values[2]*255);
	}
	
	public byte getAlphaByte()
	{
		return (byte)(values[3]*255);
	}
	
	@Override
	public String toString()
	{
		return "("+getRed()+", "+getGreen()+", "+getBlue()+", "+getAlpha()+")";
	}

	public void set(float r, float g, float b, float a) {
		values[0] = r;
		values[1] = g;
		values[2] = b;
		values[3] = a;
	}
	
	public void set(float r, float g, float b) {
		values[0] = r;
		values[1] = g;
		values[2] = b;
	}
	
	public void set(FloatColor color) {
		values[0] = color.values[0];
		values[1] = color.values[1];
		values[2] = color.values[2];
		values[3] = color.values[3];
	}
	
	public void set(FloatColor color1,FloatColor color2,float weight) {
		float dWeight = 1-weight;
		values[0] = color1.values[0]*dWeight+color2.values[0]*weight;
		values[1] = color1.values[1]*dWeight+color2.values[1]*weight;
		values[2] = color1.values[2]*dWeight+color2.values[2]*weight;
		values[3] = color1.values[3]*dWeight+color2.values[3]*weight;
	}
		
	public void writeIntoBuffer(ByteBuffer source,boolean includeAlpha) {
		source.put((byte)(values[0]*255));
		source.put((byte)(values[1]*255));
		source.put((byte)(values[2]*255));
		if(includeAlpha)
			source.put((byte)(values[3]*255));
	}
	
	public void writeIntoBufferRGBA(ByteBuffer source) {
		source.put((byte)(values[0]*255));
		source.put((byte)(values[1]*255));
		source.put((byte)(values[2]*255));
		source.put((byte)(values[3]*255));
	}
	
	public void writeIntoBufferRGB(ByteBuffer source) {
		source.put((byte)(values[0]*255));
		source.put((byte)(values[1]*255));
		source.put((byte)(values[2]*255));
	}

	public void copyToArray(float[] target) {
		System.arraycopy(values, 0, target, 0, 4);
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
