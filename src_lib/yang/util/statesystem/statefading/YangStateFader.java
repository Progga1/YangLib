package yang.util.statesystem.statefading;

import yang.util.statesystem.StateSystemInterface;
import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public abstract class YangStateFader<StateSystemType extends YangProgramStateSystem> extends YangProgramState<StateSystemType> {

	public YangProgramState<StateSystemType> mFromState = null, mToState = null;
	public float mTransitionTime = 1;

	public YangStateFader(float transitionTime) {
		mTransitionTime = transitionTime;
	}

	public YangStateFader() {
		this(1);
	}

	protected abstract void refreshProgress(float deltaTime,float toWeight);
	protected abstract void prepareStateDraw(YangProgramState<StateSystemType> state,float fade);

	protected void preStateDraw() { }
	protected void postStateDraw() { }

	public YangStateFader<StateSystemType> setTargetState(YangProgramState<StateSystemType> toState) {
		mFromState = null;
		mToState = toState;
		return this;
	}

	@Override
	protected void step(float deltaTime) {
		float t = (float)(mStateTimer/mTransitionTime);
		if(t>1) {
			if(mFromState!=null)
				mFromState.mFadeProgress = 1;
			if(mToState!=null) {
				mToState.mFadeProgress = 1;
				mToState.onFadeInFinished();
			}
			mToState.getParentStateSystem().setStateNoStart(mToState);
		}else{
			refreshProgress(deltaTime,t);
		}
	}

	@Override
	protected void draw() {
		preStateDraw();
		if(mFromState!=null) {
			prepareStateDraw(mFromState,mFromState.mFadeProgress);
			mFromState.drawFrame();
		}
		if(mToState!=null) {
			prepareStateDraw(mToState,mToState.mFadeProgress);
			mToState.drawFrame();
		}
		postStateDraw();
	}

	@Override
	public void onSet(StateSystemInterface stateSystem,int layer) {
		super.onSet(stateSystem, layer);
		mFromState = getParentStateSystem().getCurrentState(getStateSystemLayer());
		if(mFromState!=null) {
			if(!mFromState.isInitialized())
				mFromState.init(mStateSystem);
			mFromState.onFadeOut();
			mFromState.mFadeProgress = 1;
		}
		if(mToState!=null) {
			if(!mToState.isInitialized())
				mToState.init(mStateSystem);
			mToState.mFadeProgress = 0;
			mToState.onSet(getParentStateSystem(),getStateSystemLayer());
		}
	}

	@Override
	public void stop() {
		if(mFromState!=null)
			mFromState.mFadeProgress = 1;
		if(mToState!=null)
			mToState.mFadeProgress = 1;
	}
}
