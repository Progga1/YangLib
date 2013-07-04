package yang.graphics.defaults.meshcreators;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.translator.AbstractGraphics;
import yang.graphics.translator.GraphicsTranslator;

public class MeshCreator<GraphicsType extends AbstractGraphics<?>> {

	public static float PI = 3.1415926535f;
	
	public GraphicsType mGraphics;
	protected GraphicsTranslator mTranslator;
	
	public MeshCreator(GraphicsType graphics) {
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
	
}
