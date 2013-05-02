package yang.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Util {

	public static final long ns	= 1L;
	public static final long us	= 1000L * ns;
	public static final long ms	= 1000L * us;
	public static final long s	= 1000L * ms;
	
	public static final float F_PI = (float)Math.PI;	
	
	public static String bufferToString(ByteBuffer byteBuffer,int size) {
		String res = "";
		int savePos = byteBuffer.position();
		byteBuffer.position(0);
		for(int i=0;i<size;i++) {
			if(i>0)
				res += " ";
			res += byteBuffer.get();
		}
		byteBuffer.position(savePos);
		return res;
	}
	
	public static String bufferToString(ByteBuffer byteBuffer) {
		return bufferToString(byteBuffer,byteBuffer.capacity());
	}
	
	public static String bufferToString(FloatBuffer floatBuffer,int size) {
		String res = "";
		int savePos = floatBuffer.position();
		floatBuffer.position(0);
		for(int i=0;i<size;i++) {
			if(i>0)
				res += " ";
			res += floatBuffer.get();
		}
		floatBuffer.position(savePos);
		return res;
	}
	
	public static String bufferToString(FloatBuffer floatBuffer) {
		return bufferToString(floatBuffer,floatBuffer.position());
	}
	
	public static String bufferToString(IntBuffer intBuffer,int size) {
		String res = "";
		int savePos = intBuffer.position();
		intBuffer.position(0);
		for(int i=0;i<size;i++) {
			if(i>0)
				res += " ";
			res += intBuffer.get();
		}
		intBuffer.position(savePos);
		return res;
	}
	
	public static String bufferToString(IntBuffer intBuffer) {
		return bufferToString(intBuffer,intBuffer.position());
	}
	
	public static String bufferToString(ShortBuffer shortBuffer,int size) {
		String res = "";
		int savePos = shortBuffer.position();
		shortBuffer.position(0);
		for(int i=0;i<size;i++) {
			if(i>0)
				res += " ";
			res += shortBuffer.get();
		}
		shortBuffer.position(savePos);
		return res;
	}
	
	public static String bufferToString(ShortBuffer shortBuffer) {
		return bufferToString(shortBuffer,shortBuffer.position());
	}
	
	public static String intArrayToString(int[] array) {
		String res = "";
		for(int i=0;i<array.length;i++){
			if(i>0)
				res += ", ";
			res += array[i];
		}
		return res;
	}
	
	public static float round(float value,int factor) {
		return ((int)(value*factor))/(float)factor;
	}

	public static float getDistance(float posX1, float posY1, float posX2, float posY2) {
		return (float)Math.sqrt((posX2-posX1)*(posX2-posX1) + (posY2-posY1)*(posY2-posY1));
	}
	
	public static double getDistance(double posX1, double posY1, double posX2, double posY2) {
		return Math.sqrt((posX2-posX1)*(posX2-posX1) + (posY2-posY1)*(posY2-posY1));
	}
	
	public static float getDistance(float deltaX, float deltaY) {
		return (float)Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	public static double getDistance(double deltaX, double deltaY) {
		return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	public static float cross2D(float x1,float y1,float x2,float y2) {
		return y1*x2 - x1*y2;
	}
	
	public static float getAngleDown(float distance,float dX,float dY) {
		if(distance==0)
			return 0;
		if(dX>0)
			return (float)Math.acos(-dY/distance);
		else
			return -(float)Math.acos(-dY/distance);
	}
	
	public static float getAngle(float dX,float dY) {
		if(dX==0 && dY==0)
			return 0;
		double distance = Math.sqrt(dX*dX + dY*dY);
		if(dY>0)
			return (float)Math.acos(dX/distance);
		else
			return -(float)Math.acos(dX/distance);
	}
	
	public static float getAngleDown(float deltaX,float deltaY) {
		return getAngleDown(getDistance(deltaX,deltaY),deltaX,deltaY);
	}
	
	public static float getAngleDown(float posX1, float posY1, float posX2, float posY2) {
		return getAngleDown(getDistance(posX1,posY1,posX2,posY2),posX2-posX1,posY2-posY1);
	}

	public static float rotateGetX(float x, float y, float anchorX, float anchorY, float angle) {
		return (float)((x-anchorX)*Math.cos(angle) + (y-anchorY)*Math.sin(angle) + anchorX);
	}
	
	public static float rotateGetY(float x, float y, float anchorX, float anchorY, float angle) {
		return (float)(-(x-anchorX)*Math.sin(angle) + (y-anchorY)*Math.cos(angle) + anchorY);
	}
	
	public static float abs(float x) {
		if(x<0)
			return -x;
		else 
			return x;
	}
	
	public static double abs(double x) {
		if(x<0)
			return -x;
		else 
			return x;
	}
	
	public static float sqr(float x) {
		return x*x;
	}

	public static int sign(float x) {
		if(x>=0)
			return 1;
		else
			return -1;
	}
	
	public static int signZero(float x) {
		if(x==0)
			return 0;
		if(x>0)
			return 1;
		else
			return -1;
	}

	public static boolean pointInRect(float pointX,float pointY, float rectLeft,float rectTop,float rectRight,float rectBottom) {
		return pointX>rectLeft && pointX<rectRight && pointY<rectTop && pointY>rectBottom;
	}
	
	public static int random(int minX,int maxX) {
		return (int)(minX+Math.random()*(maxX-minX));
	}
	
	public static float random(float minX,float maxX) {
		return (float)(minX+Math.random()*(maxX-minX));
	}
	
	public static double random(double minX,double maxX) {
		return minX+Math.random()*(maxX-minX);
	}
	
	public static boolean circleCollision(float deltaX,float deltaY,float radiusSum) {
		return deltaX*deltaX + deltaY*deltaY <= radiusSum*radiusSum;
	}

	public static float getDistance(float deltaX, float deltaY, float deltaZ) {
		return (float)Math.sqrt(deltaX*deltaX+deltaY*deltaY+deltaZ*deltaZ);
	}

	public static <T> String arrayToString(T[] array,String separator) {
		//String result = "length="+array.length;
		if(array==null)
			return "NULL_ARRAY";
		String result="";
		for(T t:array) {
			if(result!="")
				result += separator;
			result += t;
		}
		return result;
	}
	
	public static <T> String arrayToString(T[] array) {
		return arrayToString(array,"\n");
	}
	
	public static String arrayToString(float[] array,String separator,int spaceEvery) {
		//String result = "length="+array.length;
		int c = 0;
		String result="";
		for(float t:array) {
			if(result!="") {
				result += separator;
				if(spaceEvery>0 && (c%spaceEvery==0))
					result += " ";
			}
			c++;
			result += t;
		}
		return result;
	}
	
	public static String getClassName(Object object,String ignoreEnding) {
		String name = object.getClass().getName();
		int start = name.lastIndexOf(".")+1;
		int end = name.indexOf("$");
		if(end<0)
			end = name.length();
		String result = name.substring(start,end);
		if(result.endsWith(ignoreEnding))
			result = result.substring(0,result.length()-ignoreEnding.length());
		return result;
	}

	public static String getClassName(Object object) {
		String name = object.getClass().getName();
		int start = name.lastIndexOf(".")+1;
		int end = name.indexOf("$");
		if(end<0)
			end = name.length();
		return name.substring(start,end);
	}

	public static float getDistance(float[] vector) {
		float result = 0;
		for(float comp:vector) {
			result += comp*comp;
		}
		return (float)Math.sqrt(result);
	}
	
	public static float linInterpolate(float[] array, float position) {
		if(array.length==1)
			return array[0];
		int intPos = (int)position;
		float weight = position-intPos;
		return array[intPos]*(1-weight) + array[(intPos+1)%array.length]*weight;
	}
	
	/**
	 * line major
	 */
	public static float bilinInterpolate(float[][] array, float line,float column) {
		if(array.length==1)
			return linInterpolate(array[0],column);
		int intLine = (int)line;
		float weight = line-intLine;
		return linInterpolate(array[intLine],column)*(1-weight) + linInterpolate(array[(intLine+1)%array.length],column)*weight;
	}
	
	public static float linInterpolate(float[] array, float position,int stride,int offset) {
		if(array.length==1)
			return array[0];
		int intPos = (int)position;
		float weight = position-intPos;
		int col = intPos*stride+offset;
		return array[col]*(1-weight) + array[(col+stride)%array.length]*weight;
	}
	
	public static float bilinInterpolate(float[][] array, float line,float column,int columnStride,int columnOffset) {
		if(array.length==1)
			return linInterpolate(array[0],column);
		int intLine = (int)line;
		float weight = line-intLine;
		return linInterpolate(array[intLine],column,columnStride,columnOffset)*(1-weight) + linInterpolate(array[(intLine+1)%array.length],column,columnStride,columnOffset)*weight;
	}
	
}
