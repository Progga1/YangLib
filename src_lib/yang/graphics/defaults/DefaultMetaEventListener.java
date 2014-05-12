package yang.graphics.defaults;


import java.io.FileNotFoundException;

import yang.events.Keys;
import yang.events.YangEventQueue;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangSensorEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.stereovision.LensDistortionShader;
import yang.graphics.util.EulerOrientation;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.model.DebugYang;
import yang.surface.YangSurface;

public class DefaultMetaEventListener implements YangEventListener {

	public YangSurface mSurface;
	private boolean mRecording = false;
	private boolean mCtrlPressed = false;
	private boolean mShiftPressed = false;
	private final EulerOrientation mOrientation = new EulerOrientation();
	public int mMetaBaseKey = Keys.F1;

	public DefaultMetaEventListener(YangSurface surface,int metaBaseKey) {
		mSurface = surface;
		mMetaBaseKey = metaBaseKey;
		initMetaKeys();
	}

	protected void initMetaKeys() {
		final YangEventQueue eventQueue = mSurface.mEventQueue;
		eventQueue.setMetaKeys(mMetaBaseKey,12);
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		return false;
	}

	@Override
	public void pointerDown(float x, float y, SurfacePointerEvent event) {
		if(mCtrlPressed || mShiftPressed) {
			if(event.mButton==SurfacePointerEvent.BUTTON_MIDDLE) {
				mOrientation.reset();
				mSurface.mGraphics.setDebugPostCameraOrientation(mOrientation.getUpdatedMatrix());
				mSurface.mGraphics.setDebugPostCameraPosition(Point3f.ZERO);
				mSurface.mGraphics.setDebugPostCameraEnabled(false);
			}
		}
	}

	@Override
	public void pointerMoved(float x, float y, SurfacePointerEvent event) {

	}

	@Override
	public void pointerDragged(float x, float y, SurfacePointerEvent event) {
		if(mCtrlPressed || mShiftPressed) {
			mSurface.mGraphics.setDebugPostCameraEnabled(true);
			float MOVE_SCALE = 1;

			if(event.mButton==SurfacePointerEvent.BUTTON_LEFT) {
				if(mShiftPressed) {
					mTempVec.set(-event.mDeltaX,-event.mDeltaY,-event.mDeltaZ);
				}else if(mCtrlPressed) {
					mTempVec.set(-event.mDeltaX,0,event.mDeltaY);
				}else
					return;
				mTempVec.scale(MOVE_SCALE);
				mOrientation.getUpdatedMatrix().apply3D(mTempVec,mResVec);
				mSurface.mGraphics.moveDebugPostCamera(mResVec, null);
			}

			if(event.mButton==SurfacePointerEvent.BUTTON_RIGHT) {
				mSurface.mGraphics.setDebugPostCameraEnabled(true);
				if(mShiftPressed) {
					mOrientation.mYaw -= event.mDeltaX*MOVE_SCALE;
					mOrientation.mPitch += event.mDeltaY*MOVE_SCALE;
				}else if(mCtrlPressed) {
					mOrientation.mRoll -= event.mDeltaX*MOVE_SCALE;
					mOrientation.mPitch += event.mDeltaY*MOVE_SCALE;
				}else
					return;
				mSurface.mGraphics.setDebugPostCameraOrientation(mOrientation.getUpdatedQuaternion());
			}
		}


	}

	@Override
	public void pointerUp(float x, float y, SurfacePointerEvent event) {

	}

	private Vector3f mTempVec = new Vector3f();
	private Vector3f mResVec = new Vector3f();

	@Override
	public void keyDown(int code) {
		if(code==Keys.SHIFT)
			mShiftPressed = true;
		if(code==Keys.CTRL)
			mCtrlPressed = true;

		int base = mMetaBaseKey-1;
		if(code==base+1) {
			if(mSurface.isInactive())
				mSurface.simulateResume();
			else
				mSurface.simulateStop();
		}
		if(code==base+2)
			DebugYang.DRAW_GFX_VALUES ^= true;
		if(code==base+3) {
			mSurface.setPaused(!mSurface.isPaused());
//			if(mSurface.isInactive())
//				mSurface.simulateResume();
//			else
//				mSurface.simulatePause();
		}
		if(code==base+4) {
			if(mSurface.mPlaySpeed<32)
				mSurface.mPlaySpeed *= 2;
			else{
				mSurface.mPlaySpeed = 0;
			}
		}
		if(code==base+5) {
			mSurface.setPaused(false);
			mSurface.mPlaySpeed = 1;
			mSurface.mFastForwardToTime = -1;
		}
		if(code==base+6) {
			if(mSurface.mPlaySpeed>0.125f*0.25f)
				mSurface.mPlaySpeed *= 0.5f;
			else if(mSurface.mPlaySpeed==0) {
				mSurface.mPlaySpeed = 32;
			}
		}
		if(code==base+7) {
			mSurface.proceed();
		}
		if(code==base+8) {
			if(mSurface.mGFXDebug!=null) {
				DebugYang.DRAW_POINTERS ^= true;

			}
		}
		if(code==base+9) {
			if(mSurface.isStereoVision())
				mSurface.setStereoVision(0);
			else
				mSurface.setStereoVision(1024);
		}
		if(code==base+10) {
			mSurface.stopMacro();
		}
		if(code==base+11) {
			if(!mRecording) {
				mRecording = true;
				try {
					mSurface.recordMacro("macro.ym");
				} catch (final FileNotFoundException e) {
					DebugYang.exception(e);
				}
			}
		}

		if(code==base+12 && mSurface.mResources.fileExistsInFileSystem("macro.ym"))
			mSurface.playMacro("macro.ym");


		if(mSurface.mStereoVision!=null) {
			final LensDistortionShader stereoShader = mSurface.mStereoVision.mLensDistortionShader;
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
				mSurface.mStereoVision.setInterOcularDistance(mSurface.mStereoVision.getInterOcularDistance()+0.002f);
			if(code=='r')
				mSurface.mStereoVision.setInterOcularDistance(mSurface.mStereoVision.getInterOcularDistance()-0.002f);
			if(code=='p')
				System.out.println("scale="+stereoShader.mScaleX+":"+stereoShader.mScaleY+", scaleToLens="+stereoShader.mScaleToLens+", shift="+mSurface.mStereoVision.mLensShift+", distance="+mSurface.mStereoVision.getInterOcularDistance());

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
		mTempVec.set(0,0,factor);
		mOrientation.getUpdatedMatrix().apply3D(mTempVec,mResVec);
		mSurface.mGraphics.setDebugPostCameraEnabled(true);
		mSurface.mGraphics.moveDebugPostCamera(mResVec, null);
	}

	@Override
	public void sensorChanged(YangSensorEvent event) {

	}

}
