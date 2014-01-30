package yang.util.statesystem;

import yang.events.eventtypes.YangEvent;
import yang.graphics.defaults.DefaultSurface;
import yang.util.statesystem.statefading.YangStateFader;

public class YangProgramStateSystem extends DefaultSurface implements StateSystemInterface {

	public YangProgramState<?> mCurrentState;

	protected YangProgramStateSystem(boolean init2dGraphics, boolean init3dGraphics) {
		super(init2dGraphics, init3dGraphics);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setState(YangProgramState newState) {
		setStateNoStart(0, newState);
		mCurrentState.onSet(this,0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setStateNoStart(int layer, YangProgramState newState) {
		if(!newState.isInitialized())
			newState.init(this);
		if(mCurrentState!=null)
			mCurrentState.stop();
		mCurrentState = newState;
	}

	@Override
	public void fadeState(int layer,YangStateFader fader,YangProgramState toState) {
		if(!fader.isInitialized())
			fader.init(this);
		if(toState==mCurrentState)
			return;
		fader.setTargetState(toState);
		fader.onSet(this,layer);
		mCurrentState = fader;
	}

	public void fadeState(YangStateFader fader,YangProgramState toState) {
		fadeState(0,fader,toState);
	}

	@Override
	public YangProgramState getCurrentState(int layer) {
		return mCurrentState;
	}

	public YangProgramState<?> getCurrentState() {
		return mCurrentState;
	}

	@Override
	public void step(float deltaTime) {
		super.step(deltaTime);
		if(mCurrentState!=null)
			mCurrentState.proceed(deltaTime);
	}

	@Override
	public void preDraw() {
		if(mCurrentState!=null) {
			mCurrentState.preDrawFrame();
		}
	}

	@Override
	public void draw() {
		if(mCurrentState!=null) {
			mCurrentState.drawFrame();
		}
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		if(mCurrentState!=null && !mCurrentState.mFirstFrame) {
			event.handle(mCurrentState);
			return false;
		}else
			return false;
	}

	@Override
	public void resume() {
		super.resume();
		if(mCurrentState!=null && !mCurrentState.mFirstFrame) {
			mCurrentState.resume();
		}
	}

	@Override
	public void exit() {
		if(mCurrentState!=null)
			mCurrentState.stop();
		super.exit();
	}

	@Override
	public void pause() {
		super.pause();
		if(mCurrentState!=null && !mCurrentState.mFirstFrame) {
			mCurrentState.pause();
		}
	}

}
