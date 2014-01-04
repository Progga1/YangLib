package yang.graphics.tail;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.geometrycreators.DoubleStripCreator;
import yang.graphics.defaults.geometrycreators.StripCreator;
import yang.math.Geometry;
import yang.model.DebugYang;
import yang.util.Util;

public class Tail {

	private final float[] mStdTexCoords = { 0, 0, 0, 2};
	private final float[] mDoubleTexCoords = { 0, 0, 0, 2, 0, 1};

	public String mName;
	public int mCapacity;
	private boolean mFilled;
	private int mRingPos;
	public float[] mPosX;
	public float[] mPosY;
	public float[] mDirX;
	public float[] mDirY;
	public float[] mDist;
	public int[] mCounts;

	private float[] mTexCoords;
	private final float[] mCurColor;
	public float mGlobAlpha;
	private int mCountRingPos;
	private int mUpdateCount;
	private int mCreateNodeFreq;
	private int mPrevIndex;
	private float mLastNodeX;
	private float mLastNodeY;
	private float mCurNormX;
	private float mCurNormY;
	public float mWidth;
	private final int mCountLength;
	private float mScaleFallOff;
	public float mMinDist;
	public float mMinScalarDist;
	public float mMinScalar;
	private boolean mInverted;
	private final boolean mSubTails;
	private final DefaultGraphics<?> mGraphics;
	public float mSuppData[];
	public int mTexXRepeat = 0;
	protected float mTexYRepeat = 2;
	public float mTexXShift = 0;
	public boolean mAutoInterruptSmallDistances = true;
	public StripCreator mStrips;
	public StripCreator mSingleStrips,mDoubleStrips;
	public float mMaxWidthAtCount = 5;

	public Tail(DefaultGraphics<?> graphics,int capacity,boolean subTails) {
		mGraphics = graphics;
		mSingleStrips = new StripCreator(graphics);
		mDoubleStrips = new DoubleStripCreator(graphics);
		setDoubled(false);
		mCapacity = capacity;
		mCountLength = capacity/2;
		mPosX = new float[capacity];
		mPosY = new float[capacity];
		mDirX = new float[capacity];
		mDirY = new float[capacity];
		mDist = new float[capacity];
		mCounts = new int[mCountLength];
		mCurColor = new float[4];
		setColor(1,1,1,1);
		mWidth = 0.25f;
		mMinDist = 0.01f;
		mMinScalarDist = 0.01f;
		mCreateNodeFreq = 1;
		mSuppData = new float[]{0,0,0,0};
		mInverted = false;
		mSubTails = subTails;
		mScaleFallOff = 0.25f;
		mMinScalar = 0.3f;
		clear();
	}

	public void setDoubled(boolean doubled) {
		if(doubled) {
			mStrips = mDoubleStrips;
			mTexCoords = mDoubleTexCoords;
		}else{
			mStrips = mSingleStrips;
			mTexCoords = mStdTexCoords;
		}
	}

	public boolean isDoubled() {
		return mStrips==mDoubleStrips;
	}

	public void clear() {
		synchronized(this) {
			mFilled = false;
			mRingPos = 0;
			mUpdateCount = 0;
			mCountRingPos = 0;
		}
	}

	public void setTexYRepeat(float repeat) {
		mTexYRepeat = repeat;
		mStdTexCoords[3] = repeat;
		mDoubleTexCoords[3] = repeat;
	}

	public void setColor(float r,float g,float b,float a) {
		mCurColor[0] = r;
		mCurColor[1] = g;
		mCurColor[2] = b;
		mCurColor[3] = a;
		mGlobAlpha = a;
	}

	public void setColor(float r,float g,float b) {
		setColor(r,g,b,1);
	}

	public void setColor(float brightness) {
		setColor(brightness,brightness,brightness);
	}

	public void interruptTail() {
		synchronized(this) {
			if(mCounts[mCountRingPos]!=0) {
				mCountRingPos++;
				if(mCountRingPos>=mCountLength)
					mCountRingPos = 0;
				mCounts[mCountRingPos] = 0;
				mPrevIndex = -2;
			}
		}
	}

	public String debugOut() {
		return Util.arrayToString(mCounts, ";", 0);
	}

