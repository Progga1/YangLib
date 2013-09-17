package yang.graphics.defaults.meshcreators;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.math.Geometry;
import yang.math.MathFunc;

public class PolygonCreator {

	public final static int ORIENTATION_CLOCKWISE = 1;
	public final static int ORIENTATION_BOTH = 0;
	public final static int ORIENTATION_COUNTERCLOCKWISE = -1;
	
	private IndexedVertexBuffer mVertexBuffer;
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
	private boolean mAutoOrientation = true;
	public float mCurrentZ = 0;
	public boolean mAutoClose = false;
	
	public PolygonCreator(IndexedVertexBuffer vertexBuffer,int elementsPerPosition,int capacity) {
		mVertexBuffer = vertexBuffer;
		mElemsPerPos = elementsPerPosition;
		mPositions = new float[capacity*mElemsPerPos];
		mIndices = new int[capacity];
		mWorkingIndices = new int[capacity+1];
		mResultIndices = new int[capacity*3];
		clear();
	}
	
	public PolygonCreator(DefaultGraphics<?> graphics,int capacity) {
		this(graphics.getCurrentVertexBuffer(),graphics.mPositionDimension,capacity);
	}
	
	public void addPoint(float x,float y) {
		mPositions[mPointCount*mElemsPerPos] = x;
		mPositions[mPointCount*mElemsPerPos+1] = y;
		if(mElemsPerPos==3)
			mPositions[mPointCount*mElemsPerPos+2] = mCurrentZ;
		mIndices[mIndexCount++] = mElemsPerPos*mPointCount++;
	}
	
	public void addPointNoIndex(float x,float y) {
		mPositions[mPointCount*mElemsPerPos] = x;
		mPositions[mPointCount*mElemsPerPos+1] = y;
		if(mElemsPerPos==3)
			mPositions[mPointCount*mElemsPerPos+2] = mCurrentZ;
	}
	
	public void addIndex(int pointIndex) {
		mIndices[mIndexCount++] = mElemsPerPos*pointIndex;
	}
	
	public void setVertexBuffer(IndexedVertexBuffer targetBuffer) {
		mVertexBuffer = targetBuffer;
	}
	
