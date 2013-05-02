package yang.android.graphics;

import yang.graphics.buffers.UniversalVertexBuffer;
import android.opengl.GLES20;

public class AndroidVertexBufferObject extends UniversalVertexBuffer{

	private int[] bufferIndices;
	private int indexBufferId;
	private boolean firstUpdate;
	
	public AndroidVertexBufferObject(boolean dynamicVertices, boolean dynamicIndices, int maxIndices,int maxVertices) {
		super(dynamicVertices,dynamicIndices,maxIndices,maxVertices);
		firstUpdate = true;
	}
	
	@Override
	public void initBuffers() {
		super.initBuffers();
		
		bufferIndices = new int[mFloatBufferCount];
		GLES20.glGenBuffers(mFloatBufferCount, bufferIndices, 0);
		
		int[] indexBufferIds = new int[1];
		GLES20.glGenBuffers(1, indexBufferIds, 0);
		indexBufferId = indexBufferIds[0];
	}
	
	private void upload(int index) {
		int pos = mFloatBuffers[index].position();
		mFloatBuffers[index].position(0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferIndices[index]);
		AndroidGraphics.clearError();
		if(false) {
			if(firstUpdate) 
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mMaxVertexCount * mFloatBufferElementSizes[index] * 4, null, mDynamicVertices?GLES20.GL_STREAM_DRAW:GLES20.GL_STATIC_DRAW);
			GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, pos * mFloatBufferElementSizes[index] * 4, mFloatBuffers[index]);
		}else{
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, pos * mFloatBufferElementSizes[index] * 4, mFloatBuffers[index], mDynamicVertices?GLES20.GL_STREAM_DRAW:GLES20.GL_STATIC_DRAW);
		}
		AndroidGraphics.checkError("update "+index+" buffer");
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void finishUpdate() {
		super.finishUpdate();
		for(int i=0;i<mFloatBufferCount;i++)
			upload(i);
		
		int pos = mIndexBuffer.position();
		mIndexBuffer.position(0);
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, pos * 2, mIndexBuffer, mDynamicIndices?GLES20.GL_STREAM_DRAW:GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		firstUpdate = false;
	}
	
	@Override
	public boolean bindBuffer(int handle,int bufferIndex) {
		//if(true)return false;
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferIndices[bufferIndex]);
		GLES20.glEnableVertexAttribArray(handle);
		GLES20.glVertexAttribPointer(handle, mFloatBufferElementSizes[bufferIndex], GLES20.GL_FLOAT, false, 0, 0);
		return true;
	}
	
	@Override
	public void setAsCurrent() {
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
	}
	
	@Override
	public boolean draw(int bufferStart, int drawVertexCount,int mode) {
		//if(true)
		//	return false;
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawVertexCount, GLES20.GL_UNSIGNED_SHORT, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		return true;
	}
	
}