	private void addNode(float dist,float x,float y,float forceDirX,float forceDirY) {

		boolean firstNode;
		if(dist<0) {
			firstNode = true;
			dist=0;
		}else
			firstNode = false;

		synchronized(this) {
			boolean newNode;

			if(mCreateNodeFreq>0) {
				newNode = (mUpdateCount%mCreateNodeFreq == 0 || (!mFilled && mRingPos<=1));
			}else
				newNode = true;

			boolean addNodeOk = newNode;
			if(newNode) {
				final int counts = mCounts[mCountRingPos];

				boolean noDistCheck = false;
				//Check scalar
				if(mPrevIndex>-1 && counts>1 && mMinScalar>-1 && dist>mMinScalarDist && (forceDirX!=0 || forceDirY!=0)) {
					final float scalar = mDirX[mPrevIndex]*forceDirX+mDirY[mPrevIndex]*forceDirY;
					if(scalar<mMinScalar) {
						mCounts[mCountRingPos]--;
						interruptTail();
						mCounts[mCountRingPos]+=2;
						noDistCheck = true;

					}
				}

				if(!noDistCheck) {
					if(dist>mMinDist || firstNode) {
						//Continue sub tail
						if(counts<0) {
							interruptTail();
							mCounts[mCountRingPos]++;
						}else{

							if(mCounts[mCountRingPos]<mCapacity)
								mCounts[mCountRingPos]++;
						}

					}else{
						//New sub tail
						if(mAutoInterruptSmallDistances) {
							if(counts>0) {
								//mCounts[mCountRingPos]--;
								interruptTail();
								mCounts[mCountRingPos]--;
							}else{
								if(mCounts[mCountRingPos]>-mCapacity) {
									mCounts[mCountRingPos]--;
								}
							}
						}else
							addNodeOk = false;
					}
				}


			}

			if(addNodeOk) {

				if(mPrevIndex>-1 && (forceDirX!=0 || forceDirY!=0) ) {
					float dirX = (mDirX[mPrevIndex]+forceDirX)*0.5f;
					float dirY = (mDirY[mPrevIndex]+forceDirY)*0.5f;
					final float dirDist = Geometry.getDistance(dirX, dirY);
					if(dirDist!=0) {
						dirX /= dirDist;
						dirY /= dirDist;
					}
					mDirX[mPrevIndex] = dirX;
					mDirY[mPrevIndex] = dirY;
//					mDirX[mPrevIndex] = (mDirX[mPrevIndex]+forceDirX)*0.5f;
//					mDirY[mPrevIndex] = (mDirY[mPrevIndex]+forceDirY)*0.5f;
				}

				if(mPrevIndex<-1)
					mPrevIndex++;
				else
					mPrevIndex = mRingPos;
				mRingPos++;
				if(mRingPos>=mCapacity) {
					mRingPos = 0;
					mFilled = true;
				}

				mLastNodeX = x;
				mLastNodeY = y;
			}

			mDist[mRingPos] = dist;
			mPosX[mRingPos] = x;
			mPosY[mRingPos] = y;
			mDirX[mRingPos] = forceDirX;
			mDirY[mRingPos] = forceDirY;

			mUpdateCount++;
		}
	}

	private float getDistance(float x,float y) {
		float dx;
		float dy;
		float dist;
		dx = x - mLastNodeX;
		dy = y - mLastNodeY;
		dist = (float)Math.sqrt(dx*dx + dy*dy);

		if(dist!=0) {
			mCurNormX = dx / dist;
			mCurNormY = dy / dist;
		}else{
//			mCurNormX = 0;
//			mCurNormY = 0;
		}

		return dist;
	}

	public void refreshFront(float x,float y,float forceDirX,float forceDirY) {
		addNode(getDistance(x,y),x,y,forceDirX,forceDirY);
	}

	public void refreshFront() {
		refreshFront(mPosX[mRingPos],mPosY[mRingPos]);
	}

	public void refreshFront(float x,float y) {

		if(!mFilled && mRingPos==0) {
			addNode(-1,x,y,0,0);
		}else{
			final float dist = getDistance(x,y);
			addNode(dist,x,y,mCurNormY,-mCurNormX);
		}
	}

	public void refreshFrontAbsolute(float x1,float y1,float x2,float y2) {
		final float dx = (x2-x1)*0.5f;
		final float dy = (y2-y1)*0.5f;
		refreshFront(x1+dx,y1+dy,dx,dy);
	}

	private int putPos;
	private int countPos;

	public int getTailLength() {
		if(mFilled)
			return mCapacity;
		else
			return mRingPos;
	}

