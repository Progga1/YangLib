package yang.android.graphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import yang.graphics.SurfaceInterface;
import yang.graphics.translator.GraphicsTranslator;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class YangSceneRenderer implements GLSurfaceView.Renderer {

	public SurfaceInterface mSurfaceInterface;
	public GraphicsTranslator mGraphicsTranslator;
	
	public YangSceneRenderer(Context context) {
		mGraphicsTranslator = new AndroidGraphics(context);
	}
	
	public void setSurface(SurfaceInterface surface) {
		mSurfaceInterface = surface;
		mSurfaceInterface.setGraphics(mGraphicsTranslator);
	}
	
	public void onDrawFrame(GL10 ignore) {
		mSurfaceInterface.drawFrame();
	}

	public void onSurfaceCreated(GL10 ignore, EGLConfig config) {
		System.out.println("CREATE-SURF--------------");
		mSurfaceInterface.onSurfaceCreated();
		
	}
	
	public void onSurfaceChanged(GL10 ignore, int width, int height) {
		System.out.println("SET-SURF-SIZE--------------");
		mSurfaceInterface.onSurfaceChanged(width,height);
	}

	public GraphicsTranslator getGraphics() {
		return mGraphicsTranslator;
	}
}
	
