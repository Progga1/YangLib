package yang.util.statesystem.statefading;

import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public abstract class YangStateFade<StateSystemType extends YangProgramStateSystem> extends YangProgramState<StateSystemType> {

	public YangProgramState<StateSystemType> mFromState = null, mToState = null;
	public float mTransitionTime = 1;

	public YangStateFade(float transitionTime) {
		mTransitionTime = transitionTime;
	}

	public YangStateFade() {
		this(1);
	}

	protected abstract void refreshProgress(float deltaTime,float toWeight);

	protected void preStateDraw() { }
	protected void postStateDraw() { }

	public YangStateFade<StateSystemType> setState(YangProgramState<StateSystemType> toState) {
		mFromState = getParentStateSystem().getCurrentState(getStateSystemLayer());
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

	protected void prepareStateDraw(YangProgramState<StateSystemType> state,float fade) {
		mGraphics2D.setColorFactor(1,1,1,fade);
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
	public void start() {
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
		mFromState.mFadeProgress = 1;
		mToState.mFadeProgress = 1;
	}
}
