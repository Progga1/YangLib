package yang.graphics.buffers;

import yang.model.Rect;


public abstract class AbstractVertexBuffer {

	public static final int VERTICESPERQUAD = 4;
	public static final float HALF_ANGLE_SCALE = 1.4f*0.5f;
	public static final float PI = 3.1415926535f;
	
	public int mMaxVertexCount;
	public int mFinishedVertexCount;
	public int mFinishedIndexCount;
	public boolean mDynamicVertices;
	public int mFloatBufferCount;
	public int[] mFloatBufferElementSizes;
	public float[][] mNeutralElements;
	
	public abstract void initBuffers();
	public abstract void setDataPosition(int bufId,int pos);
	public abstract void reset();
	public abstract int getCurrentVertexWriteCount();
	public abstract void putVec2(int bufId, float v1,float v2);
	public abstract void putVec3(int bufId, float v1,float v2,float v3);
	public abstract void putVec4(int bufId, float v1,float v2,float v3,float v4);
	public abstract void putVec8(int bufId, float v1,float v2,float v3,float v4,float v5,float v6,float v7,float v8);
	public abstract void putArray(int bufId,float[] array,int offset,int elements);
	public abstract void setIndexPosition(int pos);
	public abstract int getCurrentIndexWriteCount();
	public abstract float readData(int bufId);
	public abstract int readIndex();
	public abstract void putRelativeIndex(int index);
	public abstract void putIndexArray(short[] array);
	public abstract int getBufferPosition(int bufId);
	
	public AbstractVertexBuffer(boolean dynamicVertices,int maxVertices) {
		mDynamicVertices = dynamicVertices;
		mMaxVertexCount = maxVertices;
	}
	
	public void init(int[] floatBufferElementSizes,float[][] neutralElements) {
		mFloatBufferElementSizes = floatBufferElementSizes;
		mFloatBufferCount = floatBufferElementSizes.length;
		mNeutralElements = neutralElements;
		initBuffers();
		reset();
	}
	
	public void fillBuffers() {
		int pos = getCurrentVertexWriteCount();
		for(int i=1;i<mNeutralElements.length;i++) {
			int elemSize = mFloatBufferElementSizes[i];
			int bufPos = getBufferPosition(i)/elemSize;
			while(bufPos<pos) {
				putArray(i,mNeutralElements[i]);
				bufPos++;
			}
		}
	}
	
	public void setDataPosition(int pos) {
		for(int i=0;i<mFloatBufferCount;i++) {
			setDataPosition(i,pos);
		}
	}
	
	public void putArray(int bufId,float[] array) {
		putArray(bufId,array,0,array.length);
	}
	
	public void putArrayMultiple(int bufId,float[] array,int amount) {
		for(int i=0;i<amount;i++)
			putArray(bufId,array);
	}
	
	public void putRect2D(int bufId, Rect rect, float offsetX,float offsetY) {
		putVec4(bufId,offsetX + rect.mLeft,offsetY + rect.mBottom,offsetX + rect.mRight,offsetY + rect.mBottom);
		putVec4(bufId,offsetX + rect.mLeft,offsetY + rect.mTop,offsetX + rect.mRight,offsetY + rect.mTop);
	}
	
	public void putRotatedSquare2D(int bufId,float scale, float offsetX,float offsetY,float angle) {
		float dirX = (float)Math.cos(angle+PI/4)*scale*HALF_ANGLE_SCALE;
		float dirY = (float)Math.sin(angle+PI/4)*scale*HALF_ANGLE_SCALE;
		putVec4(bufId,offsetX-dirX,offsetY-dirY,offsetX+dirY,offsetY-dirX);
		putVec4(bufId,offsetX-dirY,offsetY+dirX,offsetX+dirX,offsetY+dirY);
	}
	
	public void putTransformed3D(int bufId,float x,float y,float z,float[] matrix) {
		putVec3(bufId,
				matrix[0]*x+matrix[4]*y+matrix[8]*z+matrix[12],
				matrix[1]*x+matrix[5]*y+matrix[9]*z+matrix[13],
				matrix[2]*x+matrix[6]*y+matrix[10]*z+matrix[14]
				);
	}
	
	public void putTransformed2D(int bufId,float x,float y,float[] matrix) {
		putVec2(bufId,
				matrix[0]*x+matrix[4]*y+matrix[12],
				matrix[1]*x+matrix[5]*y+matrix[13]
				);
	}
	
	public void putTransformedArray(int bufId,float[] array, int vertexCount, int floatsPerVertex, float[] matrix) {
		if(floatsPerVertex==2) {
			for(int i=0;i<vertexCount;i++) {
				float x = array[i*2];
				float y = array[i*2+1];
				putVec2(bufId,
						matrix[0]*x+matrix[4]*y+matrix[12],
						matrix[1]*x+matrix[5]*y+matrix[13]
						);
			}
		}else{
			for(int i=0;i<vertexCount;i++) {
				float x = array[i*2];
				float y = array[i*2+1];
				float z = array[i*2+2];
				putVec3(bufId,
						matrix[0]*x+matrix[4]*y+matrix[8]*z+matrix[12],
						matrix[1]*x+matrix[5]*y+matrix[9]*z+matrix[13],
						matrix[2]*x+matrix[6]*y+matrix[10]*z+matrix[14]
						);
			}
		}
	}
	
	public void putTransformedArray(int bufId,float[] array, int vertexCount, int floatsPerVertex, float[] matrix,float offsetX,float offsetY,float offsetZ) {
		if(floatsPerVertex==2) {
			for(int i=0;i<vertexCount;i++) {
				float x = array[i*2];
				float y = array[i*2+1];
				putVec2(bufId,
						matrix[0]*x+matrix[4]*y+matrix[12] + offsetX,
						matrix[1]*x+matrix[5]*y+matrix[13] + offsetY
						);
			}
		}else{
			for(int i=0;i<vertexCount;i++) {
				float x = array[i*2];
				float y = array[i*2+1];
				float z = array[i*2+2];
				putVec3(bufId,
						matrix[0]*x+matrix[4]*y+matrix[8]*z+matrix[12] + offsetX,
						matrix[1]*x+matrix[5]*y+matrix[9]*z+matrix[13] + offsetY,
						matrix[2]*x+matrix[6]*y+matrix[10]*z+matrix[14] + offsetZ
						);
			}
		}
	}
	
}