	public void triangulate() {
		if(mAutoOrientation)
			setOrientationByPoints();
		int uIndexCount = mIndexCount;
		System.arraycopy(mIndices, 0, mWorkingIndices, 0, mIndexCount);
		if(mAutoClose) {
			uIndexCount++;
			mWorkingIndices[mIndexCount] = mIndices[0];
		}

		mPointsLeft = mIndexCount-2;
		mResultIndexCount = 0;
		while(mPointsLeft>0) {
			int earIndex1=0,earIndex2=0,earIndex3=0;
			int middleIndex=0;
			int i = 0;
			int startIndex = 0;
			while(true) {
				for(i=startIndex;i<uIndexCount;i++)
					if(mWorkingIndices[i]>=0) {
						earIndex1 = mWorkingIndices[i];
						break;
					}
				for(i=i+1;i<uIndexCount;i++)
					if(mWorkingIndices[i]>=0) {
						earIndex2 = mWorkingIndices[i];
						middleIndex = i;
						break;
					}
				earIndex3 = -1;
				for(i=i+1;i<uIndexCount;i++)
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
				if(mOrientation!=0 && (aX*bY-aY*bX)*mOrientation<=0) {
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
							float sbX = bX/aX;
							pX /= aX;
							
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
						if(s>=0 && s<=1 && r>=0 && r<=1 && r+s<=1) {
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
	
	public void putVertices() {
		if(mResultIndexCount<0)
			return;
		
		int indexOffset = mVertexBuffer.getCurrentVertexWriteCount();
		int c = 0;
		for(int index:mResultIndices) {
			if(c++>=mResultIndexCount)
				break;
			mVertexBuffer.putIndex((short)(index+indexOffset));
		}
		
		mVertexBuffer.putArray(DefaultGraphics.ID_POSITIONS, mPositions, mPointCount*mElemsPerPos);
	}
	
	public void drawTriangleLines(DefaultGraphics<?> graphics,float width) {
		graphics.mTranslator.bindTexture(null);
		for(int i=0;i<mResultIndexCount;i+=3) {
			float x1 = mPositions[mResultIndices[i]*mElemsPerPos];
			float y1 = mPositions[mResultIndices[i]*mElemsPerPos+1];
			float x2 = mPositions[mResultIndices[i+1]*mElemsPerPos];
			float y2 = mPositions[mResultIndices[i+1]*mElemsPerPos+1];
			float x3 = mPositions[mResultIndices[i+2]*mElemsPerPos];
			float y3 = mPositions[mResultIndices[i+2]*mElemsPerPos+1];
			graphics.drawLine(x1, y1, x2, y2, width);
			graphics.drawLine(x1, y1, x3, y3, width);
			graphics.drawLine(x2, y2, x3, y3, width);
			
			graphics.drawRectCentered(x2, y2, width*2.5f);
		}
	}
	
	public void setOrientation(int orientation) {
		mAutoOrientation = false;
		mOrientation = orientation;
	}
	
	public void setOrientationByPoints() {
		if(mPointCount<3)
			return;
		float topMost = -Float.MAX_VALUE*0.5f;
		int topI = -1;
		int l = mPointCount*mElemsPerPos;
		for(int i=0;i<l;i+=mElemsPerPos) {
			if(mPositions[i+1]>topMost) {
				topMost = mPositions[i+1];
				topI = i;
			}
		}
		float x = mPositions[topI];
		float y = mPositions[topI+1];
		float pX = topI==0?mPositions[l-mElemsPerPos]:mPositions[topI-mElemsPerPos];
		float pY = topI==0?mPositions[l-mElemsPerPos+1]:mPositions[topI-mElemsPerPos+1];
		float sX = topI==l-mElemsPerPos?mPositions[0]:mPositions[topI+mElemsPerPos];
		float sY = topI==l-mElemsPerPos?mPositions[1]:mPositions[topI+mElemsPerPos+1];
		if(y==sY && y==pY) {
			mOrientation = MathFunc.sign(sX-pX);
		}else{
			mOrientation = MathFunc.sign(Geometry.cross2D(sX-x, sY-y, pX-x, pY-y));
		}
	}
	
	public int getOrientation() {
		return mOrientation;
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
	
	public void setPointPos(int pointNr, float x,float y) {
		mPositions[pointNr*mElemsPerPos] = x;
		mPositions[pointNr*mElemsPerPos+1] = y;
	}

	public void clear() {
		mPointCount = 0;
		mIndexCount = 0;
		mResultIndexCount = -1;
	}
	
	public void putTextureCoordinates(float offsetX,float offsetY,float scaleX,float scaleY) {
		for(int i=0;i<mPointCount;i++) {
			mVertexBuffer.putVec2(DefaultGraphics.ID_TEXTURES, offsetX+mPositions[i*mElemsPerPos]*scaleX, offsetY+mPositions[i*mElemsPerPos+1]*scaleY);
		}
	}
	
	public void putTextureCoordinates(float scale) {
		putTextureCoordinates(0,0,scale,scale);
	}
	
	public void putColor(FloatColor color) {
		mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS,color.mValues, mPointCount);
	}
	
	public void putColor(float r,float g,float b,float a) {
		for(int i=0;i<mPointCount;i++) {
			mVertexBuffer.putVec4(DefaultGraphics.ID_COLORS, r,g,b,a);
		}
	}

	public int pickPoint(float x, float y, float radius) {
		float minDist = radius;
		int minDistIndex = -1;
		for(int i=0;i<mPointCount;i++) {
			float dX = mPositions[i*mElemsPerPos]-x;
			float dY = mPositions[i*mElemsPerPos+1]-y;
			float dist = (float)Math.sqrt(dX*dX+dY*dY);
			if(dist<minDist) {
				minDist = dist;
				minDistIndex = i;
			}
		}
		return minDistIndex;
	}
	
	public boolean interpenetrates(float x,float y) {

		for(int i=0;i<mResultIndexCount;) {
			float x1 = mPositions[mResultIndices[i]*mElemsPerPos];
			float y1 = mPositions[mResultIndices[i++]*mElemsPerPos+1];
			float x2 = mPositions[mResultIndices[i]*mElemsPerPos];
			float y2 = mPositions[mResultIndices[i++]*mElemsPerPos+1];
			float x3 = mPositions[mResultIndices[i]*mElemsPerPos];
			float y3 = mPositions[mResultIndices[i++]*mElemsPerPos+1];
			if(Geometry.interpenetratesTriangle(x,y, x1,y1, x2,y2, x3,y3))
				return true;
		}
		return false;
		
	}

}
