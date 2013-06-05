package yang.graphics.font;

import yang.util.NonConcurrentList;
import yang.util.Util;

public class FixedString {

	public static char DOT_CHAR = '.';
	
	public int mCapacity;
	public int mLength;
	public int[] mChars;
	public int mMarker;
	public MarkInfo[] mFormatStringMarks;
	
	public FixedString() {
		
	}
	
	public FixedString(int capacity) {
		allocString(capacity);
	}
	
	public FixedString allocString(int capacity) {
		mCapacity = capacity;
		mChars = new int[capacity];
		mMarker = 0;
		mLength = 0;
		return this;
	}
	
	public FixedString allocString(String initialString, int capacity) {
		allocString(capacity);
		appendString(initialString);
		return this;
	}
	
	public FixedString allocString(String string) {
		return allocString(string,string.length());
	}
	
	public static boolean isDigit(char character) {
		return character>='0' && character<='9';
	}
	
	protected void startFormatStringParse() {
		
	}
	
	protected void handleMacro(String macro,int curPos,int lastMacro) {
		
	}
	
	protected void endFormatStringParse(int pos,int lastMacro) {
		
	}
	
	public FixedString allocFormatString(String formatString) {
		int markerCount = 0;
		boolean escaped = false;
		NonConcurrentList<MarkInfo> markList = new NonConcurrentList<MarkInfo>();
		int charCount = 0;
		
		for(int i=0;i<formatString.length();i++) {
			if(escaped) {
				escaped = false;
				charCount++;
			}else{
				if(formatString.charAt(i)=='\\')
					escaped = true;
				else{
					if(formatString.charAt(i)=='%') {
						markerCount++;
						i++;
						int preI = i;
						while(i<formatString.length() && isDigit(formatString.charAt(i)))
							i++;
						int alloc = Integer.parseInt(formatString.substring(preI, i));
						markList.add(new MarkInfo(charCount,alloc));
						charCount+=alloc;
						if(i<formatString.length())
							i--;
					}else if(formatString.charAt(i)=='[') {
						int p = formatString.indexOf("]", i+1);
						if(p<=i)
							p = formatString.length()-1;
						i = p;
					}else{
						charCount++;
					}
				}
			}
		}
		
		mFormatStringMarks = new MarkInfo[markerCount];
		int c=0;
		for(MarkInfo markInfo:markList) {
			mFormatStringMarks[c] = markInfo;
			c++;
		}

		allocString(charCount);
		
		int markInfoIndex = 0;
		int lstMacro = -1;
		startFormatStringParse();
		c=0;
		int charPos = 0;
		for(int i=0;i<charCount;i++) {
			if(escaped) {
				escaped = false;
				if(formatString.charAt(c)=='%')
					mChars[i] = '%';
				if(formatString.charAt(c)=='\\')
					mChars[i] = '\\';
				c++;
			}else{
				if(formatString.charAt(c)=='\\'){
					escaped = true;
					c++;
					i--;
				}else{
					if(formatString.charAt(c)=='%') {
						int count = mFormatStringMarks[markInfoIndex++].mLength;
						for(int k=0;k<count;k++)
							mChars[i++] = 0;
						i--;
						while(++c<formatString.length() && isDigit(formatString.charAt(c)));
						
					}else if(formatString.charAt(c)=='[') {
						int p = formatString.indexOf("]", c+1);
						if(p<=c)
							p = formatString.length()-1;
						String macro = formatString.substring(c+1,p);
						
						handleMacro(macro,i,lstMacro);
						c = p+1;
						lstMacro = i;
						i--;
					}else{
						char ch = formatString.charAt(c++);
						mChars[i] = ch;
//						if(ch!=' ' && ch!='\t' && ch!='\n')
//							charPos++;
					}
					
				}
			}
		}
		mLength = charCount;
		mMarker = mLength;
		
		endFormatStringParse(charCount,lstMacro);
		
		//System.out.println(mLength+" "+Util.arrayToString(mFormatStringMarks));
		return this;
	}
	
	public void reset() {
		mMarker = 0;
		mLength = 0;
	}
	
	public void setString(String string) {
		mMarker = 0;
		appendString(string);
		truncAtMarker();
	}
	
	public void setInt(int value) {
		mMarker = 0;
		appendInt(value,-1);
		truncAtMarker();
	}
	
	public void setFloat(float value,int fracDigits) {
		mMarker = 0;
		appendFloat(value,fracDigits,-1);
		truncAtMarker();
	}
	
	public void appendString(String string,int minCharacterCount) {
		int l= string.length();
		for(int i=0;i<l;i++) {
			mChars[mMarker+i] = string.charAt(i);
		}
		mMarker += l;
		if(minCharacterCount>0) {
			minCharacterCount-=l;
			for(int i=0;i<minCharacterCount;i++)
				mChars[mMarker++] = 0;
		}
		if(mMarker>=mLength)
			mLength = mMarker;
	}
	
