package yang.graphics.buffers;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;


public abstract class IndexedVertexBuffer extends AbstractVertexBuffer{

	public ShortBuffer mIndexBuffer;
	public IntBuffer mIndexBufferInt;
	public Buffer mActiveBuffer;
	public boolean mDynamicIndices;
	public int mMaxIndexCount;

	public IndexedVertexBuffer(boolean dynamicVertices,boolean dynamicIndices,int maxIndices,int maxVertices) {
		super(dynamicVertices,maxVertices);
		mDynamicIndices = dynamicIndices;
		mMaxIndexCount = maxIndices;
	}

	@Override
	protected void allocBuffers() {
		mIndexBuffer = ByteBuffer.allocateDirect(mMaxIndexCount*2).order(ByteOrder.nativeOrder()).asShortBuffer();
		mActiveBuffer = mIndexBuffer;
	}
	
	public void setIntMode(boolean enabled) {
		if(enabled) {
			if(mIndexBufferInt==null)
				mIndexBufferInt = ByteBuffer.allocateDirect(mMaxIndexCount*4).order(ByteOrder.nativeOrder()).asIntBuffer();
			mActiveBuffer = mIndexBufferInt;
		}else{
			mActiveBuffer = mIndexBuffer;
		}
	}
	
	public boolean isIntMode() {
		return mActiveBuffer == mIndexBufferInt;
	}

	//---INDICES---

	@Override
	public void setIndexPosition(int pos) {
		mActiveBuffer.position(0);
	}

	@Override
	public int getCurrentIndexWriteCount() {
		return mActiveBuffer.position();
	}

	/**
	 * index = -1 points to last vertex
	 */
	@Override
	public void putRelativeIndex(int index) {
		mIndexBuffer.put((short)(getCurrentVertexWriteCount()+index));
	}
	
	public void putRelativeIndexInt(int index) {
		mIndexBufferInt.put(getCurrentVertexWriteCount()+index);
	}

	public void putIndex(short index) {
		mIndexBuffer.put(index);
	}
	
	public void putIndexInt(int index) {
		mIndexBufferInt.put(index);
	}

	@Override
	public void putIndexArray(short[] indices) {
		mIndexBuffer.put(indices);
	}

	public void putIndexInt(int[] indices) {
		mIndexBufferInt.put(indices);
	}

	@Override
	public void putIndexArray(short[] indices,int offset,int count) {
		mIndexBuffer.put(indices,offset,count);
	}
	
	public void putIndexArray(int[] indices,int offset,int count) {
		mIndexBufferInt.put(indices,offset,count);
	}

	@Override
	public int readIndex() {
		return mActiveBuffer==mIndexBufferInt?mIndexBufferInt.get():mIndexBuffer.get();
	}
	
	public int readIndex(int pos) {
		return mActiveBuffer==mIndexBufferInt?mIndexBufferInt.get(pos):mIndexBuffer.get(pos);
	}
	
	public void putIndex(int index) {
		if(mActiveBuffer==mIndexBufferInt)
			mIndexBufferInt.put(index);
		else
			mIndexBuffer.put((short)index);
	}

	//TODO remove parameter wireFrames
	public void beginQuad(boolean wireFrames) {
		short offset = (short)getCurrentVertexWriteCount();
		if(wireFrames) {
			mIndexBuffer.put(offset);
			mIndexBuffer.put((short)(offset+1));
			mIndexBuffer.put((short)(offset+1));
			mIndexBuffer.put((short)(offset+3));
			mIndexBuffer.put((short)(offset+3));
			mIndexBuffer.put((short)(offset+2));
			mIndexBuffer.put((short)(offset+2));
			mIndexBuffer.put(offset);
		}else{
			mIndexBuffer.put(offset);
			offset++;
			mIndexBuffer.put(offset);
			mIndexBuffer.put((short)(offset+1));
			mIndexBuffer.put((short)(offset+2));
			mIndexBuffer.put((short)(offset+1));
			mIndexBuffer.put(offset);
		}
	}
	
	public void beginQuadInt(boolean wireFrames) {
		int offset = getCurrentVertexWriteCount();
		if(wireFrames) {
			mIndexBufferInt.put(offset);
			mIndexBufferInt.put((offset+1));
			mIndexBufferInt.put((offset+1));
			mIndexBufferInt.put((offset+3));
			mIndexBufferInt.put((offset+3));
			mIndexBufferInt.put((offset+2));
			mIndexBufferInt.put((offset+2));
			mIndexBufferInt.put(offset);
		}else{
			mIndexBufferInt.put(offset);
			offset++;
			mIndexBufferInt.put(offset);
			mIndexBufferInt.put((offset+1));
			mIndexBufferInt.put((offset+2));
			mIndexBufferInt.put((offset+1));
			mIndexBufferInt.put(offset);
		}
	}

