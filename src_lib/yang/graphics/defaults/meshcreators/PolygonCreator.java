package yang.graphics.defaults.meshcreators;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;

public class PolygonCreator extends MeshCreator<DefaultGraphics<?>> {

	public final static int ORIENTATION_CLOCKWISE = 1;
	public final static int ORIENTATION_BOTH = 0;
	public final static int ORIENTATION_COUNTERCLOCKWISE = -1;
	
	private float[] mPositions;
	private int[] mIndices;
	private int[] mWorkingIndices;
	private int mPointCount;
	private int mIndexCount;
	private int mIndexOffset;
	private int mPointsLeft;
	private int mBytesPerPos;
	private int mOrientation = 1;
	
	public PolygonCreator(DefaultGraphics<?> graphics,int capacity) {
		super(graphics);
		mBytesPerPos = graphics.mPositionBytes;
		mPositions = new float[capacity*mBytesPerPos];
		mIndices = new int[capacity];
		mWorkingIndices = new int[capacity];
		clear();
	}
	
	public void addPoint(float x,float y) {
		mPositions[mPointCount*mBytesPerPos] = x;
		mPositions[mPointCount*mBytesPerPos+1] = y;
		mIndices[mIndexCount++] = mBytesPerPos*mPointCount++;
	}
	
	public void addPointNoIndex(float x,float y) {
		mPositions[mPointCount*mBytesPerPos] = x;
		mPositions[mPointCount*mBytesPerPos+1] = y;
	}
	
	public void addIndex(int pointIndex) {
		mIndices[mIndexCount++] = pointIndex*mBytesPerPos;
	}
	
	public void triangulate() {
		IndexedVertexBuffer vertexBuffer = mGraphics.mCurrentVertexBuffer;
		mIndexOffset = vertexBuffer.getCurrentIndexWriteCount();
		vertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions);

		System.arraycopy(mIndices, 0, mWorkingIndices, 0, mIndexCount);
		mPointsLeft = mPointCount-2;
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
					int pI = p*mBytesPerPos;

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
			vertexBuffer.putIndex((short)(mIndexOffset+earIndex1/2));
			vertexBuffer.putIndex((short)(mIndexOffset+earIndex2/2));
			vertexBuffer.putIndex((short)(mIndexOffset+earIndex3/2));
			
			mPointsLeft--;
		}
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
		return mPositions[pointNr*mBytesPerPos];
	}
	
	public float getPosY(int pointNr) {
		return mPositions[pointNr*mBytesPerPos+1];
	}

	public void clear() {
		mPointCount = 0;
		mIndexCount = 0;
	}

}
