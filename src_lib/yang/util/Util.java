package yang.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Util {

	public static final class NanoSec {
		public static final long MICROSEC	= 1000L;
		public static final long MILLISEC	= 1000L * MICROSEC;
		public static final long SEC		= 1000L * MILLISEC;
	}

	public static String bufferToString(ByteBuffer byteBuffer,int size) {
		String res = "";
		final int savePos = byteBuffer.position();
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
		final int savePos = floatBuffer.position();
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
		final int savePos = intBuffer.position();
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
		final int savePos = shortBuffer.position();
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



	public static <T> String arrayToString(T[] array,String separator) {
		if(array==null)
			return "NULL_ARRAY";
		String result="";
		for(final T t:array) {
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
		for(final float t:array) {
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

	public static String arrayToString(short[] array,String separator,int spaceEvery) {
		//String result = "length="+array.length;
		int c = 0;
		String result="";
		for(final short t:array) {
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

	public static String arrayToString(float[][] array,String separator,int spaceEvery) {
		//String result = "length="+array.length;
		String result="";
		for(final float[] row:array) {
			int c = 0;
			String line = "";
			for(final float f:row) {
				if(line!="") {
					line += separator;
					if(spaceEvery>0 && (c%spaceEvery==0))
						line += " ";
				}
				c++;
				line += f;
			}
			if(result!="") {
				result += "\r\n";
			}
			result += line;
		}
		return result;
	}

	public static String arrayToString(float[][] array) {
		return arrayToString(array," ",0);
	}

	public static String arrayToString(int[] array,String separator,int spaceEvery) {
		//String result = "length="+array.length;
		int c = 0;
		String result="";
		for(final float t:array) {
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
		final String name = object.getClass().getName();
		final int start = name.lastIndexOf(".")+1;
		int end = name.indexOf("$");
		if(end<0)
			end = name.length();
		String result = name.substring(start,end);
		if(result.endsWith(ignoreEnding))
			result = result.substring(0,result.length()-ignoreEnding.length());
		return result;
	}

	public static String getClassName(Object object) {
		final String name = object.getClass().getName();
		final int start = name.lastIndexOf(".")+1;
		int end = name.indexOf("$");
		if(end<0)
			end = name.length();
		return name.substring(start,end);
	}

	public static String minLengthString(String string,int minChars,char fillChar) {
		while(string.length()<minChars)
			string = fillChar+string;
		return string;
	}

	public static String stringToLineNumbersString(String[] lines) {
		final StringBuilder result = new StringBuilder();
		for(int i=0;i<lines.length;i++) {
			result.append(minLengthString(""+i,3,' '));
			result.append(": ");
			result.append(lines[i]);
			result.append("\n");
		}
		return result.toString();
	}

	public static String stringToLineNumbersString(String string) {
		return stringToLineNumbersString(string.split("\n"));
	}

	public static int hexCharToInt(char hexChar) {
		if(hexChar>='0' && hexChar<='9')
			return hexChar-'0';
		else if(hexChar>='A' && hexChar<='F')
			return hexChar-'A'+10;
		else if(hexChar>='a' && hexChar<='f')
			return hexChar-'a'+10;
		else
			return -1;

	}

	public static <ElementType> ElementType[] resizeArray(ElementType[] array, ElementType[] newArray) {
		if(array==null)
			return newArray;
		else{
			System.arraycopy(array, 0, newArray, 0, Math.min(array.length,newArray.length));
			return newArray;
		}
	}

	//TODO very inefficient
	public static float wrapValueRepeat(float value, float minValue, float maxValue) {
		final float delta = maxValue-minValue;
		while(value>maxValue)
			value -= delta;
		while(value<minValue)
			value += delta;
		return value;
	}

	public static float wrapValueClamp(float value, float minValue, float maxValue) {
		if(value>maxValue)
			return maxValue;
		if(value<minValue)
			return minValue;
		else
			return value;
	}

	public static String intToBin(int value,int digits) {
		int c = 0;
		value = Math.abs(value);
		if(digits>31)
			digits = 31;
		final StringBuilder result = new StringBuilder(digits+4);
		for(int i=0;i<digits;i++) {
			result.append("0");
			c++;
			if(c%8==0)
				result.append(" ");
		}
		c = result.length()-1;
		for(int i=0;i<digits;i++) {
			if(result.charAt(c)==' ')
				c--;
			result.setCharAt(c,value%2==0?'0':'1');
			value /= 2;
			c--;
		}

		return result.toString();
	}

}
