package yang.graphics.defaults;


import java.io.FileNotFoundException;

import yang.events.Keys;
import yang.events.YangEventQueue;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangSensorEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.stereovision.LensDistortionShader;
import yang.graphics.util.HeadMovement;
import yang.model.DebugYang;
import yang.surface.YangSurface;

public class DefaultMetaEventListener implements YangEventListener {

	public YangSurface mSurface;
	private boolean mRecording = false;
	private boolean mCtrlPressed = false;
	private boolean mShiftPressed = false;
	private HeadMovement mHead = new HeadMovement();
	
	public DefaultMetaEventListener(YangSurface surface) {
		mSurface = surface;
		initMetaKeys();
	}
	
	protected void initMetaKeys() {
		YangEventQueue eventQueue = mSurface.mEventQueue;
		eventQueue.setMetaKeys(Keys.F1,12);
	}
	
	@Override
	public boolean rawEvent(YangEvent event) {
		return false;
	}

	@Override
	public void pointerDown(float x, float y, SurfacePointerEvent event) {
		if(mCtrlPressed) {
			if(event.mButton==SurfacePointerEvent.BUTTON_MIDDLE) {
				mSurface.mGraphics.mStereoCameraMatrixEnabled = false;
				mHead.reset();
			}
				
		}
	}

	@Override
	public void pointerMoved(float x, float y, SurfacePointerEvent event) {
		
	}

	@Override
	public void pointerDragged(float x, float y, SurfacePointerEvent event) {
		if(mCtrlPressed) {
			mSurface.mGraphics.mStereoCameraMatrixEnabled = true;
			if(event.mButton==SurfacePointerEvent.BUTTON_LEFT) {
				mHead.mYaw += event.mDeltaX;
				mHead.mPitch += event.mDeltaY;
			}
			if(event.mButton==SurfacePointerEvent.BUTTON_RIGHT) {
				mHead.mRoll += event.mDeltaX;
				mHead.mPitch += event.mDeltaY;
			}
			mSurface.mGraphics.mStereoCameraMatrix.set(mHead.getUpdatedMatrix());
		}
	}

	@Override
	public void pointerUp(float x, float y, SurfacePointerEvent event) {
		
	}

	@Override
	public void keyDown(int code) {
		if(code==Keys.SHIFT)
			mShiftPressed = true;
		if(code==Keys.CTRL)
			mCtrlPressed = true;
		
		if(code==Keys.F1) {
			if(mSurface.isInactive())
				mSurface.simulateResume();
			else
				mSurface.simulatePause();
		}
		if(code==Keys.F2)
			DebugYang.DRAW_GFX_VALUES ^= true;
		if(code==Keys.F3)
			mSurface.setPaused(!mSurface.isPaused());
		if(code==Keys.F4) {
			if(mSurface.mPlaySpeed<32)
				mSurface.mPlaySpeed *= 2;
			else{
				mSurface.mPlaySpeed = 0;
			}
		}
		if(code==Keys.F5) {
			mSurface.setPaused(false);
			mSurface.mPlaySpeed = 1;
		}
		if(code==Keys.F6) {
			if(mSurface.mPlaySpeed>0.125f*0.25f)
				mSurface.mPlaySpeed *= 0.5f;
			else if(mSurface.mPlaySpeed==0) {
				mSurface.mPlaySpeed = 32;
			}
		}
		if(code==Keys.F7) {
			mSurface.proceed();
		}
		if(code==Keys.F8) {
			if(mSurface.mGFXDebug!=null)
				DebugYang.DRAW_POINTERS ^= true;
		}
		if(code==Keys.F9) {
			if(mSurface.isStereoVision())
				mSurface.setStereoVision(0);
			else
				mSurface.setStereoVision(1024);
		}
		if(code==Keys.F11) {
			if(!mRecording) {
				mRecording = true;
				try {
					mSurface.recordMacro("macro.ym");
				} catch (FileNotFoundException e) {
					DebugYang.exception(e);
				}
			}
		}

		if(code==Keys.F12 && mSurface.mResources.fileExistsInFileSystem("macro.ym"))
			mSurface.playMacro("macro.ym");
		
		
		if(mSurface.mStereoVision!=null) {
			LensDistortionShader stereoShader = mSurface.mStereoVision.mLensDistortionShader;
			final float STEPS = 0.01f;
			if(code=='a')
				stereoShader.mScaleX += STEPS;
			if(code=='d')
				stereoShader.mScaleX -= STEPS;
			if(code=='s')
				stereoShader.mScaleY += STEPS;
			if(code=='w')
				stereoShader.mScaleY -= STEPS;
			if(code=='q')
				stereoShader.mScaleToLens += STEPS;
			if(code=='e')
				stereoShader.mScaleToLens -= STEPS;
			if(code=='y')
				mSurface.mStereoVision.mLensShift -= 0.01f;
			if(code=='x')
				mSurface.mStereoVision.mLensShift += 0.01f;
			if(code=='t')
				mSurface.mStereoVision.mInterOcularDistance += 0.002f;
			if(code=='r')
				mSurface.mStereoVision.mInterOcularDistance -= 0.002f;
			if(code=='p')
				System.out.println("scale="+stereoShader.mScaleX+":"+stereoShader.mScaleY+", scaleToLens="+stereoShader.mScaleToLens+", shift="+mSurface.mStereoVision.mLensShift+", distance="+mSurface.mStereoVision.mInterOcularDistance);
				
		}
	}

	@Override
	public void keyUp(int code) {
		if(code==Keys.SHIFT)
			mShiftPressed = false;
		if(code==Keys.CTRL)
			mCtrlPressed = false;
	}

	@Override
	public void zoom(float factor) {
		
	}

	@Override
	public void sensorChanged(YangSensorEvent event) {
		
	}

}
