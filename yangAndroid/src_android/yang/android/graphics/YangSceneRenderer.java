package yang.android.graphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import yang.graphics.YangSurface;
import yang.graphics.translator.GraphicsTranslator;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class YangSceneRenderer implements GLSurfaceView.Renderer {

	public YangSurface mSurfaceInterface;
	public GraphicsTranslator mGraphicsTranslator;
	
	public YangSceneRenderer(Context context) {
		mGraphicsTranslator = new AndroidGraphics(context);
	}
	
	public void setSurface(YangSurface surface) {
		mSurfaceInterface = surface;
		mSurfaceInterface.setGraphics(mGraphicsTranslator);
	}
	
	public void onDrawFrame(GL10 ignore) {
		mSurfaceInterface.drawFrame();
	}

	public void onSurfaceCreated(GL10 ignore, EGLConfig config) {
		mSurfaceInterface.onSurfaceCreated();
	}
	
	public void onSurfaceChanged(GL10 ignore, int width, int height) {
		mSurfaceInterface.onSurfaceChanged(width,height);
	}

	public GraphicsTranslator getGraphics() {
		return mGraphicsTranslator;
	}
}
	
