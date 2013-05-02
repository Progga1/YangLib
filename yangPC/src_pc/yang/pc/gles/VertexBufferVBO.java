package yang.pc.gles;

import javax.media.opengl.GL2ES2;

import yang.graphics.buffers.UniversalVertexBuffer;


public class VertexBufferVBO extends UniversalVertexBuffer{

	private GL2ES2 mGl;
	private int[] bufferIndices;
	private int indexBufferId;
	
	public VertexBufferVBO(boolean dynamicVertices,boolean dynamicIndices, int maxIndices,int maxVertices,GL2ES2 gl) {
		super(dynamicVertices,dynamicIndices,maxIndices,maxVertices);
		this.mGl = gl;
	}
	
	@Override
	public void initBuffers() {
		super.initBuffers();
		
		bufferIndices = new int[mFloatBufferCount];
		mGl.glGenBuffers(mFloatBufferCount, bufferIndices, 0);
		
		int[] indexBufferIds = new int[1];
		mGl.glGenBuffers(1, indexBufferIds, 0);
		indexBufferId = indexBufferIds[0];
	}
	
	private void upload(int index) {
		mGl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, bufferIndices[index]);
		mGl.glBufferData(GL2ES2.GL_ARRAY_BUFFER, mFloatBuffers[index].position() * mFloatBufferElementSizes[index] * 4, mFloatBuffers[index], GL2ES2.GL_STATIC_DRAW);
		mGl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void finishUpdate() {
		super.finishUpdate();
		for(int i=0;i<mFloatBufferCount;i++)
			upload(i);
		mGl.glBindBuffer(GL2ES2.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		mGl.glBufferData(GL2ES2.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer.capacity() * 2, mIndexBuffer, GL2ES2.GL_STATIC_DRAW);
		mGl.glBindBuffer(GL2ES2.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	@Override
	public boolean bindBuffer(int handle,int bufferIndex) {
		//if(true)return false;
		mGl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, bufferIndices[bufferIndex]);
		mGl.glEnableVertexAttribArray(handle);
		mGl.glVertexAttribPointer(handle, mFloatBufferElementSizes[bufferIndex], GL2ES2.GL_FLOAT, false, 0, 0L);
		return true;
	}
	
	@Override
	public void setAsCurrent() {
		mGl.glBindBuffer(GL2ES2.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
	}
	
	@Override
	public boolean draw(int bufferStart, int drawVertexCount,int mode) {
		mGl.glDrawElements(GL2ES2.GL_TRIANGLES, drawVertexCount, GL2ES2.GL_UNSIGNED_SHORT, 0);
		return true;
	}
	
}
