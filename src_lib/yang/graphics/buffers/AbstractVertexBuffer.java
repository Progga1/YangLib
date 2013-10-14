package yang.graphics.buffers;

import yang.model.Rect;
import yang.util.NonConcurrentList;


public abstract class AbstractVertexBuffer {

	protected class BufferLink {
		public int mBufferId;
		public AbstractVertexBuffer mVertexBuffer;
		public int mLinkedBufferId;
	}
	
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
	public NonConcurrentList<BufferLink> mLinkedBuffers;
	
	protected abstract void allocBuffers();
	protected abstract void initBuffer(int bufId);
	public abstract void setDataPosition(int bufId,int pos);
	public abstract void reset();
	public abstract int getCurrentVertexWriteCount();
	public abstract void putVec2(int bufId, float v1,float v2);
	public abstract void putVec3(int bufId, float v1,float v2,float v3);
	public abstract void putVec4(int bufId, float v1,float v2,float v3,float v4);
	public abstract void putVec6(int bufId, float v1,float v2,float v3, float v4,float v5,float v6);
	public abstract void putVec8(int bufId, float v1,float v2,float v3,float v4,float v5,float v6,float v7,float v8);
	public abstract void putVec12(int bufId, float v1,float v2,float v3,float v4,float v5,float v6,float v7,float v8,float v9,float v10,float v11,float v12);
	public abstract void putRect2D(int bufId, float x1, float y1, float x2, float y2);
	public abstract void putRect3D(int bufId, float x1, float y1, float x2,float y2, float z);
	public abstract void putArray(int bufId,float[] array,int offset,int elements);
	public abstract void setIndexPosition(int pos);
	public abstract int getCurrentIndexWriteCount();
	public abstract float readData(int bufId);
	public abstract int readIndex();
	public abstract void putRelativeIndex(int index);
	public abstract void putIndexArray(short[] indices,int offset,int count);
	public abstract int getBufferPosition(int bufId);
	
	public AbstractVertexBuffer(boolean dynamicVertices,int maxVertices) {
		mDynamicVertices = dynamicVertices;
		mMaxVertexCount = maxVertices;
	}
	
	public final void initBuffers() {
		allocBuffers();
		for(int i=0;i<mFloatBufferCount;i++) {
			initBuffer(i);
		}
	}
	
	public void init(int[] floatBufferElementSizes,float[][] neutralElements) {
		mFloatBufferElementSizes = floatBufferElementSizes;
		mFloatBufferCount = floatBufferElementSizes.length;
		mNeutralElements = neutralElements;
		initBuffers();
		reset();
	}
	
