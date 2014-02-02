package yang.graphics.buffers;

import yang.graphics.translator.AbstractGraphics;
import yang.graphics.translator.glconsts.GLDrawModes;

public class DrawBatch {

	private String mName;
	private String mDebugOut = "PRE draw batch";
	public AbstractGraphics<?> mGraphics;
	public IndexedVertexBuffer mVertexBuffer;

	public DrawBatch(AbstractGraphics<?> graphics,IndexedVertexBuffer vertexBuffer) {
		mGraphics = graphics;
		mVertexBuffer = vertexBuffer;
	}

	public DrawBatch setName(String name) {
		mName = name;
		mDebugOut = "draw batch ("+name+")";
		return this;
	}

	public String getName() {
		return mName;
	}

	public void draw() {
		assert mGraphics.mTranslator.preCheck(mDebugOut);
		if(mVertexBuffer.getIndexCount()==0)
			return;

		mGraphics.mTranslator.mBatchCount++;
		mGraphics.mTranslator.mBatchPolygonCount += mVertexBuffer.getIndexCount()/3;
		//mVertexBuffer.reset();
		mGraphics.mTranslator.drawBuffer(mVertexBuffer,0, mVertexBuffer.getIndexCount(), GLDrawModes.TRIANGLES);
//		mGraphics.setVertexBuffer(mVertexBuffer);
//		mGraphics.mTranslator.prepareDraw();
//		mGraphics.mTranslator.drawVertices(0, mVertexBuffer.getIndexCount(), GraphicsTranslator.T_TRIANGLES);
//		mGraphics.resetVertexBuffer();
		assert mGraphics.mTranslator.checkErrorInst("Draw batch");
	}

}
