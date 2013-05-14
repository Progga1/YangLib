package yang.graphics.buffers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public abstract class IndexedVertexBuffer extends AbstractVertexBuffer{

	public ShortBuffer mIndexBuffer;
	public boolean mDynamicIndices;
	public int mMaxIndexCount;
	
	public IndexedVertexBuffer(boolean dynamicVertices,boolean dynamicIndices,int maxIndices,int maxVertices) {
		super(dynamicVertices,maxVertices);
		mDynamicIndices = dynamicIndices;
		mMaxIndexCount = maxIndices;
	}
	
	public void initBuffers() {
		mIndexBuffer = ByteBuffer.allocateDirect(mMaxIndexCount*2).order(ByteOrder.nativeOrder()).asShortBuffer();
	}
	
	//---INDICES---
	
	@Override
	public void setIndexPosition(int pos) {
		mIndexBuffer.position(0);
	}

	@Override
	public int getCurrentIndexWriteCount() {
		return mIndexBuffer.position();
	}
	
	/**
	 * index = -1 points to last vertex
	 */
	@Override
	public void putRelativeIndex(int index) {
		mIndexBuffer.put((short)(getCurrentVertexWriteCount()+index));
	}
	
	public void putIndex(short index) {
		mIndexBuffer.put(index);
	}
	
	@Override
	public void putIndexArray(short[] indices) {
		mIndexBuffer.put(indices);
	}
	
	@Override
	public int readIndex() {
		return mIndexBuffer.get();
	}
	
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
	
	//---After-finish---
	public void finishUpdate() {
		mFinishedVertexCount = getCurrentVertexWriteCount();
		mFinishedIndexCount = getCurrentIndexWriteCount();
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
	
}
