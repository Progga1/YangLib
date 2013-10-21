package yang.graphics.buffers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class UniversalVertexBuffer extends IndexedVertexBuffer{

	public FloatBuffer[] mFloatBuffers;
	private ByteBuffer[] mByteBuffers;

	public UniversalVertexBuffer(boolean dynamicVertices,boolean dynamicIndices,int maxIndices,int maxVertices) {
		super(dynamicVertices,dynamicIndices,maxIndices,maxVertices);
	}

	@Override
	protected void initBuffer(int bufId) {
		mByteBuffers[bufId] = ByteBuffer.allocateDirect(mMaxVertexCount * mFloatBufferElementSizes[bufId] * 4).order(ByteOrder.nativeOrder());
		mFloatBuffers[bufId] = mByteBuffers[bufId].asFloatBuffer();
	}

	@Override
	protected void linkBuffer(int bufId,BufferLink link) {
		if(!(link.mVertexBuffer instanceof UniversalVertexBuffer))
			throw new RuntimeException("Invalid buffer type: "+link.mVertexBuffer.getClass().getName());
		final UniversalVertexBuffer buf = (UniversalVertexBuffer)link.mVertexBuffer;
		mByteBuffers[bufId] = buf.getByteBuffer(link.mLinkedBufferId);
		mFloatBuffers[bufId] = mByteBuffers[bufId].asFloatBuffer();
	}

	@Override
	protected void allocBuffers() {
		super.allocBuffers();
		mFloatBuffers = new FloatBuffer[mFloatBufferCount];
		mByteBuffers = new ByteBuffer[mFloatBufferCount];
	}

	@Override
	public void setDataPosition(int pos) {
		for(int i=0;i<mFloatBufferCount;i++) {
			mFloatBuffers[i].position(pos*mFloatBufferElementSizes[i]);
		}
	}

	@Override
	public void setDataPosition(int bufId,int pos) {
		mFloatBuffers[bufId].position(pos*mFloatBufferElementSizes[bufId]);
	}

	@Override
	public void reset() {
		for(int i=0;i<mFloatBufferCount;i++)
			mFloatBuffers[i].position(0);
		mIndexBuffer.position(0);
	}

	@Override
	public int getCurrentVertexWriteCount() {
		return mFloatBuffers[0].position()/mFloatBufferElementSizes[0];
	}

	@Override
	public float readData(int bufId) {
		return mFloatBuffers[bufId].get();
	}

	//---PUT-DATA---

	@Override
	public void putVec2(int bufId, float v1,float v2) {
		final FloatBuffer buf = mFloatBuffers[bufId];
		buf.put(v1);
		buf.put(v2);
	}

	@Override
	public void putVec3(int bufId, float v1,float v2,float v3) {
		final FloatBuffer buf = mFloatBuffers[bufId];
		buf.put(v1);
		buf.put(v2);
		buf.put(v3);
	}

	@Override
	public void putVec4(int bufId, float v1,float v2,float v3,float v4) {
		final FloatBuffer buf = mFloatBuffers[bufId];
		buf.put(v1);
		buf.put(v2);
		buf.put(v3);
		buf.put(v4);
	}

	@Override
	public void putVec6(int bufId, float v1,float v2,float v3,float v4,float v5,float v6) {
		final FloatBuffer buf = mFloatBuffers[bufId];
		buf.put(v1);
		buf.put(v2);
		buf.put(v3);
		buf.put(v4);
		buf.put(v5);
		buf.put(v6);
	}

	@Override
	public void putVec8(int bufId, float v1,float v2,float v3,float v4,float v5,float v6,float v7,float v8) {
		final FloatBuffer buf = mFloatBuffers[bufId];
		buf.put(v1);
		buf.put(v2);
		buf.put(v3);
		buf.put(v4);
		buf.put(v5);
		buf.put(v6);
		buf.put(v7);
		buf.put(v8);
	}

	@Override
	public void putVec12(int bufId, float v1,float v2,float v3,float v4,float v5,float v6,float v7,float v8,float v9,float v10,float v11,float v12) {
		final FloatBuffer buf = mFloatBuffers[bufId];
		buf.put(v1);
		buf.put(v2);
		buf.put(v3);
		buf.put(v4);
		buf.put(v5);
		buf.put(v6);
		buf.put(v7);
		buf.put(v8);
		buf.put(v9);
		buf.put(v10);
		buf.put(v11);
		buf.put(v12);
	}

	@Override
	public void putArray(int bufId,float[] array) {
		mFloatBuffers[bufId].put(array);
	}

	@Override
	public void putArray(int bufId,float[] array,int offset,int count) {
		mFloatBuffers[bufId].put(array,offset,count);
	}

	@Override
	public FloatBuffer getFloatBuffer(int bufferIndex) {
		return mFloatBuffers[bufferIndex];
	}

	@Override
	public ByteBuffer getByteBuffer(int bufferIndex) {
		return mByteBuffers[bufferIndex];
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		final int count = Math.max(mFinishedIndexCount, mIndexBuffer.position());
		mIndexBuffer.position(0);
		result.append("INDICES: ");
		for(int i=0;i<count;i++) {
			if(i>0)
				result.append(",");
			if(i%3==0)
				result.append(" ");
			result.append(mIndexBuffer.get());
		}
		result.append("\n");
		return result.toString();
	}

	@Override
	public int getBufferPosition(int bufId) {
		return mFloatBuffers[bufId].position();
	}

	@Override
	public void putRect2D(int bufId, float x1, float y1, float x2, float y2) {
		final FloatBuffer buf = mFloatBuffers[bufId];
		buf.put(x1);
		buf.put(y1);
		buf.put(x2);
		buf.put(y1);
		buf.put(x1);
		buf.put(y2);
		buf.put(x2);
		buf.put(y2);
	}

	@Override
	public void putRect3D(int bufId, float x1, float y1, float x2, float y2,float z) {
		final FloatBuffer buf = mFloatBuffers[bufId];
		buf.put(x1);
		buf.put(y1);
		buf.put(z);
		buf.put(x2);
		buf.put(y1);
		buf.put(z);
		buf.put(x1);
		buf.put(y2);
		buf.put(z);
		buf.put(x2);
		buf.put(y2);
		buf.put(z);
	}
}
