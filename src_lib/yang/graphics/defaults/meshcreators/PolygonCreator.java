package yang.graphics.defaults.meshcreators;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.DefaultGraphics;

public class PolygonCreator extends MeshCreator<Default2DGraphics> {

	private float[] mPositions;
	private int[] mIndices;
	private int[] mWorkingIndices;
	private int mPointCount;
	private int mIndexCount;
	private int mIndexOffset;
	private int mPointsLeft;
	
	public PolygonCreator(Default2DGraphics graphics,int capacity) {
		super(graphics);
		mPositions = new float[capacity];
		mIndices = new int[capacity];
		mWorkingIndices = new int[capacity];
		restart();
	}
	
	public void restart() {
		mPointCount = 0;
		mIndexCount = 0;
	}
	
	public void addPoint(float x,float y) {
		mPositions[mPointCount*2] = x;
		mPositions[mPointCount*2+1] = y;
		mIndices[mIndexCount++] = 2*mPointCount++;
	}
	
	public void putTriangulatedPositions() {
		IndexedVertexBuffer vertexBuffer = mGraphics.mCurrentVertexBuffer;
		mIndexOffset = vertexBuffer.getCurrentIndexWriteCount();
		vertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions);
		
		System.arraycopy(mIndices, 0, mWorkingIndices, 0, mIndexCount);
		mPointsLeft = mPointCount-2;
		while(mPointsLeft>0) {
			int earIndex1=0,earIndex2=0,earIndex3=0;
			int middleIndex=0,lastIndex=0;
			int i = 0;
			for(i=0;i<mIndexCount;i++)
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
			for(i=i+1;i<mIndexCount;i++)
				if(mWorkingIndices[i]>=0) {
					earIndex3 = mWorkingIndices[i];
					lastIndex = i;
					break;
				}
			float x = mPositions[earIndex2];
			float y = mPositions[earIndex2+1];
			float aX = mPositions[earIndex1]-x;
			float aY = mPositions[earIndex1+1]-y;
			float bX = mPositions[earIndex3]-x;
			float bY = mPositions[earIndex3+1]-y;
			if(aX*bY-aY*bX<0) {
				if(lastIndex==mIndexCount-1) {
					mWorkingIndices[lastIndex] = -1;
					mPointsLeft--;
				}
				continue;
			}
			
			mWorkingIndices[middleIndex] = -1;
			vertexBuffer.putIndex((short)(mIndexOffset+earIndex1/2));
			vertexBuffer.putIndex((short)(mIndexOffset+earIndex2/2));
			vertexBuffer.putIndex((short)(mIndexOffset+earIndex3/2));
			
			mPointsLeft--;
		}
	}

}
