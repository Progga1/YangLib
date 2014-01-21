package yang.util.statesystem.statefading;

import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public class YangStateBlend<StateSystemType extends YangProgramStateSystem> extends YangStateFade<StateSystemType> {

	public YangStateBlend(float transitionTime) {
		super(transitionTime);
	}

	@Override
	protected void prepareStateDraw(YangProgramState<StateSystemType> state,float fade) {
		if(mFromState!=null)
			mGraphics2D.setColorFactor(1,1,1,fade);
		else
			mGraphics2D.setColorFactor(fade);
	}

	@Override
	protected void refreshProgress(float deltaTime,float toWeight) {
		if(mFromState!=null) {
			mFromState.mFadeProgress = 1-toWeight;
			mFromState.proceed(deltaTime);
		}
		if(mToState!=null) {
			mToState.mFadeProgress = toWeight;
			mToState.proceed(deltaTime);
		}
	}



}
