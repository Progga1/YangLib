package yang.util.filereader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TokenReader {

	public static final int ERROR_INT = Integer.MIN_VALUE;
	public static final float ERROR_FLOAT = Float.MIN_VALUE;
	
	public BufferedReader mInputStream;
	public static int maxWordLength = 1024;

	public char[] mCharBuffer = new char[maxWordLength];
	public char mFstSpaceChar = '\0';
	//public boolean mLineBroken = false;
	public int mWordLength = 0;
	public int mLstRead;
	
	public TokenReader(InputStream stream) {
		mInputStream = new BufferedReader(new InputStreamReader(stream));
		mLstRead = 0;
	}
	
	public void skipSpace(boolean ignoreLineBreak) throws IOException {
		if(mLstRead<0)
			return;
		if(!ignoreLineBreak && mFstSpaceChar=='\n') {
			mCharBuffer[0] = '\n';
			return;
		}
			
		char c = '\0';
		int read;
		while((mLstRead=mInputStream.read())>=0) {
			c = (char) mLstRead;
			if(!ignoreLineBreak && c=='\n') {
				break;
			}else if(c!=' ' && c!='\t' && c!='\r') {
				mCharBuffer[0] = c;
				return;
			}
		}
		mCharBuffer[0] = c;
	}
	
	public void toLineEnd() throws IOException {
		if(mLstRead<0 || mFstSpaceChar=='\n' || mCharBuffer[0]=='\n') {
			mCharBuffer[0] = '\n';
			return;
		}
		char c = '\0';
		while((mLstRead=mInputStream.read())>=0) {
			c = (char) mLstRead;
			if(c=='\n') {
				mCharBuffer[0] = '\n';
				return;
			}
		}
	}
	
	public boolean nextWord(boolean ignoreLineEnd) throws IOException {
		skipSpace(ignoreLineEnd);
		if(mLstRead<0) {
			mWordLength = 0;
			mFstSpaceChar = '\0';
			return false;
		}
		if(mCharBuffer[0]=='\n') {
			mWordLength = 1;
			mFstSpaceChar = '\0';
			return true;
		}
		char c;
		int i=(mCharBuffer[0]=='\0')?0:1;
		while((mLstRead=mInputStream.read())>=0 && i<maxWordLength) {
			c = (char) mLstRead;
			if(c!='\r') {
				if(c==' ' || c=='\t' || c=='\n') {
					mFstSpaceChar = c;
					break;
				}else{
					mCharBuffer[i++] = c;
				}
			}
		}
		mWordLength = i;
		return i>=1;
	}
	
	public String wordToString() {
		return String.copyValueOf(mCharBuffer, 0, mWordLength);
	}
	
	public int wordToInt() {
		if(mWordLength<=0)
			return ERROR_INT;
		int result = 0;
		int i = mCharBuffer[0]=='-'?1:0;
		while(i<mWordLength) {
			int v = mCharBuffer[i]-'0';
			if(v<0 || v>9)
				return ERROR_INT;
			result = result*10 + v;
			i++;
		}
		if(mCharBuffer[0]=='-')
			return -result;
		else
			return result;
	}
	
	public float wordToFloat() {
		if(mWordLength<=0)
			return ERROR_FLOAT;
		float result = 0;
		int v = 0;
		int i = mCharBuffer[0]=='-'?1:0;
		char c = '\0';
		while(i<mWordLength) {
			c = mCharBuffer[i];
			v = c-'0';
			i++;
			if(c=='.')
				break;
			if(v<0 || v>9)
				return ERROR_FLOAT;
			result = result*10 + v;
		}
		if(c=='.') {
			float fac = 0.1f;
			while(i<mWordLength) {
				v = mCharBuffer[i]-'0';
				if(v<0 || v>9)
					return ERROR_FLOAT;
				result += v*fac;
				fac *= 0.1f;
				i++;
			}
		}
		if(mCharBuffer[0]=='-')
			return -result;
		else
			return result;
	}

	public boolean eof() throws IOException {
		return mLstRead<0;
	}

	public int readInt(boolean ignoreLineBreak) throws IOException {
		nextWord(ignoreLineBreak);
		return wordToInt();
	}
	
	public float readFloat(boolean ignoreLineBreak) throws IOException {
		nextWord(ignoreLineBreak);
		return wordToFloat();
	}

	public String readString(boolean ignoreLineBreak) throws IOException {
		nextWord(ignoreLineBreak);
		return wordToString();
	}
	
	public int pickWord(String[] words) {
		int i = -1;
		for(String word:words) {
			i++;
			if(word.length()!=mWordLength)
				continue;
			boolean eq = true;
			for(int j=0;j<mWordLength;j++) {
				if(mCharBuffer[j]!=word.charAt(j)) {
					eq = false;
					break;
				}
			}
			if(eq)
				return i;
		}
		return -1;
	}
}
