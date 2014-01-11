package yang.util.filereader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import yang.math.objects.Point3f;
import yang.util.filereader.exceptions.UnexpectedTokenException;

public class TokenReader {

	public static final int ERROR_INT = Integer.MIN_VALUE;
	public static final float ERROR_FLOAT = Float.MIN_VALUE;

	public static int maxWordLength = 1024;

	public BufferedReader mInputStream;

	//PROPERTIES
	public boolean mAutoSkipComments = false;
	public boolean mAutoHandleQuotationMarks = true;
	public boolean[] mWordBreakers = new boolean[256];
	public boolean[] mWhiteSpaces = new boolean[256];
	public char[] mLineComments = "//".toCharArray();

	//STATE
	public char[] mCharBuffer = new char[maxWordLength];
	public int mFstSpaceChar = '\0';
	public int mWordLength = 0;
	public int mLstRead;
	public int mNumberPos = -1;
	private int mCurLine;
	private int mCurColumn;
	private boolean mIsQuotationMarks = false;

	public TokenReader(InputStream stream) {
		reset(stream);
		mWordBreakers[' '] = true;
		mWordBreakers['\n'] = true;
		mWordBreakers['\t'] = true;
		mWordBreakers['\r'] = true;
		mWhiteSpaces[' '] = true;
		mWhiteSpaces['\n'] = true;
		mWhiteSpaces['\t'] = true;
		mWhiteSpaces['\r'] = true;
	}

	public void reset(InputStream stream) {
		mInputStream = new BufferedReader(new InputStreamReader(stream));	//TODO crash on nexus
		mLstRead = 0;
		mCurLine = 0;
		mCurColumn = 0;
	}

	private int nextChar() throws IOException {
		int c = mInputStream.read();
		if(c=='\n') {
			mCurColumn = 0;
			mCurLine++;
		}else
			mCurColumn++;
		mLstRead = c;
		return c;

	}

	public void skipSpace(boolean ignoreLineBreak) throws IOException {
		if(mLstRead<0)
			return;
		if(!ignoreLineBreak && mFstSpaceChar=='\n') {
			mCharBuffer[0] = '\n';
			return;
		}

		char c = '\0';
		while(nextChar()>=0) {
			c = (char) mLstRead;
			if(!ignoreLineBreak && c=='\n') {
				break;
			}else if(!mWhiteSpaces[c]) {
				//mCharBuffer[0] = c;
				return;
			}
		}
		//mCharBuffer[0] = c;
	}

	public void setLineCommentChars(String chars) {
		mLineComments = chars.toCharArray();
	}

	public void toLineEnd() throws IOException {
		if(mLstRead<0 || mFstSpaceChar=='\n' || mLstRead=='\n') {
			mCharBuffer[0] = '\n';
			mWordLength = 1;
			return;
		}
		char c = '\0';
		while(nextChar()>=0) {
			c = (char) mLstRead;
			if(c=='\n') {
				mCharBuffer[0] = '\n';
				mWordLength = 1;
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
		if(mLstRead=='\n') {
			mWordLength = 1;
			mFstSpaceChar = '\0';
			return true;
		}

		boolean comment = mAutoSkipComments;
		int c = mLstRead;
		if(c=='\0') {
			c = nextChar();
		}
		int i = 0;
		if(mAutoHandleQuotationMarks && c=='"') {
			//Quotation marks
			mIsQuotationMarks = true;
			while((c=nextChar())>=0 && i<maxWordLength) {
				if(c=='"')
					break;
				mCharBuffer[i++] = (char) c;
			}
			skipSpace(false);
//			if(mWordBreakers[mLstRead])
//				nextChar();
		}else{
			mIsQuotationMarks = false;

			while(c>=0 && i<maxWordLength) {
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
						mCharBuffer[i++] = (char) c;
					}
				}
				c = nextChar();
			}
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

	public boolean startsWith(String word) {
		int l = word.length();
		if(l>mWordLength)
			return false;
		for(int j=0;j<l;j++) {
			if(mCharBuffer[j]!=word.charAt(j)) {
				return false;
			}
		}
		return true;
	}

	public boolean isWord(String word) {
		if(word.length()!=mWordLength)
			return false;
		for(int j=0;j<mWordLength;j++) {
			if(mCharBuffer[j]!=word.charAt(j)) {
				return false;
			}
		}
		return true;
	}

	public boolean isSubString(String word, int offset) {
		int l = word.length();
		if(l+offset>mWordLength)
			return false;
		for(int j=0;j<l;j++) {
			if(mCharBuffer[j+offset]!=word.charAt(j)) {
				return false;
			}
		}
		return true;
	}

	public boolean endsWith(String word) {
		if(mWordLength<word.length())
			return false;
		else
			return isSubString(word, mWordLength-word.length());
	}

	public boolean isChar(char c) {
		return mWordLength==1 && mCharBuffer[0] == c;
	}

	public void expect(String expectedWord) throws UnexpectedTokenException, IOException {
		nextWord(true);
		if(!isWord(expectedWord))
			throw new UnexpectedTokenException(this,expectedWord);
	}

	public int getCurrentLine() {
		return mCurLine;
	}

	public int getCurrentColumn() {
		return mCurColumn;
	}

	public void skipWord(boolean ignoreLineEndings) throws IOException {
		nextWord(ignoreLineEndings);
	}

	public void skipWords(int count) throws IOException {
		for(int i=0;i<count;i++)
			skipWord(true);
	}

	public void readPoint3f(Point3f targetPoint) throws IOException {
		nextWord(true);
		targetPoint.mX = wordToFloat(0,0);
		nextWord(true);
		targetPoint.mY = wordToFloat(0,0);
		nextWord(true);
		targetPoint.mZ = wordToFloat(0,0);
	}

	@Override
	public String toString() {
		return ""+mCurLine+"-"+mCurColumn+" "+wordToString();
	}

}