	public void appendString(String string) {
		int l= string.length();
		for(int i=0;i<l;i++) {
			mChars[mMarker+i] = string.charAt(i);
		}
		mMarker += l;
		if(mMarker>=mLength)
			mLength = mMarker;
	}
	
	public void appendStringAtMark(int markIndex,String string) {
		setMarker(mFormatStringMarks[markIndex].mPos);
		appendString(string,mFormatStringMarks[markIndex].mLength);
	}
	
	public void appendInt(int value,int minCharacterCount) {
		int c=0;
		if(value==0) {
			mChars[mMarker++] = '0';
			c = 1;
		}else{
			if(value<0) {
				mChars[mMarker++] = '-';
				value = -value;
			}
			
			while(value>0) {
				mChars[mMarker+c] = '0'+value%10;
				value/=10;
				c++;	
			}
			//Swap
			for(int i=0;i<c/2;i++) {
				int h=mChars[mMarker+i];
				mChars[mMarker+i] = mChars[mMarker+c-i-1];
				mChars[mMarker+c-i-1] = h;
			}
			mMarker+=c;
		}
		if(minCharacterCount>0) {
			minCharacterCount-=c;
			for(int i=0;i<minCharacterCount;i++)
				mChars[mMarker++] = 0;
		}
		
		if(mMarker>mLength)
			mLength = mMarker;
	}
	
	public void appendIntAtMark(int markIndex,int value) {
		setMarker(mFormatStringMarks[markIndex].mPos);
		appendInt(value,mFormatStringMarks[markIndex].mLength);
	}
	
	public void appendFloat(float value,int fracDigits,int minCharacterCount) {

		int fac = 1;
		for(int i=0;i<fracDigits;i++)
			fac*=10;
		value *= fac;
		int intVal = (int)(value+0.4999999f);
		
		if(intVal<0) {
			mChars[mMarker++] = '-';
			intVal = -intVal;
		}
		
		int c=0;
		int lastVal = 0;
		if(intVal<fac) {
			intVal += fac;
			mChars[mMarker++] = '0';
			lastVal = 1;
		}
		
		while(intVal>lastVal) {
			mChars[mMarker+c] = '0'+intVal%10;
			intVal/=10;
			c++;
			if(c==fracDigits) {
				mChars[mMarker+ c] = DOT_CHAR;
				c++;
			}
				
			
				
		}
		//Swap
		for(int i=0;i<c/2;i++) {
			int h=mChars[mMarker+i];
			mChars[mMarker+i] = mChars[mMarker+c-i-1];
			mChars[mMarker+c-i-1] = h;
		}
		mMarker+=c;	
		
		if(minCharacterCount>0) {
			minCharacterCount-=c+1;
			for(int i=0;i<minCharacterCount;i++)
				mChars[mMarker++] = 0;
		}
		
		if(mMarker>mLength)
			mLength = mMarker;
	}
	
	public void appendFloatAtMark(int markIndex,float value,int fracDigits) {
		setMarker(mFormatStringMarks[markIndex].mPos);
		appendFloat(value,fracDigits,mFormatStringMarks[markIndex].mLength);
	}
	
	public void setMarker(int position) {
		mMarker = position;
	}
	
	public void recalculateLength() {
		for(int i=0;i<mCapacity;i++) {
			if(mChars[i]==0) {
				mLength = i;
				return;
			}
		}
		mLength = mCapacity;
	}
	
	public String createRawString() {
		StringBuilder result = new StringBuilder(mLength);
		for(int i=0;i<mLength;i++) {
			int val = mChars[i];
			if(val>0 && val<255)
				result.append((char)val);
			else
				result.append('['+val+']');
		}
		return result.toString();
	}
	
	public String bufferToString() {
		StringBuilder result = new StringBuilder();
		for(int i=0;i<mCapacity;i++) {
			if(i==0)
				result.append(" ");
			result.append(mChars[i]);
		}
		return result.toString();
	}
	
	public String toString() {
		return "Mark/Len/Cap="+mMarker+"/"+mLength+"/"+mCapacity+"; String="+createRawString();
	}
	
	public FixedString truncAtMarker() {
		if(mMarker<mCapacity)
			mChars[mMarker] = 0;
		mLength = mMarker;
		return this;
	}
	
	public void appendZeros(int count) {
		for(int i=0;i<count;i++)
			mChars[mMarker++] = 0;
	}
	
	public void appendZerosUntil(int index) {
		while(mMarker<=index)
			mChars[mMarker++] = 0;
	}
	
}
