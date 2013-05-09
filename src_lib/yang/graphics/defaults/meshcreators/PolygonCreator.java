package yang.graphics.defaults.meshcreators;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;

public class PolygonCreator {

	public final static int ORIENTATION_CLOCKWISE = 1;
	public final static int ORIENTATION_BOTH = 0;
	public final static int ORIENTATION_COUNTERCLOCKWISE = -1;
	
	private float[] mPositions;
	private int[] mIndices;
	private int[] mWorkingIndices;
	private int[] mResultIndices;
	private int mPointCount;
	private int mIndexCount;
	private int mResultIndexCount;
	private int mPointsLeft;
	private int mElemsPerPos;
	private int mOrientation = 1;
	private boolean mAutoClose = true;
	
	public PolygonCreator(int elementsPerPosition,int capacity) {
		mElemsPerPos = elementsPerPosition;
		mPositions = new float[capacity*mElemsPerPos];
		mIndices = new int[capacity];
		mWorkingIndices = new int[capacity+1];
		mResultIndices = new int[capacity];
		clear();
	}
	
	public PolygonCreator(DefaultGraphics<?> graphics,int capacity) {
		this(graphics.mPositionDimension,capacity);
	}
	
	public void addPoint(float x,float y) {
		mPositions[mPointCount*mElemsPerPos] = x;
		mPositions[mPointCount*mElemsPerPos+1] = y;
		mIndices[mIndexCount++] = mElemsPerPos*mPointCount++;
	}
	
	public void addPointNoIndex(float x,float y) {
		mPositions[mPointCount*mElemsPerPos] = x;
		mPositions[mPointCount*mElemsPerPos+1] = y;
	}
	
	public void addIndex(int pointIndex) {
		mIndices[mIndexCount++] = pointIndex*mElemsPerPos;
	}
	
	public void triangulate() {
		System.arraycopy(mIndices, 0, mWorkingIndices, 0, mIndexCount);
		if(mAutoClose) {
			mWorkingIndices[mIndexCount] = mIndices[0];
		}
		mPointsLeft = mPointCount-2;
		mResultIndexCount = 0;
		while(mPointsLeft>0) {
			int earIndex1=0,earIndex2=0,earIndex3=0;
			int middleIndex=0;
			int i = 0;
			int startIndex = 0;
			while(true) {
				for(i=startIndex;i<mIndexCount;i++)
					if(mWorkingIndices[i]>=0) {
						earIndex1 = mWorkingIndices[i];
						break;
					}
				for(i=i+1;i<mIndexCount;i++)
					if(mWorkingIndices[i]>=0) {
						earIndex2 = mWorkingIndices[i];
						middleIndex = i;
						break;
					}
				earIndex3 = -1;
				for(i=i+1;i<mIndexCount;i++)
					if(mWorkingIndices[i]>=0) {
						earIndex3 = mWorkingIndices[i];
						break;
					}
				
				if(earIndex3==-1) {
					//No triangle found
					return;
				}
				
				startIndex = middleIndex;
				
				float x = mPositions[earIndex2];
				float y = mPositions[earIndex2+1];
				float aX = mPositions[earIndex1]-x;
				float aY = mPositions[earIndex1+1]-y;
				float bX = mPositions[earIndex3]-x;
				float bY = mPositions[earIndex3+1]-y;
				if((aX*bY-aY*bX)*mOrientation<=0) {
					//Concave, no triangle creation
					continue;
				}
				
				//Find intersecting points
				boolean intersected = false;
				for(int p=0;p<mPointCount;p++) {
					int pI = p*mElemsPerPos;

					if(pI!=earIndex1 && pI!=earIndex2 && pI!=earIndex3) {
						float pX = mPositions[pI] - x;
						float pY = mPositions[pI+1] - y;
						
						//LES: p = r*a + s*b
						float s = -1;
						float r = -1;
						if(aX!=0) {
							//Normalize row 1
							float raX = 1;
							float sbX = bX/aX;
							pX /= aX;
							// row2 - aY*row1
							float raY = 0;
							float sbY = bY - aY*sbX;
							pY -= aY*pX;
							if(sbY==0)
								break;
							s = pY/sbY;
							r = pX-s*sbX;
						}else{
							if(bX==0 || aY==0)
								break;
							s = pX/bX;
							r = (pY - s*bY)/aY;
						}
						if(s>0 && s<1 && r>0 && r<1) {
							intersected = true;
							break;
						}
					}
				}
				if(intersected)
					continue;
				
				//triangle ok
				break;
			}
			
			//remove corner
			mWorkingIndices[middleIndex] = -1;
			//put triangle indices
			
			mResultIndices[mResultIndexCount++] = earIndex1/mElemsPerPos;
			mResultIndices[mResultIndexCount++] = earIndex2/mElemsPerPos;
			mResultIndices[mResultIndexCount++] = earIndex3/mElemsPerPos;
					
			mPointsLeft--;
		}
	}
	
	public void putVertices(DefaultGraphics<?> graphics) {
		if(mResultIndexCount<0)
			return;
		IndexedVertexBuffer vertexBuffer = graphics.mCurrentVertexBuffer;
		
		int indexOffset = vertexBuffer.getCurrentVertexWriteCount();
		int c = 0;
		for(int index:mResultIndices) {
			if(c++>=mResultIndexCount)
				break;
			vertexBuffer.putIndex((short)(index+indexOffset));
		}
		
		vertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions, mPointCount*mElemsPerPos);
	}
	
	public void setOrientation(int orientation) {
		mOrientation = orientation;
	}

	public int getPointCount() {
		return mPointCount;
	}
	
	public int getIndexCount() {
		return mIndexCount;
	}
	
	public float getPosX(int pointNr) {
		return mPositions[pointNr*mElemsPerPos];
	}
	
	public float getPosY(int pointNr) {
		return mPositions[pointNr*mElemsPerPos+1];
	}

	public void clear() {
		mPointCount = 0;
		mIndexCount = 0;
		mResultIndexCount = -1;
	}

}
