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
	public char[] mLineComments = "//".toCharArray();
	public boolean mAutoSkipComments = false;
	public boolean[] mWordBreakers = new boolean[256];
	public boolean[] mWhiteSpaces = new boolean[256];
	public char mFstSpaceChar = '\0';
	//public boolean mLineBroken = false;
	public int mWordLength = 0;
	public int mLstRead;
	public int mNumberPos = -1;

	public TokenReader(InputStream stream) {
		mInputStream = new BufferedReader(new InputStreamReader(stream));	//TODO crash on nexus
		mLstRead = 0;
		mWordBreakers[' '] = true;
		mWordBreakers['\n'] = true;
		mWordBreakers['\t'] = true;
		mWordBreakers['\r'] = true;
		mWhiteSpaces[' '] = true;
		mWhiteSpaces['\n'] = true;
		mWhiteSpaces['\t'] = true;
		mWhiteSpaces['\r'] = true;
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
			}else if(!mWhiteSpaces[c]) {
				mCharBuffer[0] = c;
				return;
			}
		}
		mCharBuffer[0] = c;
	}

	public void setLineCommentChars(String chars) {
		mLineComments = chars.toCharArray();
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
		mNumberPos = -1;
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
		boolean comment = mAutoSkipComments;
		int i = 0;
		if(mLstRead=='\0') {
			mLstRead = mInputStream.read();
		}
		while(mLstRead>=0 && i<maxWordLength) {
			c = (char) mLstRead;
			if(c!='\r') {
				if(mWordBreakers[c]) {
					mFstSpaceChar = c;
					break;
				}else{
					if(comment) {
						if(i>=mLineComments.length || c!=mLineComments[i])
							comment = false;
						else if(i>=mLineComments.length-1) {
							mFstSpaceChar = '\0';
							toLineEnd();
							return nextWord(ignoreLineEnd);
						}

					}
					mCharBuffer[i++] = c;
				}
			}
			mLstRead=mInputStream.read();
		}
		mWordLength = i;
		return i>=1;
	}

	public String wordToString() {
		return String.copyValueOf(mCharBuffer, 0, mWordLength);
	}

	public int wordToInt(int startAt,int defaultVal) {
		if(mWordLength-startAt<=0)
			return defaultVal;
		int result = 0;
		int i = startAt+(mCharBuffer[0]=='-'?1:0);
		while(i<mWordLength) {
			int v = mCharBuffer[i]-'0';
			if(v<0 || v>9) {
				if(i==startAt) {
					mNumberPos = i;
					return defaultVal;
				}else
					break;
			}
			result = result*10 + v;
			i++;
		}
		mNumberPos = i;
		if(mCharBuffer[0]=='-')
			return -result;
		else
			return result;
	}

	public float wordToFloat(int startAt,float defaultVal) {
		if(mWordLength-startAt<=0)
			return ERROR_FLOAT;
		float result = 0;
		int v = 0;
		int i = startAt+(mCharBuffer[0]=='-'?1:0);
		char c = '\0';
		while(i<mWordLength) {
			c = mCharBuffer[i];
			v = c-'0';
			if(c=='.')
				break;
			if(v<0 || v>9) {
				if(i==startAt) {
					mNumberPos = i;
					return defaultVal;
				}else
					break;
			}
			result = result*10 + v;
			i++;
		}
		if(c=='.') {
			i++;
			float fac = 0.1f;
			while(i<mWordLength) {
				v = mCharBuffer[i]-'0';
				if(v<0 || v>9)
					break;
				result += v*fac;
				fac *= 0.1f;
				i++;
			}
		}
		mNumberPos = i;
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
		return wordToInt(0,ERROR_INT);
	}

	public float readFloat(boolean ignoreLineBreak) throws IOException {
		nextWord(ignoreLineBreak);
		return wordToFloat(0,ERROR_FLOAT);
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

	public boolean isWord(String word) {
		if(word.length()!=mWordLength)
			return false;
		boolean eq = true;
		for(int j=0;j<mWordLength;j++) {
			if(mCharBuffer[j]!=word.charAt(j)) {
				return false;
			}
		}
		return true;
	}
}
