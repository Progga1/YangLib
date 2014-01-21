package yang.util.statesystem;

import yang.events.eventtypes.YangEvent;
import yang.util.statesystem.statefading.YangStateFader;

public class YangSubStateMachine<StateMachineType extends YangProgramStateSystem> extends YangProgramState<StateMachineType> implements StateSystemInterface {

	public YangProgramState<?> mCurrentState;

	@Override
	@SuppressWarnings("rawtypes")
	public void setState(YangProgramState state) {
		setStateNoStart(state);
		if(state!=null)
			state.onSet(this,0);
	}


	@Override
	public void setStateNoStart(YangProgramState state) {
		if(state!=null && !state.isInitialized())
			state.init(mStateSystem);
		if(mCurrentState!=null)
			mCurrentState.stop();
		mCurrentState = state;
	}

	@Override
	public YangProgramState<?> getCurrentState(int layer) {
		return mCurrentState;
	}

	@Override
	public void fadeState(int layer,YangStateFader fader,YangProgramState toState) {
		if(!fader.isInitialized())
			fader.init(mStateSystem);
		fader.setTargetState(toState);
		fader.onSet(this,layer);
		mCurrentState = fader;
	}

	public void fadeState(YangStateFader fader,YangProgramState toState) {
		fadeState(0,fader,toState);
	}

	@Override
	public void step(float deltaTime) {
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
	public void stop() {
		if(mCurrentState!=null)
			mCurrentState.stop();
	}

	@Override
	public void resume() {
		if(mCurrentState!=null && !mCurrentState.mFirstFrame) {
			mCurrentState.resume();
		}
	}

	@Override
	public void pause() {
		if(mCurrentState!=null && !mCurrentState.mFirstFrame) {
			mCurrentState.pause();
		}
	}

	@Override
	public void onBlock() {
		if(mCurrentState!=null) {
			mCurrentState.mBlocked = true;
			mCurrentState.onBlock();
		}
	}

	@Override
	public void onUnblock() {
		if(mCurrentState!=null) {
			mCurrentState.mBlocked = false;
			mCurrentState.onUnblock();
		}
	}

}