	protected int initTailDraw() {
		final int l = getTailLength();
		if(l<=1)
			return l;
		countPos = mCountRingPos;
		putPos = mRingPos;
		return getTailLength();
	}

	private float curTailTexX;
	private float tailTexXStep;

	protected void putTailVertices(float scale,float alpha) {
		if(putPos<0) {
			putPos = mCapacity-1;
		}
		mCurColor[3] = mGlobAlpha * alpha;
		//mCurColor[3] = 1;
		if(scale==0) {
			mStrips.continueStripSinglePoint(mPosX[putPos], mPosY[putPos]);
			if(mTexXRepeat==0)
				mGraphics.putTextureCoord(0, 1);
			else
				mGraphics.putTextureCoord(curTailTexX, 0);
		}else{
			mStrips.continueStrip(mPosX[putPos]+mDirX[putPos]*scale, mPosY[putPos]+mDirY[putPos]*scale,mPosX[putPos]-mDirX[putPos]*scale, mPosY[putPos]-mDirY[putPos]*scale);
			if(mTexXRepeat==0)
				mGraphics.putTextureArray(mTexCoords);
			else{
				mGraphics.putTextureCoord(curTailTexX, 0);
				mGraphics.putTextureCoord(curTailTexX, mTexYRepeat);
				if(mStrips==mDoubleStrips)
					mGraphics.putTextureCoord(curTailTexX, mDoubleTexCoords[5]);
			}
			mGraphics.putColor(mCurColor);
			mGraphics.putSuppData(mSuppData);
			if(mStrips==mDoubleStrips) {
				mGraphics.putColor(mCurColor);
				mGraphics.putSuppData(mSuppData);
			}
		}
		mGraphics.putColor(mCurColor);
		mGraphics.putSuppData(mSuppData);
		curTailTexX += tailTexXStep;
		putPos--;
	}

	protected void skipTailVertices() {
		putPos--;
		if(putPos<0) {
			putPos = mCapacity-1;
		}
	}

	public void drawWholeTail() {
		if(!DebugYang.drawTails)
			return;

		if(mTexXRepeat>0) {
			tailTexXStep = 1f/mTexXRepeat;
			curTailTexX = mTexXShift * tailTexXStep;
		}

		synchronized(this) {
			final int tSize = initTailDraw();
			if(tSize<=1)
				return;
			int c = 0;
			int count = 0;
			float alpha;
			float scale;
			while(true) {
				if(c>=tSize-1)
					break;
				final int curCount = mCounts[countPos];

				if(curCount<0) {
					skipTailVertices();
					count++;
					if(-count<=curCount) {
						countPos--;
						if(countPos<0)
							countPos = mCountLength-1;
						count = 0;
					}
				}else {
					if(curCount>2) {
						float countFac;
						if(curCount<mMaxWidthAtCount)
							countFac = curCount/mMaxWidthAtCount;
						else
							countFac = 1;
						float tGlobal,tSub;
						if(mInverted) {
							tGlobal = 1-(float)c/(tSize-1);
							tSub = 1-(float)count/(curCount-1);
						}else{
							tGlobal = (float)c/(tSize-1);
							tSub = (float)count/(curCount-1);
						}
						if(count==0)
							mStrips.startStrip();
						float subFac;
						if(mSubTails) {
							final float sqr = ((float)Math.pow(tSub,0.7f)*2-1);
							if(sqr*sqr>1)
								System.err.println("sqr = "+sqr);
							subFac = (float)Math.sqrt(1-sqr*sqr) * countFac;
						}else
							subFac = 1;
						scale = (1 - tGlobal * mScaleFallOff) * subFac;
						alpha = (1-tGlobal) * subFac;

						putTailVertices(scale*mWidth,alpha);
					}else{
						if(curCount>0)
							skipTailVertices();
					}
					count++;
					if(count>=curCount) {
						countPos--;
						if(countPos<0)
							countPos = mCountLength-1;
						count = 0;
					}
				}
				c++;
			}
		}
	}

	public void setWidth(float width) {
		mWidth = width;
	}

	public void createNodeEveryNthStep(int n) {
		mCreateNodeFreq = n;
	}

	public void setInverted(boolean invert) {
		mInverted = invert;
	}

	public void setScaleFallOff(float scaleFallOff) {
		mScaleFallOff = scaleFallOff;
	}

	public void setAlpha(float alpha) {
		mGlobAlpha = alpha;
	}

	public void setTexShiftBySize() {
		mTexXShift = -getTailLength()%mTexXRepeat;
	}

}
