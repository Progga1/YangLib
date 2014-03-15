package yang.graphics.defaults.geometrycreators;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.translator.AbstractGraphics;
import yang.graphics.translator.GraphicsTranslator;

public class GeometryCreator<GraphicsType extends AbstractGraphics<?>> {

	public static float PI = 3.1415926535f;

	protected int mVertexStartId,mIndexStartId;
	public GraphicsType mGraphics;
	protected GraphicsTranslator mTranslator;

	public GeometryCreator(GraphicsType graphics) {
		if(graphics!=null)
			setGraphics(graphics);
	}

	public void setGraphics(GraphicsType graphics) {
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
	}

	public void finish() {
		mGraphics.fillBuffers();
	}

	public DrawBatch finishBatch() {
		finish();
		return mGraphics.finishBatchRecording();
	}

	public void begin() {
		mIndexStartId = mGraphics.getCurrentVertexBuffer().getCurrentIndexWriteCount();
		mVertexStartId = mGraphics.getCurrentVertexBuffer().getCurrentVertexWriteCount();
	}

	public void putNormals() {
		//TODO make normals graphics 3D independent
		((Default3DGraphics)mGraphics).fillNormals(mIndexStartId);
	}

}
