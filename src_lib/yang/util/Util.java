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



	public static <T> String arrayToString(T[] array,String separator) {
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
	
	public static String arrayToString(short[] array,String separator,int spaceEvery) {
		//String result = "length="+array.length;
		int c = 0;
		String result="";
		for(short t:array) {
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
		for(float[] row:array) {
			int c = 0;
			String line = "";
			for(float f:row) {
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
	
	public static String minLengthString(String string,int minChars,char fillChar) {
		while(string.length()<minChars)
			string = fillChar+string;
		return string;
	}

	public static String stringToLineNumbersString(String[] lines) {
		StringBuilder result = new StringBuilder();
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
	
}
