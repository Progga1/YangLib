package yang.graphics.defaults;

import yang.events.Keys;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangSensorEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.font.BitmapFont;
import yang.math.objects.matrix.YangMatrix;
import yang.model.DebugYang;
import yang.surface.YangSurface;
import yang.systemdependent.YangSensor;

public abstract class DefaultSurface extends YangSurface implements YangEventListener{

	public Default2DGraphics mGraphics2D;
	public Default3DGraphics mGraphics3D;
	private boolean mInit2DGraphics;
	private boolean mInit3DGraphics;

	protected DefaultSurface(boolean init2DGraphics,boolean init3DGraphics) {
		super();
		mInit2DGraphics = init2DGraphics;
		mInit3DGraphics = init3DGraphics;
		mEventListener = this;
		mMetaEventListener = new DefaultMetaEventListener(this);
	}

	@Override
	protected final void initGraphics() {
		if(mInit2DGraphics) {
			mGraphics2D = new Default2DGraphics(mGraphics);
			mGraphics2D.init();
			if(!mInit3DGraphics) {
				mGraphics2D.activate();
				mGraphics2D.setDefaultProgram();
			}
		}
		if(mInit3DGraphics) {
			mGraphics3D = new Default3DGraphics(mGraphics);
			if(mInit2DGraphics)
				mGraphics3D.shareBuffers(mGraphics2D);
			mGraphics3D.init();
			if(!mInit2DGraphics) {
				mGraphics3D.activate();
				mGraphics3D.setDefaultProgram();
			}
		}
	}

	protected void initDebugOutput(BitmapFont font) {
		initDebugOutput(mGraphics2D,font);
	}

	public boolean rawEvent(YangEvent event) {
		return false;
	}

	public void pointerDown(float x, float y, SurfacePointerEvent event) {

	}

	public void pointerDragged(float x, float y, SurfacePointerEvent event) {

	}

	public void pointerMoved(float x, float y, SurfacePointerEvent event) {

	}

	public void pointerUp(float x, float y, SurfacePointerEvent event) {

	}

	public void keyDown(int code) {

	}

	public void keyUp(int code) {
		if(code==Keys.ESC)
			exit();
	}

	public void zoom(float factor) {

	}
	
	public void sensorChanged(YangSensorEvent event) {
		if(event.mType==YangSensor.TYPE_GYROSCOPE) {
			mGraphics.mStereoCameraMatrixEnabled = true;
			YangMatrix mat = mGraphics.mStereoCameraMatrix;
			mat.rotateZ(-event.mZ);
			mat.rotateY(event.mY);
			mat.rotateX(-event.mX);
		}
		if(event.mType==YangSensor.TYPE_ROTATION_VECTOR) {
			mGraphics.mStereoCameraMatrixEnabled = true;
			YangMatrix mat = mGraphics.mStereoCameraMatrix;
			mat.setFromQuaternion(event.mX,event.mY,event.mZ);
		}
	}

}
