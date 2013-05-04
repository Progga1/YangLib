package yang.android.graphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import yang.graphics.SurfaceInterface;
import yang.graphics.translator.GraphicsTranslator;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class SceneRenderer implements GLSurfaceView.Renderer {

	public SurfaceInterface mSurfaceInterface;
	public GraphicsTranslator mGraphicsTranslator;
	
	public SceneRenderer(Context context) {
		mGraphicsTranslator = new AndroidGraphics(context);
	}
	
	public void setSurface(SurfaceInterface surface) {
		mSurfaceInterface = surface;
		mSurfaceInterface.setGraphics(mGraphicsTranslator);
	}
	
	public void onDrawFrame(GL10 ignore) {
		mSurfaceInterface.draw();
		mGraphicsTranslator.flush();
	}

	public void onSurfaceCreated(GL10 ignore, EGLConfig config) {
		
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		
	}
	
	public void onSurfaceChanged(GL10 ignore, int width, int height) {
		mSurfaceInterface.surfaceChanged(width,height);
	}

	public GraphicsTranslator getGraphics() {
		return mGraphicsTranslator;
	}
}
	
