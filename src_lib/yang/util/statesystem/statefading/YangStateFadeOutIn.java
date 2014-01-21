package yang.util.statesystem.statefading;

import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public class YangStateFadeOutIn<StateSystemType extends YangProgramStateSystem> extends YangStateFader<StateSystemType> {

	public YangStateFadeOutIn(float transitionTime) {
		super(transitionTime);
	}

	protected void prepareFade(YangProgramState<StateSystemType> state,float fade) {
		mGraphics2D.setColorFactor(fade);
	}

	@Override
	protected void prepareStateDraw(YangProgramState<StateSystemType> state,float fadeProgress) {
		if(fadeProgress>0.5f) {
			prepareFade(state,fadeProgress*2-1);
		}
	}

	@Override
	protected void refreshProgress(float deltaTime,float fadeProgress) {
		if(fadeProgress<0.5f) {
			if(mFromState!=null) {
				mFromState.mFadeProgress = 1-fadeProgress*2;
				mFromState.proceed(deltaTime);
			}
		}else{
			if(mToState!=null) {
				mToState.mFadeProgress = fadeProgress*2-1;
				mToState.proceed(deltaTime);
			}
		}
	}



}
