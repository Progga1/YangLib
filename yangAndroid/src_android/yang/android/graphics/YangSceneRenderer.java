package yang.android.graphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import yang.graphics.translator.GraphicsTranslator;
import yang.surface.YangSurface;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class YangSceneRenderer implements GLSurfaceView.Renderer {

	public YangSurface mSurface;
	public GraphicsTranslator mGraphicsTranslator;

	public YangSceneRenderer(Context context) {
		mGraphicsTranslator = new AndroidGraphics(context);
	}

	public void setSurface(YangSurface surface) {
		mSurface = surface;
		mSurface.setBackend(mGraphicsTranslator);
	}

	@Override
	public void onDrawFrame(GL10 ignore) {
		mSurface.drawFrame();
	}

	@Override
	public void onSurfaceCreated(GL10 ignore, EGLConfig config) {
		mSurface.onSurfaceCreated(true);
	}

	@Override
	public void onSurfaceChanged(GL10 ignore, int width, int height) {
		mSurface.onSurfaceChanged(width,height);
	}

	public GraphicsTranslator getGraphics() {
		return mGraphicsTranslator;
	}
}