	public void beginQuad() {
		beginQuad(false);
	}
	
	public void beginQuadInt() {
		beginQuadInt(false);
	}

	public void beginQuad(boolean wireFrames,short offset) {
		if(wireFrames) {
			mIndexBuffer.put(offset);
			mIndexBuffer.put((short)(offset+1));
			mIndexBuffer.put((short)(offset+1));
			mIndexBuffer.put((short)(offset+3));
			mIndexBuffer.put((short)(offset+3));
			mIndexBuffer.put((short)(offset+2));
			mIndexBuffer.put((short)(offset+2));
			mIndexBuffer.put(offset);
		}else{
			mIndexBuffer.put(offset);
			offset++;
			mIndexBuffer.put(offset);
			mIndexBuffer.put((short)(offset+1));
			mIndexBuffer.put((short)(offset+2));
			mIndexBuffer.put((short)(offset+1));
			mIndexBuffer.put(offset);
		}
	}
	
	public void beginQuadInt(boolean wireFrames,int offset) {
		if(wireFrames) {
			mIndexBufferInt.put(offset);
			mIndexBufferInt.put(offset+1);
			mIndexBufferInt.put(offset+1);
			mIndexBufferInt.put(offset+3);
			mIndexBufferInt.put(offset+3);
			mIndexBufferInt.put(offset+2);
			mIndexBufferInt.put(offset+2);
			mIndexBufferInt.put(offset);
		}else{
			mIndexBufferInt.put(offset);
			offset++;
			mIndexBufferInt.put(offset);
			mIndexBufferInt.put(offset+1);
			mIndexBufferInt.put(offset+2);
			mIndexBufferInt.put(offset+1);
			mIndexBufferInt.put(offset);
		}
	}

	public void putQuadIndicesMultiple(int count) {
		for(int i=0;i<count;i++) {
			beginQuad(false,(short)(i*4));
		}
	}
	
	public void putQuadIndicesMultipleInt(int count) {
		for(int i=0;i<count;i++) {
			beginQuadInt(false,(i*4));
		}
	}

	public void putRectIndices(int bottomLeft,int bottomRight,int topLeft,int topRight) {
		mIndexBuffer.put((short)bottomLeft);
		mIndexBuffer.put((short)bottomRight);
		mIndexBuffer.put((short)topLeft);
		mIndexBuffer.put((short)topRight);
		mIndexBuffer.put((short)topLeft);
		mIndexBuffer.put((short)bottomRight);
	}
	
	public void putRectIndicesInt(int bottomLeft,int bottomRight,int topLeft,int topRight) {
		mIndexBufferInt.put(bottomLeft);
		mIndexBufferInt.put(bottomRight);
		mIndexBufferInt.put(topLeft);
		mIndexBufferInt.put(topRight);
		mIndexBufferInt.put(topLeft);
		mIndexBufferInt.put(bottomRight);
	}

	public int getMaxVertexCount() {
		return mMaxVertexCount;
	}

	public boolean isDynamic() {
		return mDynamicVertices;
	}

	public void dispose() {

	}

	public void beginUpdate() {

	}

	public void beginDataUpdate() {

	}

	//---After-finish---
	public void finishUpdate() {
		mFinishedVertexCount = getCurrentVertexWriteCount();
		mFinishedIndexCount = getCurrentIndexWriteCount();
	}

	public void finishDataUpdate() {
		mFinishedVertexCount = getCurrentVertexWriteCount();
	}

	public int getVertexCount() {
		return mFinishedVertexCount;
	}

	public int getIndexCount() {
		return mFinishedIndexCount;
	}

	public boolean bindBuffer(int handle, int bufferIndex) {
		return false;
	}

	public boolean draw(int bufferStart, int drawVertexCount,int mode) {
		return false;
	}

	public void setAsCurrent() {

	}

	public FloatBuffer getFloatBuffer(int bufferIndex) {
		return null;
	}

	public ByteBuffer getByteBuffer(int bufferIndex) {
		return null;
	}

