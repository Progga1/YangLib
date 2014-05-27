package yang.graphics.model;

import java.nio.ByteBuffer;

import yang.math.objects.Quadruple;
import yang.util.Util;


public class FloatColor extends Quadruple {

	public static int R = 0;
	public static int G = 1;
	public static int B = 2;
	public static int A = 3;

	public static final float SILVERVALUE = 0.8f;
	public static final float GRAYVALUE = 0.5f;
	public static final FloatColor WHITE = new FloatColor(1,1,1);
	public static final FloatColor BLACK = new FloatColor(0,0,0);
	public static final FloatColor NULL = new FloatColor(0,0,0,0);
	public static final FloatColor WHITE_TRANSPARENT = new FloatColor(1,1,1,0);
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

	public FloatColor(Quadruple preface) {
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
		final float[] result = new float[4];
		copyToArray(result);
		return result;
	}

	@Override
	public FloatColor clone() {
		return new FloatColor(this);
	}

	public void set(float brightness) {
		set(brightness,brightness,brightness,1);
	}

	public void setAlpha(float alpha) {
		mValues[3] = alpha;
	}

	public static FloatColor fromHex(String hexString) {
		final float[] values = new float[4];
		values[3] = 1;
		for(int i=0;i<hexString.length()/2 && i<4;i++) {
			final int c1 = Util.hexCharToInt(hexString.charAt(i*2));
			final int c2 = Util.hexCharToInt(hexString.charAt(i*2+1));
			if(c1<0 || c2<0)
				values[i] = 0;
			else
				values[i] = (c1*16+c2)/255f;
		}
		return new FloatColor(values);
	}

	public static FloatColor fromHex(int rgba) {
		return new FloatColor(
				((rgba >> 24) 	& 0xff) / 255f,
				((rgba >> 16) 	& 0xff) / 255f,
				((rgba >> 8) 	& 0xff) / 255f,
				((rgba) 		& 0xff) / 255f);
	}

	public FloatColor cloneWithAlpha(float alpha) {
		return new FloatColor(mValues[0],mValues[1],mValues[2],alpha);
	}

	public void multBrightness(float factor) {
		if(factor<1) {
			mValues[0] *= factor;
			mValues[1] *= factor;
			mValues[2] *= factor;
		}
		if(factor>1) {
			final float dFactor = factor-1;
			mValues[0] += (1-mValues[0])*dFactor;
			mValues[1] += (1-mValues[1])*dFactor;
			mValues[2] += (1-mValues[2])*dFactor;
		}
	}

	public void setBlend(FloatColor startColor,FloatColor endColor,float weight) {
		mValues[0] = (1-weight)*startColor.mValues[0] + weight*endColor.mValues[0];
		mValues[1] = (1-weight)*startColor.mValues[1] + weight*endColor.mValues[1];
		mValues[2] = (1-weight)*startColor.mValues[2] + weight*endColor.mValues[2];
		mValues[3] = (1-weight)*startColor.mValues[3] + weight*endColor.mValues[3];
	}

	public void blend(FloatColor endColor,float weight) {
		mValues[0] = (1-weight)*mValues[0] + weight*endColor.mValues[0];
		mValues[1] = (1-weight)*mValues[1] + weight*endColor.mValues[1];
		mValues[2] = (1-weight)*mValues[2] + weight*endColor.mValues[2];
		mValues[3] = (1-weight)*mValues[3] + weight*endColor.mValues[3];
	}

	public void toArray(float[] target, int offset) {
		target[offset] = mValues[0];
		target[offset+1] = mValues[1];
		target[offset+2] = mValues[2];
		target[offset+3] = mValues[3];
	}

	public void clamp() {
		if(mValues[0]<0)
			mValues[0] = 0;
		if(mValues[0]>1)
			mValues[0] = 1;
		if(mValues[1]<0)
			mValues[1] = 0;
		if(mValues[1]>1)
			mValues[1] = 1;
		if(mValues[2]<0)
			mValues[2] = 0;
		if(mValues[2]>1)
			mValues[2] = 1;
		if(mValues[3]<0)
			mValues[3] = 0;
		if(mValues[3]>1)
			mValues[3] = 1;
	}

	/**
	 * Each value 0-255 
	 * */
	public static FloatColor fromIntRGB(int r, int g, int b) {
		return new FloatColor(r/255f, g/255f,  b/255f);
	}

}
