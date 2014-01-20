package yang.util.statesystem;

public class YangStateFade<StateSystemType extends YangProgramStateSystem> extends YangProgramState<StateSystemType> {

	public YangProgramState<StateSystemType> mFromState = null, mToState = null;
	public float mTransitionTime = 1;

	public YangStateFade(float transitionTime) {
		mTransitionTime = transitionTime;
	}

	protected void preStateDraw() { }
	protected void postStateDraw() { }

	public YangStateFade<StateSystemType> setStates(YangProgramState<StateSystemType> fromState,YangProgramState<StateSystemType> toState) {
		mFromState = fromState;
		if(fromState!=null && !fromState.isInitialized())
			fromState.init(mStateSystem);
		mToState = toState;
		if(toState!=null && !toState.isInitialized())
			toState.init(mStateSystem);
		return this;
	}

	@Override
	protected void step(float deltaTime) {
		float t = (float)(mStateTimer/mTransitionTime);
		if(t>1) {
			mFromState.mFadeProgress = 1;
			mToState.mFadeProgress = 1;
			mToState.onFadeInFinished();
			mStateSystem.setStateNoStart(mToState);
		}else{
			if(mFromState!=null) {
				mFromState.mFadeProgress = 1-t;
				mFromState.step(deltaTime);
			}
			if(mToState!=null) {
				mToState.mFadeProgress = t;
				mToState.step(deltaTime);
			}
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
		if(mFromState!=null) {
			mFromState.onFadeOut();
			mFromState.mFadeProgress = 1;
		}
		if(mToState!=null) {
			mToState.mFadeProgress = 0;
			mToState.start();
		}
	}

	@Override
	public void stop() {
		mFromState.mFadeProgress = 1;
		mToState.mFadeProgress = 1;
	}
}