	public void putGridIndices(int width,int height) {
		if(isIntMode()) {
			putGridIndicesInt(width,height);
			return;
		}
		short c = (short)getCurrentVertexWriteCount();
		for(int i=0;i<height-1;i++) {
			for(int j=0;j<width-1;j++){
				mIndexBuffer.put(c);
				mIndexBuffer.put((short)(c+1));
				mIndexBuffer.put((short)(c+width));
				mIndexBuffer.put((short)(c+1+width));
				mIndexBuffer.put((short)(c+width));
				mIndexBuffer.put((short)(c+1));
				c++;
			}
//			if(loopX) {
//				mIndexBuffer.put((short)(c));
//				mIndexBuffer.put((short)(c-width+1));
//				mIndexBuffer.put((short)(c+2));
//				mIndexBuffer.put((short)(c+1));
//				mIndexBuffer.put((short)(c+width));
//				mIndexBuffer.put((short)(c-width+1));
//			}
			c++;
		}
	}
	
	public void putGridIndicesInt(int width,int height) {
		int c = getCurrentVertexWriteCount();
		for(int i=0;i<height-1;i++) {
			for(int j=0;j<width-1;j++){
				mIndexBufferInt.put(c);
				mIndexBufferInt.put(c+1);
				mIndexBufferInt.put(c+width);
				mIndexBufferInt.put(c+1+width);
				mIndexBufferInt.put(c+width);
				mIndexBufferInt.put(c+1);
				c++;
			}
//			if(loopX) {
//				mIndexBuffer.put((short)(c));
//				mIndexBuffer.put((short)(c-width+1));
//				mIndexBuffer.put((short)(c+2));
//				mIndexBuffer.put((short)(c+1));
//				mIndexBuffer.put((short)(c+width));
//				mIndexBuffer.put((short)(c-width+1));
//			}
			c++;
		}
	}

	public void putGridIndexPatch(int width, int patchX,int patchY) {
		if(mActiveBuffer==mIndexBufferInt) {
			putGridIndexPatchInt(width,patchX,patchY);
			return;
		}else{
			short c = (short)getCurrentVertexWriteCount();
			c += width*patchY + patchX;
			mIndexBuffer.put(c);
			mIndexBuffer.put((short)(c+1));
			mIndexBuffer.put((short)(c+width));
			mIndexBuffer.put((short)(c+1+width));
			mIndexBuffer.put((short)(c+width));
			mIndexBuffer.put((short)(c+1));
		}
	}
	
	public void putGridIndexPatchInt(int width, int patchX,int patchY) {
		int c = getCurrentVertexWriteCount();
		c += width*patchY + patchX;
		mIndexBufferInt.put(c);
		mIndexBufferInt.put(c+1);
		mIndexBufferInt.put(c+width);
		mIndexBufferInt.put(c+1+width);
		mIndexBufferInt.put(c+width);
		mIndexBufferInt.put(c+1);
	}

	public void putStripSegmentIndices(short startIndex,int segmentCount) {
		short c = (short)(startIndex+2);

		for(int i=0;i<segmentCount;i++) {
			mIndexBuffer.put((short)(c-2));
			mIndexBuffer.put((short)(c-1));
			mIndexBuffer.put(c);
			mIndexBuffer.put((short)(c+1));
			mIndexBuffer.put(c);
			mIndexBuffer.put((short)(c-1));
			c += 2;
		}
	}

	public void putStripSegmentIndicesInt(int startIndex,int segmentCount) {
		int c = startIndex+2;

		for(int i=0;i<segmentCount;i++) {
			mIndexBufferInt.put(c-2);
			mIndexBufferInt.put(c-1);
			mIndexBufferInt.put(c);
			mIndexBufferInt.put(c+1);
			mIndexBufferInt.put(c);
			mIndexBufferInt.put(c-1);
			c += 2;
		}
	}
	
	public void putStripIndices(short startIndex,int triangleCount) {
		short c = (short)(startIndex+2);

		for(int i=0;i<triangleCount;i++) {
			mIndexBuffer.put((short)(c-2));
			mIndexBuffer.put((short)(c-1));
			mIndexBuffer.put(c);
			c += 1;
		}
	}
	
	public void putStripIndicesInt(int startIndex,int triangleCount) {
		int c = startIndex+2;

		for(int i=0;i<triangleCount;i++) {
			mIndexBufferInt.put(c-2);
			mIndexBufferInt.put(c-1);
			mIndexBufferInt.put(c);
			c += 1;
		}
	}

	public Buffer getActiveIndexBuffer() {
		return mActiveBuffer;
	}
}