	public void putIndexArray(short[] indices) {
		putIndexArray(indices,0,indices.length);
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
	
	/**
	 * Must be called before initBuffers.
	 */
	public void linkBuffer(int bufferId, AbstractVertexBuffer vertexBuffer, int linkedBufferId) {
		BufferLink link = new BufferLink(); 
		link.mBufferId = bufferId;
		link.mVertexBuffer = vertexBuffer;
		link.mLinkedBufferId = linkedBufferId;
		mLinkedBuffers.add(link);
	}
	
	public void setDataPosition(int pos) {
		for(int i=0;i<mFloatBufferCount;i++) {
			setDataPosition(i,pos);
		}
	}
	
	public void putArray(int bufId,float[] array) {
		putArray(bufId,array,0,array.length);
	}
	
	public void putArray(int bufId, float[] array, int count) {
		putArray(bufId,array,0,count);
	}
	
	public void putArrayMultiple(int bufId,float[] array,int amount) {
		for(int i=0;i<amount;i++)
			putArray(bufId,array);
	}
	
	public void putRect2D(int bufId, Rect rect, float offsetX,float offsetY) {
		putVec8(bufId,offsetX + rect.mLeft,offsetY + rect.mBottom,offsetX + rect.mRight,offsetY + rect.mBottom,offsetX + rect.mLeft,offsetY + rect.mTop,offsetX + rect.mRight,offsetY + rect.mTop);
	}
	
	public void putRect3D(int bufId, Rect rect, float offsetX,float offsetY,float z) {
		putVec12(bufId,
				offsetX + rect.mLeft,offsetY + rect.mBottom,z,
				offsetX + rect.mRight,offsetY + rect.mBottom,z,
				offsetX + rect.mLeft,offsetY + rect.mTop,z,
				offsetX + rect.mRight,offsetY + rect.mTop,z
				);
	}
	
	public void putRotatedSquare2D(int bufId,float scale, float offsetX,float offsetY,float angle) {
		float dirX = (float)Math.cos(angle+PI/4)*scale*HALF_ANGLE_SCALE;
		float dirY = (float)Math.sin(angle+PI/4)*scale*HALF_ANGLE_SCALE;
		putVec8(bufId,offsetX-dirX,offsetY-dirY,offsetX+dirY,offsetY-dirX,offsetX-dirY,offsetY+dirX,offsetX+dirX,offsetY+dirY);
	}
	

	public void putRotatedSquare3D(int bufId, float scale, float offsetX, float offsetY, float z, float angle) {
		float dirX = (float)Math.cos(angle+PI/4)*scale*HALF_ANGLE_SCALE;
		float dirY = (float)Math.sin(angle+PI/4)*scale*HALF_ANGLE_SCALE;
		putVec12(bufId,offsetX-dirX,offsetY-dirY,z,
				offsetX+dirY,offsetY-dirX,z,
				offsetX-dirY,offsetY+dirX,z,
				offsetX+dirX,offsetY+dirY,z);
	}
	
	public void putRotatedRect2D(int bufId,float width,float height,float offsetX,float offsetY,float angle) {
		float sinA = (float)Math.sin(angle);
		float cosA = (float)Math.cos(angle);
		width *= 0.5f;
		height *= 0.5f;
		putVec8(bufId,
				offsetX-width*cosA+height*sinA,offsetY-width*sinA-height*cosA,
				offsetX+width*cosA+height*sinA,offsetY+width*sinA-height*cosA,
				offsetX-width*cosA-height*sinA,offsetY-width*sinA+height*cosA,
				offsetX+width*cosA-height*sinA,offsetY+width*sinA+height*cosA
				);
	}
	
	public void putRotatedRect3D(int bufId, float width, float height, float offsetX, float offsetY, float z, float angle) {
		float sinA = (float)Math.sin(angle);
		float cosA = (float)Math.cos(angle);
		float widthSin = width*0.5f*sinA;
		float widthCos = width*0.5f*cosA;
		float heightSin = height*0.5f*sinA;
		float heightCos = height*0.5f*cosA;
		height *= 0.5f;
		putVec12(bufId,
				offsetX-widthCos+heightSin,offsetY-widthSin-heightCos,z,
				offsetX+widthCos+heightSin,offsetY+widthSin-heightCos,z,
				offsetX-widthCos-heightSin,offsetY-widthSin+heightCos,z,
				offsetX+widthCos-heightSin,offsetY+widthSin+heightCos,z);
//		width *= 0.5f;
//		height *= 0.5f;
//		putVec12(bufId,
//				offsetX-width,offsetY-height,z,
//				offsetX+width,offsetY-height,z,
//				offsetX-width,offsetY+height,z,
//				offsetX+width,offsetY+height,z);
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
	
	public void putTransformedArray2D(int bufId,float[] array, int vertexCount, float[] matrix) {
		for(int i=0;i<vertexCount;i++) {
			float x = array[i*2];
			float y = array[i*2+1];
			putVec2(bufId,
					matrix[0]*x+matrix[4]*y+matrix[12],
					matrix[1]*x+matrix[5]*y+matrix[13]
					);
		}
	}
	
	public void putTransformedArray3D(int bufId,float[] array, int vertexCount, float[] matrix) {
		for(int i=0;i<vertexCount;i++) {
			float x = array[i*3];
			float y = array[i*3+1];
			float z = array[i*3+2];
			putVec3(bufId,
					matrix[0]*x+matrix[4]*y+matrix[8]*z+matrix[12],
					matrix[1]*x+matrix[5]*y+matrix[9]*z+matrix[13],
					matrix[2]*x+matrix[6]*y+matrix[10]*z+matrix[14]
					);
		}
	}
	
	public void putTransformedArray2D(int bufId,float[] array, int vertexCount, float[] matrix,float offsetX,float offsetY) {
		for(int i=0;i<vertexCount;i++) {
			float x = array[i*2];
			float y = array[i*2+1];
			putVec2(bufId,
					matrix[0]*x+matrix[4]*y+matrix[12] + offsetX,
					matrix[1]*x+matrix[5]*y+matrix[13] + offsetY
					);
		}
	}
	
	public void putTransformedArray3D(int bufId,float[] array, int vertexCount, float[] matrix,float offsetX,float offsetY,float offsetZ) {
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
	
	public void putTransformedArray(int bufId,float[] array, int vertexCount, int floatsPerVertex,float[] matrix,float offsetX,float offsetY,float offsetZ) {
		if(floatsPerVertex==2) {
			if(offsetX==0 && offsetY==0)
				putTransformedArray2D(bufId,array,vertexCount,matrix);
			else
				putTransformedArray2D(bufId,array,vertexCount,matrix,offsetX,offsetY);
		}else{
			if(offsetX==0 && offsetY==0)
				putTransformedArray3D(bufId,array,vertexCount,matrix);
			else
				putTransformedArray3D(bufId,array,vertexCount,matrix,offsetX,offsetY,offsetZ);
		}
	}
	
}
