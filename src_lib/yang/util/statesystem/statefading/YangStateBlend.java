package yang.util.statesystem.statefading;

import yang.util.statesystem.YangProgramStateSystem;

public class YangStateBlend<StateSystemType extends YangProgramStateSystem> extends YangStateFade<StateSystemType> {

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
